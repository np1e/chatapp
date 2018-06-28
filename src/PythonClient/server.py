import json
from socket import *
from data import User
from threading import Thread

IP_TCP = '127.0.0.1'
BUFFER_SIZE = 1024

class Server:
    def __init__(self, tcpport, controller):
        self.active_users = []
        self._tcpport = int(tcpport)
        self.sock = socket(AF_INET, SOCK_STREAM)
        self.sock.bind((IP_TCP, self._tcpport))
        self.sock.listen(1)
        self.queue = controller.queue
        self.controller = controller
        thread = Thread(target=self.start_server, args=(self.sock, self._tcpport,))
        thread.start()


    def start_server(self, sock, tcpport):
        while 1:
            connsock, addr = sock.accept()
            thread = Thread(target=self.handle_request, args=(connsock, addr))
            thread.start()

    def handle_request(self, sock, addr):
        while 1:
            data = sock.recv(BUFFER_SIZE)
            if not data: break
            self.loadlist(data.decode("utf-8"))

    def loadlist(self, data):
        self.active_users=[]
        json_dict = json.loads(data)
        for k in json_dict["data"]:
            user = User(k['username'], k['ip'])
            self.active_users.append(user)
        self.controller.client.active_users = self.active_users
        gui_dict = {"name":"active_users", "data": self.active_users}
        self.queue.put(gui_dict)
        print(gui_dict)