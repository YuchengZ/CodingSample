package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"github.com/cmu440-F15/paxosapp/rpc/paxosrpc"
	"hash/fnv"
	"html/template"
	"math/rand"
	"net/http"
	"net/rpc"
	"strconv"
	"strings"
)

// global variable
var (
	numNodes   = flag.Int("N", 1, "number of paxos nodes in the ring")
	paxosPorts = flag.String("paxosports", "", "ports of all nodes")
)

// struct for an order
type Order struct {
	PId   int
	PName string
	PNum  int
}

// struct of web page
type Page struct {
	Title string
	Body  string
}

// struct of a user, a value in paxos
type User struct {
	UName    string
	UId      int
	Password string
	Orders   map[string]*Order
}

// server
type Server struct {
	nodeId int
	cliMap map[int]*rpc.Client
}

// hash string
func hash(s string) uint32 {
	h := fnv.New32a()
	h.Write([]byte(s))
	return h.Sum32()
}

// init app
func initApp() (map[int]*rpc.Client, error) {
	allPorts := "9009,9010,9011"
	numNodes := 3
	fmt.Println(allPorts, numNodes)

	// create client
	cliMap := make(map[int]*rpc.Client)
	portList := strings.Split(allPorts, ",")

	// connection with client and build node
	for i, p := range portList {
		cli, err := rpc.DialHTTP("tcp", "localhost:"+p)
		if err != nil {
			fmt.Println("Connection fail", p)
			return nil, fmt.Errorf("could not connect to node %d", i)
		}
		cliMap[i] = cli
		fmt.Println("Connection success", p)
	}

	return cliMap, nil
}

/************************************************
 * below is rpc call for paxos node
 ***********************************************/

// call paxos: get next proposal number
func (s *Server) GetNextProposalNumber(key string, nodeID int) (*paxosrpc.ProposalNumberReply, error) {
	fmt.Println("in app: get next proposal")

	args := &paxosrpc.ProposalNumberArgs{Key: key}
	fmt.Println("in app: ", args)
	var reply paxosrpc.ProposalNumberReply
	err := s.cliMap[nodeID].Call("PaxosNode.GetNextProposalNumber", args, &reply)
	fmt.Println("in app: ", err)
	return &reply, err
}

// call paxos: get value
func (s *Server) GetValue(key string, nodeID int) (*paxosrpc.GetValueReply, error) {
	args := &paxosrpc.GetValueArgs{Key: key}
	var reply paxosrpc.GetValueReply
	err := s.cliMap[nodeID].Call("PaxosNode.GetValue", args, &reply)
	return &reply, err
}

// call paxos: propose
func (s *Server) Propose(key string, value interface{}, pnum, nodeID int) (*paxosrpc.ProposeReply, error) {
	args := &paxosrpc.ProposeArgs{N: pnum, Key: key, V: value}
	var reply paxosrpc.ProposeReply
	err := s.cliMap[nodeID].Call("PaxosNode.Propose", args, &reply)
	return &reply, err
}

/************************************************
 * below are web handler function
 ***********************************************/

// exectute the html file
func (s *Server) renderTemplate(w http.ResponseWriter, tmpl string, p *Page) {
	t, _ := template.ParseFiles(tmpl + ".html")
	fmt.Println(t)
	t.Execute(w, p)
}

// do viewHandler when request: /view/
func (s *Server) viewHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("in view")
	// get id from the request
	id := r.URL.Path[len("/view/"):]

	reply, _ := s.GetValue(id, s.nodeId)
	fmt.Println(reply)
	userM := reply.V.([]byte)
	var user *User
	json.Unmarshal([]byte(userM), &user)

	// build response string
	response := ""
	response += "uId:" + strconv.Itoa(user.UId) + "&"
	response += "uName:" + user.UName + "&"
	response += "uOrder:"
	orders := user.Orders
	for _, v := range orders {
		response += strconv.Itoa(v.PId) + "#"
		response += v.PName + "#"
		response += strconv.Itoa(v.PNum) + ":"
	}
	p := &Page{Title: response}

	s.renderTemplate(w, "view", p)
}

// register handler for request /register/
func (s *Server) registerHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("in register handle")
	s.renderTemplate(w, "register", nil)
}

// index handler for request /, the begin of the web
func (s *Server) indexHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("index handler")
	s.renderTemplate(w, "login", nil)
}

// login handler for request /login/
func (s *Server) loginHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("in login handler")
	userName := r.FormValue("username")
	s.nodeId = rand.Intn(3)
	fmt.Println("username: ", userName)
	uId := int(hash(userName))
	http.Redirect(w, r, "/view/"+strconv.Itoa(uId), http.StatusFound)
}

// create an user
func (s *Server) createHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("in create handle")
	s.nodeId = rand.Intn(3)
	userName := r.FormValue("username")
	password := r.FormValue("password")

	// key
	userId := int(hash(userName))
	fmt.Println("uername: ", userName)
	fmt.Println("password: ", password)
	fmt.Println("userId: ", userId)
	fmt.Println("nodeId: ", s.nodeId)

	// value
	newUser := &User{
		UName:    userName,
		UId:      userId,
		Password: password,
		Orders:   make(map[string]*Order),
	}
	fmt.Println("new user is: ", newUser)

	userM, _ := json.Marshal(newUser)
	fmt.Println("value is: ", userM)

	// proposal
	replyN, _ := s.GetNextProposalNumber(strconv.Itoa(userId), s.nodeId)
	fmt.Println(replyN, userId)
	replyP, err := s.Propose(strconv.Itoa(userId), userM, replyN.N, s.nodeId)
	fmt.Println(replyP, err)

	http.Redirect(w, r, "/view/"+strconv.Itoa(userId), http.StatusFound)
}

// make an order for handler
func (s *Server) makeOrderHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("in make order handle")
	uId := r.URL.Path[len("/makeOrder/"):]
	// parse uid
	uId = strings.Split(uId, "&")[0]
	uId = strings.Split(uId, ":")[1]

	pName := r.FormValue("pName")
	quant, _ := strconv.Atoi(r.FormValue("quantity"))

	fmt.Println("uId", uId)
	fmt.Println("pName", pName)
	fmt.Println("quant", quant)

	// get proposal number and get value of a user, update
	// user, then proposal
	replyN, _ := s.GetNextProposalNumber(uId, s.nodeId)

	fmt.Println("replyN: ", replyN)
	pId := replyN.N

	order := &Order{
		PId:   pId,
		PName: pName,
		PNum:  quant,
	}

	replyU, _ := s.GetValue(uId, s.nodeId)
	fmt.Println("replyU: ", replyU)

	userM := replyU.V.([]byte)
	fmt.Println("userM: ", userM)

	// unmarshal value
	var user *User
	json.Unmarshal([]byte(userM), &user)
	fmt.Println("user: ", user)
	user.Orders[strconv.Itoa(pId)] = order
	fmt.Println("user: ", user)

	// marshal value
	userM, _ = json.Marshal(user)

	replyP, err := s.Propose(uId, userM, pId, s.nodeId)
	fmt.Println("replyP: ", replyP, err)

	http.Redirect(w, r, "/view/"+uId, http.StatusFound)
}

func main() {
	flag.Parse()
	cliMap, _ := initApp()
	s := &Server{
		nodeId: 0,
		cliMap: cliMap,
	}
	http.HandleFunc("/", s.indexHandler)
	http.HandleFunc("/login/", s.loginHandler)
	http.HandleFunc("/view/", s.viewHandler)
	http.HandleFunc("/register/", s.registerHandler)
	http.HandleFunc("/create/", s.createHandler)
	http.HandleFunc("/makeOrder/", s.makeOrderHandler)
	http.ListenAndServe(":8080", nil)

	fmt.Println("Success")
}
