package JavaServer;

public class User {

    private String username;
    private String ip;

    public User(String name, String ip) {
            username = name;
            this.ip = ip;
    }

    public String toString() {
        return username;
    }
}
