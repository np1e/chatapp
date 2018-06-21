package JavaServer;

import javafx.collections.ObservableList;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {

    private ObservableList<User> activeUsers;

    public TCPClient(ObservableList<User> activeUsersObservable) {
        this.activeUsers = activeUsersObservable;
    }

    public void sendToAll(String jsonString) {
        System.out.println(activeUsers);
        for(User user: activeUsers) {
            Socket clientSocket = null;
            try {
                clientSocket = new Socket(user.getIp(), user.getPort());
                DataOutputStream outToUser = new DataOutputStream(clientSocket.getOutputStream());
                outToUser.writeBytes(jsonString);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
