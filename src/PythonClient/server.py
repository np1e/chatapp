import json
import socket
import User
from threading import Thread

PORT_TCP = None
IP_TCP = '127.0.0.1'
BUFFER_SIZE = 1024
active_users = []


def start_server(tcpport):
    global PORT_TCP
    PORTTCP = int(tcpport)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((IP_TCP, PORTTCP))
    s.listen(1)

    conn, addr = s.accept()
    print("Connection address:", addr)
    thread = Thread(target=handle_request, args=(conn, addr))
    thread.start()

def handle_request(conn, addr):
    while 1:
        data = conn.recv(BUFFER_SIZE)
        if not data: break
        print("received data:", data)
        loadList(data)


def loadList(data):
    jsonObj = json.loads(data)
    for u in jsonObj:
        print(u)

