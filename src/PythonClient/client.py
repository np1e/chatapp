from socket import *
import json
import time
import datetime
from data import Message, User
from queue import Queue
from threading import Thread

IP = 'localhost'
PORT = 8080
connected = False
clientSocket = socket(AF_INET, SOCK_STREAM)

class Client:
    def __init__(self, udp_port, tcp_port, controller):
        self.serialized_chat = {}
        self.queue = controller.queue
        self.udp_port = udp_port
        self.tcp_port = tcp_port
        self.controller = controller
        self.active_users = []
        self.serial = 0
        self.udp_rcv_starter(udp_port)

    def process(self):
        while self.queue.qsize():
            try:
                item = self.queue.get()
                if item['name'] == "active_users":
                    for user in item["data"]:
                        self.active_users.append(User(user["username"], user["ip"]))
                    print(item)
                self.queue.task_done()
            except Queue.Empty:
                pass

        self.root.after(1000, self.process)

    def login(self, username, password, tcpport, udpport):
        self.connect(IP,PORT)
        self.username = username
        data = {
            'method' : 'login',
            'username' : username,
            'password' : password,
            'tcpport' : tcpport,
            'udpport' : udpport
        }
        return self.makeRequest(data)

    def logout(self):
        print("logout")
        self.connect(IP, PORT)
        data = {
            'method' : 'logout',
            'username' : self.username
        }
        return  self.makeRequest(data)

    def register(self, username, password, confirm):
        self.connect(IP,PORT)
        data = {
            "method" : "register",
            "username" : username,
            "password" : password,
            "confirm": confirm
        }
        return self.makeRequest(data)

    def sendMessage(self, msg):
        if(self.request):
            if msg in ['y', 'Y', 'yes', 'Yes']:
                self.make_chatconf()
                self.request = False
            elif msg in ['n', 'N', 'no', 'No']:
                self.make_chatdecl()
                self.request = False
        #serverName = "localhost"
        #serverPort = 12000
        #clientSocket = socket(AF_INET, SOCK_DGRAM)
        #message = raw_input("Input lowercase sentence: ")
        #clientSocket.sendto((message), (serverName, serverPort))
        #clientSocket.close()

    def makeRequest(self, data):
        json_string = json.dumps(data) + "\n"
        clientSocket.send(json_string.encode("utf-8"))
        receive = json.loads(clientSocket.recv(1024))
        return self.deliverData(receive)

    def deliverData(self, json_dict):
        if(json_dict["method"] == "confirmation"):

            if(json_dict["type"] == "login"):
                if(json_dict["status"] == 0):
                    print("failed authentification")
                    return False
                else:
                    print("successful login")
                    return True

            if(json_dict["type"] == "register"):
                if(json_dict["status"] == 0):
                    print("failed registration")
                else:
                    print("successful registration")

        if(json_dict["method"] == "message"):

            print("message")
        if(json_dict["method"] == "request"):
            self.request = True
            self.updateChatMessages(json_dict["username"], "Chatanfrage von User ", json_dict["username"], "erhalten.")
            self.updateChatMessages("Annehmen?")
            print("chatrequest")
        if(json_dict["method"] == "confirm"):
            for user in self.active_users:
                if user._username == json_dict["username"]:
                    user.confirmed = True
                    self.updateChatMessages(user._username, "Chatanfrage wurde akzeptiert!")
            print("chatconfirm")
        return False

    def updateChatMessages(self, username, msg):
        for user in self.active_users:
            if user._username == username:
                user._chat.append(Message(msg, self.get_timestamp(), user))
                self.queue.put(user._chat)

    def close(self):
        clientSocket.close()

    def connect(self, ip, port):
        global connected
        if not connected:
            clientSocket.connect((ip, port))
            connected = True


    ## UDP


    ## Receive


    ## Waiting for arriving udp-messages
    def udp_rcv_starter(self, port):
        thread = Thread(target=self.udp_rcv, args=(port,))
        thread.start()

    def udp_rcv(self, port):
        serverPort = 8010
        serverSocket = socket(AF_INET, SOCK_DGRAM)
        serverSocket.bind(("localhost", serverPort))
        print("socket opened")
        while 1:
            # read client's message AND REMEMBER client's address (IP and port)
            message, clientAddress = serverSocket.recvfrom(2048)
            # output to console the sentence received from client over UDP
            json_dict = json.loads(message)
            print ("Received from Client: ", json_dict)

            if json_dict["method"] == "confirm":
                print("confirm")
                if not self.udp_corrupted(json_dict):
                    print("not corrupted")
                    ## Not corrupted
                    self.deliverData(json_dict)
                else:
                    self.make_nak()
            else:
                if json_dict["method"] == "ack":
                    print("ACK!")
                    del self.serialized_chat[self.serial]
                elif json_dict["method"] == "nak":
                    print("NAK!")
                    pkt_map = self.serialized_chat[self.serial]
                    self.remake_pkt(self, pkt_map)

    ## Extract udp-messages
    def udp_extract(self, jsonString):
        json_dict = json.loads(jsonString)
        return json_dict

    def java_hashcode(self, s):
        print(s)
        h = 0
        for k, v in s.items():
            print("h;",h)
            for c in k:
                h = (31 * h + ord(c)) & 0xFFFFFFFF
            for l in v:
                h = (31 * h + ord(l)) & 0xFFFFFFFF

            h += ((h + 0x80000000) & 0xFFFFFFFF) - 0x80000000
        return h

    ## Check hashcode
    def udp_corrupted(self, json_dict):
        ## Get transmitted hashcode
        tran_hashcode = json_dict["hashcode"]
        ## Remove hashcode field and re-calculate hashcode
        del json_dict["hashcode"]
        calc_hashcode = self.java_hashcode(json_dict)
        return False
        if tran_hashcode == calc_hashcode:
            return False
        return True


    ## Send

    def make_chatmsg(self, content):
        pkt_dict = {}
        pkt_dict.update({"method": "message"})
        pkt_dict.update({"username": self.username})
        pkt_dict.update({"message": content})
        pkt_dict.update({"timetamp": self.get_timestamp()})
        pkt_dict.update({"serial": ++self.serial})
        pkt_dict.update({"hashcode": self.java_hashcode(pkt_dict)})
        self.make_pkt(pkt_dict)

    def make_chatreq(self):
        pkt_dict = {}
        pkt_dict.update({"method": "request"})
        pkt_dict.update({"username": self.username})
        pkt_dict.update({"timestamp": self.get_timestamp()})
        pkt_dict.update({"serial": str(++self.serial)})

        #pkt_dict.update({"hashcode": self.java_hashcode(pkt_dict)})
        pkt_dict.update({"hashcode": "0"})
        self.make_pkt(pkt_dict)

    def make_chatconf(self):
        pkt_dict = {}
        pkt_dict.update("method", "confirm")
        pkt_dict.update("username", self.username)
        pkt_dict.update("timestamp", self.get_timestamp())
        pkt_dict.update("serial", ++self.serial)
        pkt_dict.update("hashcode", self.hashcode_java(pkt_dict))
        self.make_pkt(pkt_dict)

    def make_chatdecl(self):
        pkt_dict = {}
        pkt_dict.update("method", "decline")
        pkt_dict.update("username", self.username)
        pkt_dict.update("timestamp", self.get_timestamp())
        pkt_dict.update("serial", ++self.serial)
        pkt_dict.update("hashcode", self.hashcode_java(pkt_dict))
        self.make_pkt(pkt_dict)

    def make_ack(self):
        pkt_dict = {}
        pkt_dict.update({"method": "ack"})
        pkt_dict.update({"serial": self.serial})
        bytes = json.dumps(pkt_dict).encode("utf-8")
        self.udp_send(bytes);

    def make_nak(self):
        pkt_dict = {}
        pkt_dict.update({"method": "nak"})
        pkt_dict.update({"serial": self.serial})
        bytes = json.dumps(pkt_dict).encode("utf-8")
        self.udp_send(bytes);

    def make_pkt(self, pkt_dict):
        ## Store pkt_map in serialized_chat, pkt_map gets removed, if ack is received
        self.serialized_chat.update({self.serial: pkt_dict})
        thread = Thread(target=self.make_pkt_thread, args=(pkt_dict,))
        thread.start()
        bytes = json.dumps(pkt_dict).encode("utf-8")
        print(bytes)
        self.udp_send(bytes)

    def make_pkt_thread(self, pkt_dict):
        time.sleep(5)
        if pkt_dict in self.serialized_chat.values():
            print("No ack receiveid for serial in 5 secs")
            self.remake_pkt(self, pkt_dict)
        self.stop = True

    def remake_pkt(self, pkt_dict):
        ## Store pkt_map in serialized_chat, pkt_map gets removed, if ack is received
        self.serialized_chat.update({self.serial: pkt_dict})
        thread = Thread(target=self.remake_pkt_thread, args=(pkt_dict,))
        thread.start()
        bytes = json.dumps(pkt_dict).encode("utf-8")
        print(bytes)
        self.udp_send(bytes)

    def remake_pkt_thread(self, pkt_dict):
        time.sleep(5)
        if pkt_dict in self.serialized_chat:
            print("No ack receiveid for serial in 5 secs")
            self.remake_pkt(self, pkt_dict)
        self.stop = True

    def udp_send(self, bytes):
        goalPort = 9010
        serverName = "localhost"
        clientSocket = socket(AF_INET, SOCK_DGRAM)
        clientSocket.sendto((bytes), (serverName, goalPort))
        clientSocket.close()

    def get_timestamp(self):
        t = time.time()
        st = datetime.datetime.fromtimestamp(t).strftime('%d-%m-%Y %H:%M:%S')
        return st
