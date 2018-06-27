import json
import socket
import User

PORT_TCP = None
BUFFER_SIZE = 1024
active_users = []


def start_server(tcpport):
    global PORT_TCP
    PORTTCP = tcpport
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((PORT_TCP,))
    s.listen(1)

    conn, addr = s.accept()
    print("Connection address:", addr)
    while 1:
        data = conn.recv(BUFFER_SIZE)
        if not data: break
        print("received data:", data)
        loadList(data)


def loadList(data):
    jsonObj = json.loads(data)
    for u in jsonObj:
        print(u)

