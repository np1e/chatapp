package JavaClient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class User {

    private String username;
    private String ip;
    private ObservableList<Message> chat;

    public User(Object username, Object ip) {
        this.username = username.toString();
        this.ip = ip.toString();
        this.chat = FXCollections.observableArrayList();
    }

    public String toString() {
        return username;
    }
    public String getIp() {
        return ip;
    }

    public ObservableList getChat() {
        return chat;
    }
}
