from tkinter import *
import client
import sys
import socket

root = Tk()
UDPPORT = None
TCPPORT = None

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



def login(username, password, tcpport, udpport):
    client.login(username, password, tcpport, udpport)

    
def register(username, password, confirm):
    client.register(username, password, confirm)


class LoginScreen(Frame):
    global UDPPORT,TCPPORT

    def __init__(self, root):
        super().__init__(root)
        Label(self, text="Username").grid(row=0)
        Label(self, text="Password").grid(row=1)
        username = Entry(self)
        password = Entry(self, show='*')

        username.grid(row=0, column=1)
        password.grid(row=1, column=1)
        
        Button(self, text="Back", command=showWelcomeScreen).grid(row=2, column = 0)
        Button(self, text="Login", command= lambda: login(username.get(), password.get(), TCPPORT, UDPPORT)).grid(row=2, column = 1)


class RegisterScreen(Frame):

    def __init__(self, root):
        super().__init__(root)
        Label(self, text="Username").grid(row=0)
        Label(self, text="Password").grid(row=1)
        Label(self, text="Confirm Password").grid(row=2)

        username = Entry(self)
        password = Entry(self, show='*')
        confirm = Entry(self, show='*')

        username.grid(row=0, column=1)
        password.grid(row=1, column=1)
        confirm.grid(row=2, column=1)

        Button(self, text="Back", command=showWelcomeScreen).grid(row=3, column = 0)
        Button(self, text="Register", command= lambda: register(username.get(), password.get(), confirm.get())).grid(row=3, column = 1)


class WelcomeScreen(Frame):

    def __init__(self, root):
        super().__init__(root)
        entry = Label(self, text="Register or login to chat.").pack()
        register = Button(self, text="Register", command=showRegisterScreen).pack(side="right")
        login = Button(self, text="Login", command=showLoginScreen).pack(side="left")

def on_closing():
    client.close()
    root.destroy()

if __name__ == "__main__":

    UDPPORT = sys.argv[1]
    TCPPORT = sys.argv[2]
    welcomeScreen = WelcomeScreen(root)
    showWelcomeScreen()
    if 'clientSocket' not in globals():
        clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        connected = False
    ip = "localhost"
    port = 8080
    root.protocol("WM_DELETE_WINDOW", on_closing)
    root.mainloop()