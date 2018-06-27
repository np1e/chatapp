import tkinter as tk
from tkinter import Entry
import client
import sys
import socket

root = Tk()
UDPPORT = None
TCPPORT = None

class ClientGUI(tk.Tk):
    def __init__(self):
        tk.Tk.__init__(self)
        self._frame = None
        self.switch_frame(WelcomeScreen)

    def switch_frame(self, frame_class):
        """Destroys current frame and replaces it with a new one."""
        new_frame = frame_class(self)
        if self._frame is not None:
            self._frame.destroy()
        self._frame = new_frame
        self._frame.pack()

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


def login(username, password, tcpport, udpport):
    client.login(username, password, tcpport, udpport)


def register(username, password, confirm):
    client.register(username, password, confirm)


class LoginScreen(tk.Frame):
    global UDPPORT,TCPPORT
    def __init__(self, root):
        tk.Frame.__init__(self, root)
        usernameLabel = tk.Label(self, text="Username")
        passwordLabel = tk.Label(self, text="Password")
        usernameEntry = tk.Entry(self)
        passwordEntry = tk.Entry(self, show='*')

        usernameLabel.grid(row=0)
        passwordLabel.grid(row=1)

        usernameEntry.grid(row=0, column=1)
        passwordEntry.grid(row=1, column=1)
        
        backButton = tk.Button(self, text="Back", command=showWelcomeScreen).grid(row=2, column = 0)
        loginButton = tk.Button(self, text="Login", command= lambda: login(username.get(), password.get(), TCPPORT, UDPPORT)).grid(row=2, column = 1)

        root.switch_frame(MainScreen)

        backButton.grid(row=2, column=0)
        loginButton.grid(row=2, column = 1)


class RegisterScreen(tk.Frame):

    def __init__(self, root):
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

        backButton = tk.Button(self, text="Back", command=lambda: root.switch_frame(WelcomeScreen))
        registerButton = tk.Button(self, text="Register", command=lambda: root.switch_frame(MainScreen))
        #registerButton = tk.Button(self, text="Register",
        #                          command=lambda: register(usernameEntry.get(),
        #                                                   passwordEntry.get(),
        #                                                   confirmEntry.get()))

        backButton.grid(row=3, column=0)
        registerButton.grid(row=3, column = 1)


class WelcomeScreen(tk.Frame):

    def __init__(self, root):
        tk.Frame.__init__(self, root)

        entry = tk.Label(self, text="Register or login to chat.")
        register = tk.Button(self, text="Register", command=lambda: root.switch_frame(RegisterScreen))
        login = tk.Button(self, text="Login", command=lambda: root.switch_frame(LoginScreen))

        entry.pack()
        register.pack(side="right")
        login.pack(side="left")


class MainScreen(tk.Frame):
    def __init__(self, root):
        tk.Frame.__init__(self, root)

        leftFrame = tk.Frame(self)
        rightFrame = tk.Frame(self)

        userList = tk.Listbox(leftFrame)
        messageArea = tk.Message(rightFrame)

        messageBoxFrame = tk.Frame(rightFrame)
        sendButton = tk.Button(messageBoxFrame, text="Send")
        messageTextField = tk.Text(messageBoxFrame, height=5, width=40)

        sendButton.pack()
        messageTextField.pack()

        userList.pack()
        messageArea.pack()
        messageBoxFrame.pack()

        leftFrame.pack(side="left", fill="both", expand="true")
        rightFrame.pack(side="right")

def on_closing():
    client.close()
    clientGUI.destroy()

if __name__ == "__main__":

    UDPPORT = sys.argv[1]
    TCPPORT = sys.argv[2]
    clientGUI = ClientGUI()
    if 'clientSocket' not in globals():
        clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        connected = False
    ip = "localhost"
    port = 8080
    clientGUI.protocol("WM_DELETE_WINDOW", on_closing)
    clientGUI.mainloop()
