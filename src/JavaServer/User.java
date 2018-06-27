package JavaServer;

public class User {

    private String username;
    private String ip;
    private int port;

    public User(String name, String ip, int port) {
            username = name;
            this.ip = ip;
            this.port = port;
    }

    public String toString() {
        return username;
    }
    public String getIp() {
        return ip;
    }
    public int getPort() { return port; }
}
