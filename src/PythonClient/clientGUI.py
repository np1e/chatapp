import tkinter as tk
from tkinter import Entry
import sys
from server import Server
from client import Client
from threading import Thread
from queue import Queue

class Controller:

    def __init__(self, root, max, sleeptime, udp_port, tcp_port):
        self.queue = Queue(maxsize=100)
        self.client = Client(udp_port, tcp_port, self)
        self.server = Server(tcp_port, self)
        self.tcp_port = tcp_port
        self.udp_port = udp_port
        self.gui = ClientGUI(self.client, root, self)


        self.gui.process()


class ClientGUI():

    def __init__(self, client, root, controller):
        self.root = root
        self.client = client
        self.queue = controller.queue
        self.controller = controller

        self.loginScreen = LoginScreen(root, self, controller.tcp_port, controller.udp_port)
        self.registerScreen = RegisterScreen(root, self)
        self.mainScreen = MainScreen(root, self)
        self.welcomeScreen = WelcomeScreen(root, self)

        self._frame = None
        self.switch_frame(self.welcomeScreen)

        root.protocol("WM_DELETE_WINDOW", self.on_closing)

    def on_closing(self):
        self.client.logout()
        root.destroy()
        sys.exit()


    def switch_frame(self, frame):
        """Destroys current frame and replaces it with a new one."""
        self.new_frame = frame
        if self._frame is not None:
            self._frame.pack_forget()
        self._frame = self.new_frame
        self._frame.pack()

    def process(self):
        while self.queue.qsize():
            try:
                item = self.queue.get()
                if item['name'] == "active_users":
                    self.mainScreen.userList.delete(0, tk.END)
                    for user in item["data"]:
                        self.mainScreen.userList.insert(tk.END, user)
                    print(item)
                elif item['name'] == "chat":
                    print("chat")
                else:
                    print("error")
                self.queue.task_done()
                self.root.update_idletasks()
            except Queue.Empty:
                pass

        self.root.after(1000, self.process)



    def login(self, username, password, tcpport, udpport):
        if self.client.login(username, password, tcpport, udpport):
            self.switch_frame(self.mainScreen)


    def register(self, username, password, confirm):
        self.client.register(username, password, confirm)

    def onUserSelect(self, evt):
        w = evt.widget
        list = w.curselection()
        #print(list[0])
        #value = self.mainScreen.userList.get(list[0])
        #if value.requested:
            # do something
        #else:
        self.sendChatRequest(value)

        #print(value)
        #print('You selected user {}!'.format(value))

    def sendChatRequest(self, user):
        self.client.make_chatreq()

    def send(self, text):
        self.client.sendMessage(text)

'''
def showLoginScreen():
    welcomeScreen.pack_forget()
    loginScreen = LoginScreen(root)
    loginScreen.pack()


def showRegisterScreen():
    welcomeScreen.pack_forget()

    registerScreen = RegisterScreen(root)
    registerScreen.pack()


def showWelcomeScreen():
    children = root.children.values()
    for child in children:
        child.pack_forget()

    welcomeScreen.pack()


def showMainScreen():
    loginScreen.pack_forget()
    mainScreen.pack()
'''





class LoginScreen(tk.Frame):
    def __init__(self, root, gui, tcp_port, udp_port):

        tk.Frame.__init__(self, root)
        usernameLabel = tk.Label(self, text="Username")
        passwordLabel = tk.Label(self, text="Password")
        usernameEntry = tk.Entry(self)
        passwordEntry = tk.Entry(self, show='*')

        usernameLabel.grid(row=0)
        passwordLabel.grid(row=1)

        usernameEntry.grid(row=0, column=1)
        passwordEntry.grid(row=1, column=1)
        
        backButton = tk.Button(self, text="Back", command= lambda: gui.switch_frame(gui.welcomeScreen))
        loginButton = tk.Button(self, text="Login", command= lambda: gui.login(usernameEntry.get(), passwordEntry.get(), tcp_port, udp_port))

        backButton.grid(row=2, column=0)
        loginButton.grid(row=2, column = 1)


class RegisterScreen(tk.Frame):

    def __init__(self, root, gui):
        tk.Frame.__init__(self, root)

        usernameLabel = tk.Label(self, text="Username")
        passwordLabel = tk.Label(self, text="Password")
        confirmLabel = tk.Label(self, text="Confirm Password")

        usernameLabel.grid(row=0)
        passwordLabel.grid(row=1)
        confirmLabel.grid(row=2)

        usernameEntry = tk.Entry(self)
        passwordEntry = tk.Entry(self, show='*')
        confirmEntry = tk.Entry(self, show='*')

        usernameEntry.grid(row=0, column=1)
        passwordEntry.grid(row=1, column=1)
        confirmEntry.grid(row=2, column=1)

        backButton = tk.Button(self, text="Back", command=lambda: gui.switch_frame(gui.welcomeScreen))
        registerButton = tk.Button(self, text="Register", command=lambda: gui.register(usernameEntry.get(),
                                                                                   passwordEntry.get(),
                                                                                   confirmEntry.get()))
        #registerButton = tk.Button(self, text="Register",
        #                          command=lambda: register(usernameEntry.get(),
        #                                                   passwordEntry.get(),
        #                                                   confirmEntry.get()))

        backButton.grid(row=3, column=0)
        registerButton.grid(row=3, column = 1)


class WelcomeScreen(tk.Frame):

    def __init__(self, root, gui):
        tk.Frame.__init__(self, root)

        entry = tk.Label(self, text="Register or login to chat.")
        register = tk.Button(self, text="Register", command=lambda: gui.switch_frame(gui.registerScreen))
        login = tk.Button(self, text="Login", command=lambda: gui.switch_frame(gui.loginScreen))

        entry.pack()
        register.pack(side="right")
        login.pack(side="left")


class MainScreen(tk.Frame):
    def __init__(self, root, gui):
        tk.Frame.__init__(self, root)

        leftFrame = tk.Frame(self)
        rightFrame = tk.Frame(self)

        self.userList = tk.Listbox(leftFrame)
        self.userList.bind('<<ListboxSelect>>', gui.onUserSelect)

        messageArea = tk.Message(rightFrame)

        self.chat_list = tk.Listbox(messageArea)

        messageBoxFrame = tk.Frame(rightFrame)
        messageTextField = tk.Text(messageBoxFrame, height=5, width=40)
        sendButton = tk.Button(messageBoxFrame, text="Send", command=lambda: gui.send(messageTextField.get()))

        sendButton.pack()
        messageTextField.pack()

        self.userList.pack()
        messageArea.pack()
        messageBoxFrame.pack()

        leftFrame.pack(side="left", fill="both", expand="true")
        rightFrame.pack(side="right")

if __name__ == "__main__":

    SLEEPTIME=0.4
    MAX_THREADS=10

    UDP_PORT = sys.argv[1]
    TCP_PORT = sys.argv[2]

    root = tk.Tk()
    root.title("Chatclient")
    Controller(root, MAX_THREADS, SLEEPTIME, UDP_PORT, TCP_PORT)

    root.mainloop()

