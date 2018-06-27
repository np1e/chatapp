

class User():
    def __init__(self, username, ip):
        self._username = username
        self._ip = ip
        self._chat = []

    def __repr__(self):
        return self._username

    def addMessage(self, message):
        self._chat.append(message)

class Message:

    def __init__(self, username, msg):
        self._username = username
        self._msg = msg

