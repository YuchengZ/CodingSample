package paxos

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/cmu440-F15/paxosapp/rpc/paxosrpc"
	"net"
	"net/http"
	"net/rpc"
	"time"
)

/*******************************************
 * custom struct
 ******************************************/
type paxosNode struct {
	srvId      int
	hostMap    map[int]string
	myHostPort string
	numNodes   int
	replace    bool
	numRetries int
	connMap    map[string]*rpc.Client
	// proposal number for the proposal request of its own node
	pNumberMap map[string]int
	// accept value map, for accept phase
	vaMap map[string]interface{}
	// accept proposal map, for accept phase
	naMap map[string]int
	// proposal number, store the highest proposal number have seen
	npMap map[string]int
	// the storage.
	valueMap        map[string]interface{}
	prepareDoneChan chan *PrepareData
	acceptDoneChan  chan bool
	commitDoneChan  chan bool
}

type PrepareData struct {
	status bool
	na     int
	va     interface{}
}

// NewPaxosNode creates a new PaxosNode. This function should return only when
// all nodes have joined the ring, and should return a non-nil error if the node
// could not be started in spite of dialing the other nodes numRetries times.
//
// hostMap is a map from node IDs to their hostports, numNodes is the number
// of nodes in the ring, replace is a flag which indicates whether this node
// is a replacement for a node which failed.
func NewPaxosNode(myHostPort string, hostMap map[int]string, numNodes, srvId, numRetries int, replace bool) (PaxosNode, error) {
	fmt.Println("server id: ", srvId)
	newNode := &paxosNode{
		srvId:           srvId,
		hostMap:         hostMap,
		myHostPort:      myHostPort,
		numNodes:        numNodes,
		replace:         replace,
		numRetries:      numRetries,
		connMap:         make(map[string]*rpc.Client),
		pNumberMap:      make(map[string]int),
		vaMap:           make(map[string]interface{}),
		naMap:           make(map[string]int),
		npMap:           make(map[string]int),
		valueMap:        make(map[string]interface{}),
		prepareDoneChan: make(chan *PrepareData),
		acceptDoneChan:  make(chan bool),
		commitDoneChan:  make(chan bool),
	}

	// register the paxos node for rpc usage
	err := rpc.RegisterName("PaxosNode", paxosrpc.Wrap(newNode))
	if err != nil {
		return nil, errors.New("Errow while register.")
	}

	// running as a server for listening
	rpc.HandleHTTP()
	listener, err := net.Listen("tcp", myHostPort)
	if err != nil {
		fmt.Println("err when listen: ", listener)
		return nil, errors.New(myHostPort)
	}
	go http.Serve(listener, nil)

	// build connection to every other node
	tmpValueMap := make(map[string]interface{})
	count := 0
	succeed := true
	for count < numRetries {
		succeed = true
		for _, v := range hostMap {
			client, err := rpc.DialHTTP("tcp", v)
			if err != nil {
				count += 1
				succeed = false
				break
			}
			newNode.connMap[v] = client
		}

		if succeed {
			// for replacement usage
			if replace {
				for _, client := range newNode.connMap {
					replaceArgs := &paxosrpc.ReplaceServerArgs{srvId, myHostPort}
					replaceReply := &paxosrpc.ReplaceServerReply{}
					client.Call("PaxosNode.RecvReplaceServer", replaceArgs, replaceReply)
					replaceCatchupArgs := &paxosrpc.ReplaceCatchupArgs{}
					replaceCatchupReply := &paxosrpc.ReplaceCatchupReply{}
					client.Call("PaxosNode.RecvReplaceCatchup", replaceCatchupArgs, replaceCatchupReply)
					var nodeValueMap map[string]interface{}
					json.Unmarshal([]byte(replaceCatchupReply.Data), &nodeValueMap)
					for nodeKey, nodeValue := range nodeValueMap {
						tmpValueMap[nodeKey] = nodeValue
					}
				}
			}
			newNode.valueMap = tmpValueMap
			return newNode, nil
		}
		time.Sleep(time.Second * 1)
	}

	return nil, errors.New("cannot connect to all the nodes")
}

// This function is used to generate the next proposal number given the user input
func (pn *paxosNode) GetNextProposalNumber(args *paxosrpc.ProposalNumberArgs, reply *paxosrpc.ProposalNumberReply) error {
	fmt.Println("get number")
	key := args.Key
	pNumber, ok := pn.pNumberMap[key]
	if !ok {
		pNumber = -1
	}

	newNumber := pNumber + 1
	reply.N = newNumber*pn.numNodes + pn.srvId
	pn.pNumberMap[key] = newNumber
	fmt.Println("getnumber: ", reply.N)
	return nil
}

// This is the main function of the propose part. The whole process of a
// successful propose consists of prepare, accept and commit. It will return
// once receiving Reject reply.
func (pn *paxosNode) Propose(args *paxosrpc.ProposeArgs, reply *paxosrpc.ProposeReply) error {
	// build arguments
	timeStamp := time.After(time.Second * 15)
	prepareArgs := &paxosrpc.PrepareArgs{args.Key, args.N}
	acceptArgs := &paxosrpc.AcceptArgs{Key: args.Key, N: args.N, V: args.V}
	commitArgs := &paxosrpc.CommitArgs{Key: args.Key}

	// prepare
	go pn.DoPrepare(prepareArgs)

	/* wait for the result of prepare, accept and commit
	 * of one failure, then the proposal failure
	 */
	for {
		select {
		case prepareData := <-pn.prepareDoneChan:
			if prepareData.status {
				if prepareData.va != nil {
					acceptArgs.V = prepareData.va
				}
				go pn.DoAccept(acceptArgs)
			} else {
				return nil
			}
		case status := <-pn.acceptDoneChan:
			if status {
				commitArgs.V = acceptArgs.V
				go pn.DoCommit(commitArgs)
			} else {
				return nil
			}
		case status := <-pn.commitDoneChan:
			if status {
				reply.V = acceptArgs.V
			}
			return nil
		case <-timeStamp:
			return errors.New("Time out error")
		}
	}
}

// when user proposal, fist the node should do prepare
func (pn *paxosNode) DoPrepare(args *paxosrpc.PrepareArgs) {
	var na = 0
	var va interface{}
	curPrepareChan := make(chan *PrepareData)

	// call each node to do prepare
	for _, slave := range pn.connMap {
		go func(slave *rpc.Client, args *paxosrpc.PrepareArgs, curPrepareChan chan *PrepareData) {
			prepareReply := &paxosrpc.PrepareReply{}
			err := slave.Call("PaxosNode.RecvPrepare", args, prepareReply)
			if err != nil {
				fmt.Println("err when call recv Prepare: ", err)
			}

			if prepareReply.Status == paxosrpc.OK {
				curPrepareChan <- &PrepareData{status: true, na: prepareReply.N_a, va: prepareReply.V_a}
			} else {
				curPrepareChan <- &PrepareData{status: false}
			}
		}(slave, args, curPrepareChan)
	}

	// calculate the vote
	prepareCounter := 0
	prepareDoneCounter := 0
	successFlag := false
	for {
		select {
		case prepareData := <-curPrepareChan:
			prepareCounter++
			if prepareData.status {
				prepareDoneCounter++

				if prepareData.na > na {
					na = prepareData.na
					va = prepareData.va
				}

				if !successFlag && prepareDoneCounter >= pn.getMajority() {
					successFlag = true
					pn.prepareDoneChan <- &PrepareData{status: true, na: na, va: va}
				}
			}

			if prepareCounter == pn.numNodes {
				if !successFlag {
					pn.prepareDoneChan <- &PrepareData{status: false}
				}
				return
			}
		}
	}
}

// after finish the prepare, and get majority vote, do accept
func (pn *paxosNode) DoAccept(args *paxosrpc.AcceptArgs) {
	curAcceptChan := make(chan bool)

	// call each ndoe by recv accept
	for _, slave := range pn.connMap {
		go func(slave *rpc.Client, args *paxosrpc.AcceptArgs, curAcceptChan chan bool) {
			acceptReply := &paxosrpc.AcceptReply{}
			slave.Call("PaxosNode.RecvAccept", args, acceptReply)
			curAcceptChan <- acceptReply.Status == paxosrpc.OK
		}(slave, args, curAcceptChan)
	}

	// count vote
	acceptCounter := 0
	acceptDoneCounter := 0
	successFlag := false
	for {
		select {
		case status := <-curAcceptChan:
			acceptCounter++
			if status {
				acceptDoneCounter++
				if !successFlag && acceptDoneCounter >= pn.getMajority() {
					successFlag = true
					pn.acceptDoneChan <- true
				}
			}

			if acceptCounter == pn.numNodes {
				if successFlag == false {
					pn.acceptDoneChan <- false
				}
				return
			}
		}
	}
	return
}

// after do accept and get majority vote, then do commit
func (pn *paxosNode) DoCommit(args *paxosrpc.CommitArgs) {
	pn.valueMap[args.Key] = args.V
	curCommitChan := make(chan bool)
	// call each node by recvcommit
	for _, slave := range pn.connMap {
		go func(slave *rpc.Client, args *paxosrpc.CommitArgs, curCommitChan chan bool) {
			slave.Call("PaxosNode.RecvCommit", args, nil)
			curCommitChan <- true
		}(slave, args, curCommitChan)
	}

	// count the done! number
	commitDoneCounter := 0
	for {
		select {
		case <-curCommitChan:
			commitDoneCounter++
			if commitDoneCounter == pn.numNodes {
				pn.commitDoneChan <- true
				return
			}
		}
	}
}

// what is the majority vote
func (pn *paxosNode) getMajority() int {
	return pn.numNodes/2 + 1
}

// return the value
func (pn *paxosNode) GetValue(args *paxosrpc.GetValueArgs, reply *paxosrpc.GetValueReply) error {
	reply.V = pn.valueMap[args.Key]
	return nil
}

// recieve prepare request
func (pn *paxosNode) RecvPrepare(args *paxosrpc.PrepareArgs, reply *paxosrpc.PrepareReply) error {
	key := args.Key
	number := args.N
	value, ok := pn.vaMap[key]
	// first see if the key is a new key or not
	if !ok {
		value = nil
	}
	nk, ok := pn.naMap[key]
	if !ok {
		nk = -1
	}

	np, ok := pn.npMap[key]

	if !ok {
		np = -1
		pn.npMap[key] = np
	}

	// do prepare, decide give the vote or not
	if number > np {
		pn.npMap[key] = number
		reply.Status = paxosrpc.OK

		reply.N_a = nk
		reply.V_a = value
	} else {
		reply.Status = paxosrpc.Reject
	}
	return nil
}

// recieve accept request
func (pn *paxosNode) RecvAccept(args *paxosrpc.AcceptArgs, reply *paxosrpc.AcceptReply) error {
	key := args.Key
	number := args.N
	value := args.V

	np := pn.npMap[key]

	// decide give the vote or not
	if number >= np {
		pn.vaMap[key] = value
		pn.naMap[key] = number
		reply.Status = paxosrpc.OK
	} else {
		reply.Status = paxosrpc.Reject
	}
	return nil
}

// receive commit request
func (pn *paxosNode) RecvCommit(args *paxosrpc.CommitArgs, reply *paxosrpc.CommitReply) error {
	pn.valueMap[args.Key] = args.V
	// reset the proposal number
	pn.npMap[args.Key] = -1
	// delete the key from accept numeber map and accept value map
	delete(pn.naMap, args.Key)
	delete(pn.vaMap, args.Key)
	return nil
}

// receive replace server request
func (pn *paxosNode) RecvReplaceServer(args *paxosrpc.ReplaceServerArgs, reply *paxosrpc.ReplaceServerReply) error {
	pn.srvId = args.SrvID
	pn.hostMap[pn.srvId] = args.Hostport
	pn.replace = true

	count := 0
	succeed := true
	for count < pn.numRetries {
		for _, v := range pn.hostMap {
			client, err := rpc.DialHTTP("tcp", v)
			if err != nil {
				count += 1
				succeed = false
				break
			}
			pn.connMap[v] = client
		}
		if succeed {
			return nil
		}
		time.Sleep(time.Second * 1)
	}
	return nil
}

// receive replace cat chup request
func (pn *paxosNode) RecvReplaceCatchup(args *paxosrpc.ReplaceCatchupArgs, reply *paxosrpc.ReplaceCatchupReply) error {
	value, _ := json.Marshal(pn.valueMap)
	reply.Data = value
	return nil
}
