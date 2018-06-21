package JavaClient;

import javafx.collections.ObservableList;

public class Chat {

    private User chatpartner;
    private ObservableList<Message> messages;
    private boolean accepted;

    public Chat(User user) {
        chatpartner = user;
        accepted = false;
    }

    public void acceptChat() {
        accepted = true;
    }
}
