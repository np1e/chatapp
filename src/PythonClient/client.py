import socket
import json

IP = 'localhost'
PORT = 8080
connected = False
clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        
def login(username, password):
    connect(IP,PORT)
    data = {
        'method' : 'login',
        'username' : username,
        'password' : password
    }
    makeRequest(data)


def register(username, password, confirm):
    connect(IP,PORT)
    data = {
        "method" : "register",
        "username" : username,
        "password" : password
    }
    makeRequest(data)

def makeRequest(data):
    json_string = json.dumps(data) + "\n"
    clientSocket.send(json_string.encode("utf-8"))

def close():
    clientSocket.close()

def connect(ip, port):
    global connected
    if not connected:
        clientSocket.connect((ip, port))
        connected = True
