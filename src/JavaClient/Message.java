package JavaClient;


import javafx.beans.property.SimpleBooleanProperty;

public class Message {

    private String message;
    private String timestamp;
    private User user;
    private boolean confirm;
    private SimpleBooleanProperty confirmed;

    public Message(String message, String timestamp, boolean confirm) {
        this.message = message;
        this.timestamp = timestamp;
        this.confirm = confirm;
        this.confirmed = new SimpleBooleanProperty();
    }

    public Message(String message, String timestamp, User user, boolean confirm) {
        this(message, timestamp, confirm);
        this.user = user;
    }

    public String getMessage(){
        return message;
    }
    public String getUsername() { return user.toString(); }
    public String getTimestamp() { return timestamp; }
    public boolean mustConfirm() { return confirm; }

    public String toString() {
        return message;
    }

    public boolean isConfirmed() {
        return confirmed.get();
    }

    public SimpleBooleanProperty confirmedProperty() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed.set(confirmed);
        this.user.setConfirmed(confirmed);
    }
}
