// Contains the implementation of a LSP server.

package lsp

import "errors"
import "strconv"
import "github.com/cmu440/lspnet"
import "container/list"
import "encoding/json"
import "fmt"
import "time"

type msgPackage struct {
	msg  *Message
	addr *lspnet.UDPAddr
}

type abstractClient struct {
	addr         *lspnet.UDPAddr
	nextSN       int
	nextDataRead int
	// window size variable
	// server send msg to client
	nextSmallestAck int
	mapAck          map[int]bool
	mapNeedAck      map[int]*Message

	// server receive msg from client
	nextSmallestData int
	mapReceived      map[int]*Message

	// variables of epoch
	epochNum int
	lostConn bool

	// close
	closeConn bool
}

type requestRead struct {
	ask      chan int
	response chan *Message
}

type requestWrite struct {
	ask      chan []byte
	connId   chan int
	response chan error
}

type requestClose struct {
	ask chan int
	//response chan int
	getError chan error
}

type requestCloseConn struct {
	ask      chan int
	getError chan error
}

type server struct {
	nextConnectId      int
	clients            map[int]*abstractClient
	conn               *lspnet.UDPConn
	readFromClientChan chan *msgPackage
	writeToClientChan  chan *Message
	readRequest        *requestRead
	writeRequest       *requestWrite
	readList           *list.List
	writeList          *list.List

	flag bool

	// variables for window size
	windowSize  int
	mapNeedSend *list.List

	// variables for epoch
	epochChan   chan int
	epochMillis int
	epochLimit  int

	// close
	deleteClient     chan int
	closeConnRequest *requestCloseConn

	// close every thing
	waitToWriteFinish bool
	writeFinished     chan int
	waitToAckFinish   bool
	ackFinished       chan int
	closeRead         chan int
	closeEpoch        chan int
	closeEvent        chan int

	// close connection
	closeConn chan int
}

// NewServer creates, initiates, and returns a new server. This function should
// NOT block. Instead, it should spawn one or more goroutines (to handle things
// like accepting incoming client connections, triggering epoch events at
// fixed intervals, synchronizing events using a for-select loop like you saw in
// project 0, etc.) and immediately return. It should return a non-nil error if
// there was an error resolving or listening on the specified port number.
func NewServer(port int, params *Params) (Server, error) {
	s := &server{
		nextConnectId:      1,
		clients:            make(map[int]*abstractClient),
		readFromClientChan: make(chan *msgPackage),
		writeToClientChan:  make(chan *Message),
		readRequest: &requestRead{
			ask:      make(chan int),
			response: make(chan *Message),
		},
		writeRequest: &requestWrite{
			ask:      make(chan []byte),
			connId:   make(chan int),
			response: make(chan error),
		},
		readList:  list.New(),
		writeList: list.New(),

		flag: false,

		// variables for window size
		windowSize:  params.WindowSize,
		mapNeedSend: list.New(),

		// variables for epoch
		epochChan:   make(chan int),
		epochMillis: params.EpochMillis,
		epochLimit:  params.EpochLimit,

		// close
		deleteClient: make(chan int),
		closeConnRequest: &requestCloseConn{
			ask:      make(chan int),
			getError: make(chan error),
		},
		waitToWriteFinish: false,
		writeFinished:     make(chan int),
		waitToAckFinish:   false,
		ackFinished:       make(chan int),
		closeRead:         make(chan int, 1),
		closeEpoch:        make(chan int, 1),
		closeEvent:        make(chan int, 1),

		// close conn
		closeConn: make(chan int, 1),
	}

	// start server
	addr, err := lspnet.ResolveUDPAddr("udp", "localhost:"+strconv.Itoa(port))
	if err != nil {
		return nil, err
	}

	conn, err := lspnet.ListenUDP("udp", addr)
	if err != nil {
		return nil, err
	}

	s.conn = conn
	go s.readMessage()
	go s.handleMessage()
	go s.epochFire()

	fmt.Println("new server")
	return s, nil
}

func (s *server) epochFire() {
	for {
		select {
		case <-s.closeEpoch:
			return
		default:
			////fmt.Println("Server EpochFire!!!!!!!!!!!!!!!!!!!!!!!!!!")
			<-time.After(time.Duration(s.epochMillis) * time.Millisecond)
			s.epochChan <- 1
		}

	}
}

func (s *server) handleMessage() {
	for {
		select {
		case <-s.writeFinished:
			if s.writeList.Len() == 0 {
				s.writeFinished <- 1
			} else {
				s.waitToWriteFinish = true
			}

		case <-s.ackFinished:
			//fmt.Println("Server 192: receive ack finished")
			s.checkAckFinished()
		case msg := <-s.readFromClientChan:
			////fmt.Println("Server 162: read from client: ", msg)
			s.readFromClient(msg)
		case msg := <-s.writeToClientChan:
			////fmt.Println("Server 165: write to client: ", msg)
			if s.clients[msg.ConnID] == nil {
				continue
			}
			if msg.Type == MsgData {
				s.clients[msg.ConnID].mapAck[msg.SeqNum] = false
				s.clients[msg.ConnID].mapNeedAck[msg.SeqNum] = msg
				// data type: need to consider order
				if (msg.SeqNum - s.clients[msg.ConnID].nextSmallestAck) < s.windowSize {
					s.writeToClient(msg)
				} else {
					s.writeList.PushBack(msg)
				}
			} else {
				// ack/conn type: don't need to consider order
				s.writeToClient(msg)
			}

			if s.waitToWriteFinish && s.writeList.Len() == 0 {
				s.writeFinished <- 1
				s.waitToWriteFinish = false
			}
		case <-s.readRequest.ask:
			s.handleReadRequest()
		case payload := <-s.writeRequest.ask:
			connId := <-s.writeRequest.connId
			var response = s.handleWriteRequest(connId, payload)
			s.writeRequest.response <- response
		case <-s.epochChan:
			s.addEpochNum()
			s.handleEpochEvent()
		case id := <-s.deleteClient:
			delete(s.clients, id)
		case id := <-s.closeConnRequest.ask:
			if s.clients[id] == nil {
				s.closeConnRequest.getError <- errors.New("No client")
			}
			s.sendDeadMsg(id)
			s.closeConnRequest.getError <- nil

		}
	}
}

func (s *server) checkAckFinished() {
	for _, c := range s.clients {
		if len(c.mapAck) != 0 {
			s.waitToAckFinish = true
			//fmt.Println("Server: need to wait ack finish")
			return
		}
	}
	s.ackFinished <- 1
}

func (s *server) handleWriteRequest(connID int, payload []byte) error {
	//////fmt.Println(connID)
	client := s.clients[connID]
	dataMsg := NewData(connID, client.nextSN, payload, payload)
	////fmt.Println("Server: 197 write ", dataMsg)
	go s.sendData(dataMsg)
	client.nextSN += 1
	return nil
}

func (s *server) sendData(dataMsg *Message) {
	s.writeToClientChan <- dataMsg
}

func (s *server) addEpochNum() {
	for connId, c := range s.clients {
		c.epochNum += 1
		if c.epochNum >= s.epochLimit {
			c.lostConn = true
			s.sendDeadMsg(connId)
		}
	}
}

func (s *server) sendDeadMsg(connId int) {
	dataMsg := NewData(connId, s.clients[connId].nextSN, nil, nil)
	if s.flag {
		s.readRequest.response <- dataMsg
		s.flag = false
	} else {
		s.readList.PushBack(dataMsg)
	}
}

func (s *server) readData(dataMsg *msgPackage) {
	s.readFromClient(dataMsg)
}

func (s *server) handleEpochEvent() {
	for connID, c := range s.clients {
		/*
		 * if no data received from the client, then resend ack for connection requestRead
		 */
		if c.nextSmallestAck == 1 {
			ack := NewAck(connID, 0)
			go s.sendAck(ack)
		}

		/*
		 * sent but not acked, resend the data
		 */
		for key, received := range c.mapAck {

			if !received {
				msg := c.mapNeedAck[key]
				fmt.Println("Server epoch: ", msg)
				s.writeToClient(msg)
			}
		}
	}

}

func (s *server) handleReadRequest() {
	msg := s.getMessageFromReadList()
	if msg != nil {
		//////fmt.Println("101    no message")
		/*elm := s.readList.Front()
		msg := elm.Value.(*Message)*/
		s.readRequest.response <- msg
	} else {
		s.flag = true
	}
	return
}

// find next msg that can be send
// return nil if no msg can be send
func (s *server) getMessageFromReadList() *Message {
	for e := s.readList.Front(); e != nil; e = e.Next() {
		msg := e.Value.(*Message)
		if msg.SeqNum == s.clients[msg.ConnID].nextDataRead {
			s.clients[msg.ConnID].nextDataRead += 1
			s.readList.Remove(e)
			return msg
		}
	}
	return nil
}

func (s *server) writeToClient(msg *Message) error {
	connId := msg.ConnID
	addr := s.clients[connId].addr
	mmsg, _ := json.Marshal(msg)
	fmt.Println("364 Server write to client: ", msg)
	_, err := s.conn.WriteToUDP(mmsg, addr)
	if err != nil {
		fmt.Println("364: ", err)
	}

	return err
}

func (s *server) readFromClient(msgPack *msgPackage) {
	//////fmt.Println("129 read")
	msg := msgPack.msg
	addr := msgPack.addr

	////fmt.Println("server read: ", msg)
	if msg.Type == MsgConnect {
		s.connectClient(msg, addr)
	} else {
		// set epoch number to 0
		if s.clients[msg.ConnID] != nil {
			s.clients[msg.ConnID].epochNum = 0
		}
		if msg.Type == MsgData {
			if !s.waitToAckFinish {
				//fmt.Println("Server read from client: ", msg)
				s.receiveData(msg)
			}
		} else if msg.Type == MsgAck {
			fmt.Println("389 Server: receive ack: ", msg)
			s.receiveAck(msg)
		}

		if s.waitToAckFinish {
			s.checkAckFinished()
		}
	}
	return
}

func (s *server) alreadyHasClient(addr *lspnet.UDPAddr) bool {
	////fmt.Println("server: in already has client")
	for _, client := range s.clients {
		if client.addr.String() == addr.String() {
			return true
		}
	}
	return false
}

func (s *server) receiveData(msg *Message) {
	//fmt.Println("Server read: ", msg)
	c := s.clients[msg.ConnID]
	// check duplicated forst and within window size

	/*if c.nextSmallestData+s.windowSize <= msg.SeqNum {
		// check within window size, drop it if not
		return
	} else if c.mapReceived[msg.SeqNum] != nil {
		// check duplicated outside of window size
		return
	}*/

	//fmt.Println("pass window size")

	// here is no duplicated
	// decide should we push it to the readlist or chan
	if s.flag && (s.clients[msg.ConnID].nextDataRead == msg.SeqNum) {
		s.readRequest.response <- msg
		c.nextDataRead += 1
		s.flag = false
	} else if msg.SeqNum >= c.nextDataRead {
		s.readList.PushBack(msg)
	}

	// create ack message
	connID := msg.ConnID
	seqNum := msg.SeqNum
	ack := NewAck(connID, seqNum)

	// check the receive variables in client
	// delete the message in mapreceived
	if msg.SeqNum == c.nextSmallestData {
		c.nextSmallestData += 1

		for c.mapReceived[c.nextSmallestData] != nil {
			delete(c.mapReceived, c.nextSmallestData)
			c.nextSmallestData += 1
		}

	} else {
		c.mapReceived[msg.SeqNum] = msg
	}
	go s.sendAck(ack)

	return
}

func (s *server) sendAck(ack *Message) {
	s.writeToClientChan <- ack
}

func (s *server) receiveAck(msg *Message) {
	//fmt.Println("server: receiveAck")
	c := s.clients[msg.ConnID]
	if msg.SeqNum >= c.nextSmallestAck {
		c.mapAck[msg.SeqNum] = true
	}
	if msg.SeqNum == c.nextSmallestAck {
		// reset the next smallest ack numebr
		value, exist := c.mapAck[c.nextSmallestAck]

		for exist && value {

			delete(c.mapAck, c.nextSmallestAck)
			c.nextSmallestAck += 1

			value, exist = c.mapAck[c.nextSmallestAck]
		}
		var l = *list.New()

		for element := s.writeList.Front(); element != nil; element = element.Next() {
			message := element.Value.(*Message)
			if message.ConnID == msg.ConnID {
				if (message.SeqNum - c.nextSmallestAck) < s.windowSize {
					s.writeToClient(message)
					l.PushBack(element)
				}
			}

		}
		////fmt.Println("loop finish go out of write")

		for element := l.Front(); element != nil; element = element.Next() {
			s.writeList.Remove(element)
		}
		////fmt.Println("go out of delte dfadfasdf")

		if s.waitToWriteFinish && s.writeList.Len() == 0 {
			s.writeFinished <- 1
			s.waitToWriteFinish = false
		}
		////fmt.Println("go  asdfasdfe dfadfasdf")
	}
	return
}

func (s *server) connectClient(msg *Message, addr *lspnet.UDPAddr) {
	////fmt.Println("Server: connect client ", msg)
	// check duplication of connection
	if s.alreadyHasClient(addr) {
		////fmt.Println("already has client")
		return
	}

	connID := s.nextConnectId
	newClient := abstractClient{
		addr:         addr,
		nextSN:       1,
		nextDataRead: 1,
		// server send msg to client
		nextSmallestAck: 1,
		mapAck:          make(map[int]bool),
		mapNeedAck:      make(map[int]*Message),

		// server receive msg from client
		nextSmallestData: 1,
		mapReceived:      make(map[int]*Message),

		// epoch
		epochNum: 0,
		lostConn: false,

		// close
		closeConn: false,
	}
	s.clients[connID] = &newClient
	s.nextConnectId += 1

	ack := NewAck(connID, 0)
	////fmt.Println("Server: send ack to client")
	go s.sendAck(ack)
	return
}

func (s *server) readMessage() {
	var b [1500]byte
	//////fmt.Println("Read: read message")
	for {
		select {
		case <-s.closeRead:
			return
		default:
			var msg Message

			// unmarshal
			n, addr, err := s.conn.ReadFromUDP(b[0:])
			err = json.Unmarshal(b[0:n], &msg)
			if err == nil {
				msgPck := &msgPackage{
					msg:  &msg,
					addr: addr,
				}
				//fmt.Println("server read: ", &msg)
				s.readFromClientChan <- msgPck
			} else {
				//fmt.Println(err)
				return
			}
		}

	}
}
func (s *server) Read() (int, []byte, error) {
	s.readRequest.ask <- 1
	msg := <-s.readRequest.response
	////fmt.Println("Server called read: ", msg)
	// check if the client still alive or not
	if msg.Payload == nil {
		////fmt.Println("client closed when read")
		s.deleteClient <- msg.ConnID
		//fmt.Println("read ")
		return msg.ConnID, nil, errors.New("Client connection lost")
	}
	// TODO: client lost return connection id
	return msg.ConnID, msg.Payload, nil
}

func (s *server) Write(connID int, payload []byte) error {
	//fmt.Println("server called write")
	/*client := s.clients[connID]
	dataMsg := NewData(connID, client.nextSN, payload, payload)
	////fmt.Println("Server: 197 write ", dataMsg)
	s.writeToClientChan <- dataMsg
	client.nextSN += 1*/

	//////fmt.Println(s.clients[connID])
	if s.clients[connID] == nil {
		return errors.New("Client connection lost")
	}

	s.writeRequest.ask <- payload
	s.writeRequest.connId <- connID
	response := <-s.writeRequest.response

	return response
}

func (s *server) CloseConn(connID int) error {
	//fmt.Println("server closeconn!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
	s.closeConnRequest.ask <- connID
	err := <-s.closeConnRequest.getError
	//fmt.Println("server closed finish!!!!!!!!!!!!!!!!!!!!!!")
	return err
}

func (s *server) Close() error {
	return errors.New("text")
	//fmt.Println("server: server close !!!!!!!!!!!!!!!!!!!!!!!!!!!!")
	s.writeFinished <- 0
	//fmt.Println("Server: finish wirte1")
	<-s.writeFinished
	//fmt.Println("Server: finish wirte")
	/*s.ackFinished <- 0
	<-s.ackFinished*/
	//fmt.Println("Server: finish ack")

	s.closeEpoch <- 1
	s.closeRead <- 1
	s.closeEvent <- 1

	//fmt.Println("Server: close every thing")

	err := s.conn.Close()
	if err == nil {
		return nil
	} else {
		return err
	}

	return errors.New("not yet implemented")
}
