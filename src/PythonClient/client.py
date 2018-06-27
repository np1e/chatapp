import socket
import json
import clientGUI

IP = 'localhost'
PORT = 8080
connected = False
clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        
def login(username, password, tcpport, udpport):
    connect(IP,PORT)
    data = {
        'method' : 'login',
        'username' : username,
        'password' : password,
        'tcpport' : tcpport,
        'udpport' : udpport
    }
    makeRequest(data)


def register(username, password, confirm):
    connect(IP,PORT)
    data = {
        "method" : "register",
        "username" : username,
        "password" : password,
        "confirm": confirm
    }
    makeRequest(data)

def makeRequest(data):
    json_string = json.dumps(data) + "\n"
    clientSocket.send(json_string.encode("utf-8"))
    deliverData(clientSocket.recv(1024))

def deliverData(receive):
    json_dict = json.loads(receive)
    if(json_dict["method"] == "confirmation"):

        if(json_dict["type"] == "login"):
            if(json_dict["status"] == 0):
                print("failed authentification")
            else:
                print("successful login")

        if(json_dict["type"] == "register"):
            if(json_dict["status"] == 0):
                print("failed registration")
            else:
                print("successful registration")

    if(json_dict["method"] == "message"):
        print("message")
    if(json_dict["method"] == "chatrequest"):
        print("chatrequest")

def close():
    clientSocket.close()

def connect(ip, port):
    global connected
    if not connected:
        clientSocket.connect((ip, port))
        connected = True
