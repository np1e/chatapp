import socket
import json
import time
import datetime
from data import Message, User
from queue import Queue

IP = 'localhost'
PORT = 8080
connected = False
clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)


class Client:
    def __init__(self, udp_port, tcp_port, controller):
        self.queue = controller.queue
        self.udp_port = udp_port
        self.tcp_port = tcp_port
        self.controller = controller
        self.active_users = []

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
        return self.deliverData(clientSocket.recv(1024))

    def deliverData(self, receive):
        json_dict = json.loads(receive)
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

    ## Extract udp-messages
    def udp_extract(self, jsonString):
        json_dict = json.loads(jsonString)
        return json_dict

    def java_hashcode(self, s):
        h = 0
        for c in s:
            h = (31 * h + ord(c)) & 0xFFFFFFFF
        return ((h + 0x80000000) & 0xFFFFFFFF) - 0x80000000

    ## Check hashcode
    def udp_corrupted(self, json_dict):
        ## Get transmitted hashcode
        tran_hashcode = json_dict["hashcode"]
        ## Remove hashcode field and re-calculate hashcode
        del json_dict["hashcode"]
        calc_hashcode = self.java_hashcode(json.dumps(json_dict))
        if tran_hashcode == calc_hashcode:
            return False
        return True

    ## Send

    def make_chatmsg(self, content):
        pkt_dict = {}
        pkt_dict.update("method", "message")
        pkt_dict.update("username", self.username)
        pkt_dict.update("message", content)
        pkt_dict.update("timetamp", self.get_timestamp())
        pkt_dict.update("serial", ++self.serial)
        pkt_dict.update("hashcode", self.java_hashcode(pkt_dict))
        self.make_pkt(self.serial, pkt_dict)

    def make_chatreq(self):
        pkt_dict = {}
        pkt_dict.update("method", "request")
        pkt_dict.update("username", self.username)
        pkt_dict.update("timestamp", self.get_timestamp())
        pkt_dict.update("serial", ++self.serial)
        pkt_dict.update("hashcode", self.java_hashcode(pkt_dict))
        self.make_pkt(self.serial, pkt_dict)

    def make_chatconf(self):
        pkt_dict = {}
        pkt_dict.update("method", "confirm")
        pkt_dict.update("username", self.username)
        pkt_dict.update("timestamp", self.get_timestamp())
        pkt_dict.update("serial", ++self.serial)
        pkt_dict.update("hashcode", self.hashcode_java(pkt_dict))
        self.make_pkt(self.serial, pkt_dict)

    def make_chatdecl(self):
        pkt_dict = {}
        pkt_dict.update("method", "decline")
        pkt_dict.update("username", self.username)
        pkt_dict.update("timestamp", self.get_timestamp())
        pkt_dict.update("serial", ++self.serial)
        pkt_dict.update("hashcode", self.hashcode_java(pkt_dict))
        self.make_pkt(self.serial, pkt_dict)

    def get_timestamp(self):
        t = time.time()
        st = datetime.datetime.fromtimestamp(t).strftime('%d-%m-%Y %H:%M:%S')
        return st
