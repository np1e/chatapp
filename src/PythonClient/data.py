class User():
    def __init__(self, username, ip):
        self._username = username
        self._ip = ip
        self._chat = []
        self.requested = False
        self.confirmed = False

    def __repr__(self):
        return self._username

    def addMessage(self, message):
        self._chat.append(message)

class Message:

    def __init__(self, username, timestamp, msg):
        self._username = username
        self._msg = msg
        self.timestamp = timestamp

    def __repr__(self):
        return "{}\n{}:\t{}".format(self.timestamp, self._username,self._msg)

