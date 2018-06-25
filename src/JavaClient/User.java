package JavaClient;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class User {

    private String username;
    private String ip;
    public ObservableList<Message> chat;
    private SimpleBooleanProperty requested = new SimpleBooleanProperty();
    private SimpleBooleanProperty confirmed = new SimpleBooleanProperty();

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

    public boolean isRequested() {
        return requested.get();
    }

    public SimpleBooleanProperty requestedProperty() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested.set(requested);
    }

    public boolean isConfirmed() {
        return confirmed.get();
    }

    public SimpleBooleanProperty confirmedProperty() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed.set(confirmed);
    }
}


