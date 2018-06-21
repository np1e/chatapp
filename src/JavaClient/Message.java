package JavaClient;

public class Message {

    private String message;
    private String timestamp;

    public Message(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage(){
        return message;
    }

    public String toString() {
        return message;
    }
}
