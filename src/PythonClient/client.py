import socket
import json

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


    def login(self, username, password, tcpport, udpport):
        self.connect(IP,PORT)
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
        if(json_dict["method"] == "chatrequest"):
            print("chatrequest")

        return False

    def close(self):
        clientSocket.close()

    def connect(self, ip, port):
        global connected
        if not connected:
            clientSocket.connect((ip, port))
            connected = True
