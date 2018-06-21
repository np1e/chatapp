package JavaClient;

public class User {

    private String username;
    private String ip;

    public User(Object username, Object ip) {
        this.username = username.toString();
        this.ip = ip.toString();
    }

    public String toString() {
        return username;
    }
    public String getIp() {
        return ip;
    }
}
