// Contains the implementation of a LSP client.

package lsp

import "errors"
import "github.com/cmu440/lspnet"

//import "container/list"
import "encoding/json"
import "fmt"
import "time"

const TAG = "################"

type client struct {
	nextSN             int
	connID             int
	addr               *lspnet.UDPAddr
	conn               *lspnet.UDPConn
	readFromServerChan chan *Message
	writeToServerChan  chan *Message
	readRequest        *requestRead
	writeRequest       *requestWrite
	readMap            map[int]*Message
	writeMap           map[int]*Message
	connect            bool

	connected  chan int
	serverAddr *lspnet.UDPAddr

	flag bool

	// variables for window size
	windowSize int
	// send to server data
	nextSmallestAck int
	mapAck          map[int]bool
	mapNeedAck      map[int]*Message

	// receive from server data
	nextSmallestData int
	// TODO: didn't need this
	mapAcked     map[int]bool
	mapReceived  map[int]*Message
	nextDataRead int

	// variables for epoch
	epochChan   chan int
	epochMillis int
	epochLimit  int
	epochNum    int
	lostConn    bool

	// close
	closeRequest      *requestClose
	waitToWriteFinish bool
	writeFinished     chan int
	waitToAckFinish   bool
	ackFinished       chan int
	closeRead         chan int
	closeEpoch        chan int
	closeEvent        chan int
}

// NewClient creates, initiates, and returns a new client. This function
// should return after a connection with the server has been established
// (i.e., the client has received an Ack message from the server in response
// to its connection request), and should return a non-nil error if a
// connection could not be made (i.e., if after K epochs, the client still
// hasn't received an Ack message from the server in response to its K
// connection requests).
//
// hostport is a colon-separated string identifying the server's host address
// and port number (i.e., "localhost:9999").
func NewClient(hostport string, params *Params) (Client, error) {
	c := &client{
		nextSN:             1,
		writeToServerChan:  make(chan *Message),
		readFromServerChan: make(chan *Message),
		readRequest: &requestRead{
			ask:      make(chan int),
			response: make(chan *Message),
		},
		writeRequest: &requestWrite{
			ask:      make(chan []byte),
			response: make(chan error),
		},
		readMap: make(map[int]*Message),
		// write list has order
		writeMap:  make(map[int]*Message),
		connected: make(chan int),
		flag:      false,
		connect:   false,

		// variables for window size
		windowSize: params.WindowSize,
		// send to client data
		nextSmallestAck: 1,
		mapAck:          make(map[int]bool),
		mapNeedAck:      make(map[int]*Message),

		// receive from client data
		nextSmallestData: 1,
		// TODO: didn't need this
		mapAcked:     make(map[int]bool),
		mapReceived:  make(map[int]*Message),
		nextDataRead: 1,

		// epoch variables
		epochChan:   make(chan int),
		epochMillis: params.EpochMillis,
		epochLimit:  params.EpochLimit,
		epochNum:    0,
		lostConn:    false,

		// clsoe
		closeRequest: &requestClose{
			ask: make(chan int),
			//response: make(chan int),
			getError: make(chan error),
		},
		waitToWriteFinish: false,
		writeFinished:     make(chan int),
		waitToAckFinish:   false,
		ackFinished:       make(chan int),
		closeRead:         make(chan int, 1),
		closeEpoch:        make(chan int, 1),
		closeEvent:        make(chan int, 1),
	}

	// connect to server
	addr, err := lspnet.ResolveUDPAddr("udp", hostport)

	if err != nil {
		return nil, err
	}

	conn, err := lspnet.DialUDP("udp", nil, addr)
	if err != nil {
		return nil, err
	}
	fmt.Println("new client")

	c.conn = conn
	c.addr = addr
	fmt.Println("70")

	////fmt.Println("74")

	go c.readMessage()
	go c.handleMessage()
	go c.epochFire()

	msg := NewConnect()
	c.writeToServerChan <- msg

	<-c.connected
	//fmt.Println("Client 156: connect success")

	return c, nil
}

func (c *client) sendConn() {
	// send connection mesage to server
	msg := NewConnect()
	////fmt.Println("Client 164: new connection")
	c.writeToServerChan <- msg

	////fmt.Println("Client 167: new connection finished")
}

func (c *client) epochFire() {
	for {
		select {
		case i := <-c.closeEpoch:
			c.closeEpoch <- i
			return
		default:
			<-time.After(time.Duration(c.epochMillis) * time.Millisecond)
			c.epochChan <- 1

		}

	}
}

func (c *client) handleMessage() {
	for {
		select {
		case <-c.writeFinished:
			//fmt.Println("need to wirte finish")
			if len(c.writeMap) == 0 {
				c.writeFinished <- 1
			} else {
				c.waitToWriteFinish = true
			}
		case <-c.ackFinished:
			if len(c.mapAck) == 0 {
				c.ackFinished <- 1
			} else {
				c.waitToAckFinish = true
			}

		case msg := <-c.readFromServerChan:
			//fmt.Println("Client 208: read msg: ", msg)
			c.epochNum = 0
			c.readFromServer(msg)
		case msg := <-c.writeToServerChan:
			//fmt.Println("Client 212: write to server: ", msg)
			if msg.Type == MsgData {
				// data type: need to consider order
				c.mapAck[msg.SeqNum] = false
				c.mapNeedAck[msg.SeqNum] = msg
				if (msg.SeqNum - c.nextSmallestAck) < c.windowSize {
					c.writeToServer(msg)
				} else {
					c.writeMap[msg.SeqNum] = msg
				}

			} else {
				// ack/conn type: don't need to consider order
				c.writeToServer(msg)
			}

			// check is it waiting for wirting to finished?
			if c.waitToWriteFinish && len(c.writeMap) == 0 {
				c.writeFinished <- 1
				c.waitToWriteFinish = false
			}

		case <-c.readRequest.ask:
			c.handleReadRequest()
		case payload := <-c.writeRequest.ask:
			var response = c.handleWriteRequest(payload)
			c.writeRequest.response <- response
		case <-c.epochChan:
			c.epochNum += 1
			if c.epochNum == c.epochLimit {
				c.lostConn = true
				c.lostConnection()
				dataMsg := NewData(c.connID, c.nextSN, nil, nil)
				if c.flag {
					c.readRequest.response <- dataMsg
					c.flag = false
				} else {
					//c.readMap[c.nextSN] = dataMsg
				}
				return
			}
			c.handleEpochEvent()
		case <-c.closeEvent:
			dataMsg := NewData(c.connID, c.nextSN, nil, nil)
			if c.flag {
				c.readRequest.response <- dataMsg
				c.flag = false
			} else {
				//c.readMap[c.nextSN] = dataMsg
			}
			c.closeEvent <- 1
			return

		}
	}
}

func (c *client) lostConnection() {
	/*c.closeRequest.ask <- 0
	response := <-c.closeRequest.ask
	for response != 2 {
		response = <-c.closeRequest.ask
	}*/

	c.closeEpoch <- 1
	<-c.closeEpoch

	c.closeRead <- 1
	<-c.closeRead

	c.conn.Close()
	return
}

func (c *client) handleWriteRequest(payload []byte) error {
	if c.lostConn {
		return errors.New("lost connection")
	} else {
		dataMsg := NewData(c.connID, c.nextSN, payload, payload)

		go c.sendData(dataMsg)

		c.nextSN += 1
		return nil
	}
}

func (c *client) sendData(dataMsg *Message) {
	c.writeToServerChan <- dataMsg
}

func (c *client) handleEpochEvent() {
	// TODO: do we have to check if we send the connection msg yet?

	/*
	 * if clients connection request has not yet been acknowledged
	 * resend the connection request
	 */
	if !c.connect {
		//fmt.Println("send connect again")
		go c.readMessage()
		go c.sendConn()

	}

	/*
	 * cibbectuib acknowledged but no data messages received
	 * send acknowledgment with sn = 0
	 */
	if c.connect && c.nextSN == 1 && len(c.writeMap) == 0 {
		ack := NewAck(c.connID, 0)
		go c.sendAck(ack)
	}

	/*
	 * for each data message that has been sent but not yet
	 * acked, resend data
	 */
	for key, received := range c.mapAck {
		if !received {
			msg := c.mapNeedAck[key]
			c.writeToServer(msg)
		}
	}
}

func (c *client) handleReadRequest() {
	if c.readMap[c.nextDataRead] != nil {
		msg := c.readMap[c.nextDataRead]
		c.readRequest.response <- msg
		delete(c.readMap, c.nextDataRead)
		c.nextDataRead += 1
	} else {
		c.flag = true
	}
	return
}

func (c *client) writeToServer(msg *Message) {
	//fmt.Println("Client write: ", msg)
	mmsg, _ := json.Marshal(msg)
	_, err := c.conn.Write(mmsg)
	if err != nil {
		fmt.Println(err)
	}
}

func (c *client) readFromServer(msg *Message) {

	////fmt.Println("129Client: read from server")
	if msg.Type == MsgData {
		if !c.waitToAckFinish {
			c.reveiveData(msg)
		}
	} else if msg.Type == MsgAck {
		//fmt.Println("138 Client: receive ack")
		c.receiveAck(msg)
	}

	// if need to close, see if all the data sended is acked or not
	if c.waitToAckFinish && len(c.mapAck) == 0 {
		c.ackFinished <- 1
		c.waitToAckFinish = false
	}

	return
}

func (c *client) reveiveData(msg *Message) {
	// check duplicated first and within window size
	/*if msg.SeqNum < c.nextSmallestData {
		// check duplicated outside of window size
		return
	} else */

	/*if c.nextSmallestData+c.windowSize <= msg.SeqNum {
		fmt.Printf("msgData\t%d\t%d out of order\n", c.connID, msg.SeqNum)
		// check within window size, drop it if not
		return
	} else if c.mapReceived[msg.SeqNum] != nil {
		fmt.Printf("msgData\t%d\t%d already exist\n", c.connID, msg.SeqNum)
		// check duplicated outside of window size
		return
	}*/

	// here is no duplicated
	// decide should we push it to the readlsit or chan
	if c.flag && (msg.SeqNum == c.nextDataRead) {
		// TODO: check if this msg has the same sequence number or not
		c.readRequest.response <- msg
		c.nextDataRead += 1
		c.flag = false
	} else if msg.SeqNum >= c.nextDataRead {
		// fmt.Printf("msgData\t%d\t%d\n", c.connID, msg.SeqNum)
		fmt.Printf("msgData\t%d\t%d into map\n", c.connID, msg.SeqNum)
		c.readMap[msg.SeqNum] = msg
	}
	// create ack message
	connID := msg.ConnID
	seqNum := msg.SeqNum
	ack := NewAck(connID, seqNum)
	// check the receive variables in client
	// delete the message in mapreceived
	if msg.SeqNum == c.nextSmallestData {

		c.nextSmallestData += 1

		//var l = *list.New()
		for c.mapReceived[c.nextSmallestData] != nil {
			//l.PushBack(c.nextSmallestData)
			delete(c.mapReceived, c.nextSmallestData)
			c.nextSmallestData += 1
		}

		// delete the message in map

	} else {
		c.mapReceived[msg.SeqNum] = msg
	}
	go c.sendAck(ack)
	return
}

func (c *client) sendAck(ack *Message) {
	fmt.Println("430 Client send ack: ", ack)
	c.writeToServerChan <- ack
}

func (c *client) receiveAck(msg *Message) {
	if msg.SeqNum == 0 {
		//fmt.Println("Client: ", msg)
		if c.connect {
			return
		}
		c.connect = true
		c.connID = msg.ConnID
		c.connected <- 1

		return
	}

	// receive ack of the data sended by the client
	c.mapAck[msg.SeqNum] = true
	delete(c.mapNeedAck, msg.SeqNum)
	if msg.SeqNum == c.nextSmallestAck {
		// reset the next smallest ack number
		value, exist := c.mapAck[c.nextSmallestAck]

		for exist && value {
			delete(c.mapAck, c.nextSmallestAck)
			c.nextSmallestAck += 1
			value, exist = c.mapAck[c.nextSmallestAck]
			// sent the stored message that can be sent now after update the smallest ack.
			message, exist := c.writeMap[c.nextSmallestAck+c.windowSize-1]
			if exist {
				c.writeToServer(message)
				delete(c.writeMap, message.SeqNum)

				if c.waitToWriteFinish && len(c.writeMap) == 0 {
					c.writeFinished <- 1
					c.waitToWriteFinish = false
				}
			}
		}
	}

	return
}

func (c *client) readMessage() error {
	var b [1500]byte
	for {
		// deal with close
		select {
		case i := <-c.closeRead:
			c.closeRead <- i
			fmt.Println("return from read message")
			return nil
		default:
			//fmt.Println("Client 471: Read ")
			var msg Message
			n, err := c.conn.Read(b[0:])
			//c.serverAddr = addr
			if err != nil {
				fmt.Println("read err, ", err)
				return err
			}

			err = json.Unmarshal(b[0:n], &msg)
			fmt.Println("Client 479: Read ", &msg, " ", c.nextSmallestData)
			if msg.Type == MsgData {
				// fmt.Printf("msgData\t%d\t%d\n", c.connID, msg.SeqNum)
				fmt.Printf("msgData\t%d\t%d read\n", c.connID, msg.SeqNum)
			}
			if err == nil {
				c.readFromServerChan <- &msg

			} else {
				fmt.Println("read err, ", err)
				return err
			}

		}

	}
}

func (c *client) ConnID() int {
	// TODO: race condition here, same with server
	return c.connID
}

func (c *client) Read() ([]byte, error) {
	// fmt.Println("client called read")
	fmt.Println(TAG, 1)
	c.readRequest.ask <- 1
	fmt.Println(TAG, 2)
	// fmt.Println("client called read send request")
	msg := <-c.readRequest.response
	fmt.Println(TAG, 3)
	// fmt.Println("Client Read get msg: ", msg, " ", c.nextDataRead)
	if msg.Payload == nil {
		fmt.Println("return error")
		return nil, errors.New("client close connection")
	}
	return msg.Payload, nil
}

func (c *client) Write(payload []byte) error {
	c.writeRequest.ask <- payload
	response := <-c.writeRequest.response

	return response
}

func (c *client) Close() error {
	////fmt.Println("Client close")
	// wait for messages to wirte
	// c.waitToWriteFinish = true
	c.writeFinished <- 0
	<-c.writeFinished
	//fmt.Println("Client write finished")
	// wait for message to acked
	c.ackFinished <- 0
	<-c.ackFinished
	//fmt.Println("Client ack finished")

	// close all the routine
	/*c.closeRequest.ask <- 0
	response := <-c.closeRequest.ask
	for response != 3 {
		//fmt.Println("494: response: ", response)
		c.closeRequest.ask <- response
		//fmt.Println("496: response: ", response)
		response = <-c.closeRequest.ask
	}*/
	c.closeEpoch <- 1
	//fmt.Println("close epoch")
	c.closeRead <- 1
	//<-c.closeEpoch
	//<-c.closeRead
	//fmt.Println("close read")
	c.closeEvent <- 1
	//<-c.closeEvent
	//fmt.Println("close event")

	err := c.conn.Close()

	//theError := <-c.closeRequest.getError
	if err == nil {
		return nil
	} else {
		return err
	}
}
