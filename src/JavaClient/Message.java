package JavaClient;


public class Message {

    private String message;
    private String timestamp;
    private String username;
    private boolean confirm;

    public Message(String message, String timestamp, boolean confirm) {
        this.message = message;
        this.timestamp = timestamp;
        this.confirm = confirm;
    }

    public Message(String message, String timestamp, String username, boolean confirm) {
        this(message, timestamp, confirm);
        this.username = username;
    }

    public String getMessage(){
        return message;
    }
    public String getUsername() { return username; }
    public String getTimestamp() { return timestamp; }
    public boolean mustConfirm() { return confirm; }

    public String toString() {
        return message;
    }
}
