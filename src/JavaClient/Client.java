package JavaClient;

import com.google.gson.*;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Client {

    private ObservableList<User> activeusers;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String portUDP;
    private String portTCP;
    private int serial;


    public Client(String portUDP, String portTCP, ObservableList activeUsers) throws IOException {
        activeusers = activeUsers;
        // TCP
        socket = new Socket("127.0.1.1", 8080);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // UDP
        portUDP = portUDP;
        serial = 0;
        new Thread(new Client.udpReceive(listenOnPort)).start();

    }

    public ObservableList getActiveChat() {
    }

    public class udpReceive implements Runnable {
        private int listenOnPort;
        public udpReceive(int port) {
            listenOnPort = port;
        }
        public void run() {

            // Build datagram
            DatagramSocket serverSocket = null;
            try { serverSocket = new DatagramSocket(listenOnPort); }
            catch(SocketException e) { e.printStackTrace(); }

            // Build
            byte[] receiveData = new byte[1024];

            while(true) {
                // Wait for message
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try { serverSocket.receive(receivePacket); }
                catch (IOException e) { e.printStackTrace(); }

                // Received packet
                String jsonString = new String(receivePacket.getData(), 0, receivePacket.getLength());
                JsonParser parser = new com.google.gson.JsonParser();
                JsonObject json = parser.parse(jsonString).getAsJsonObject();
                String adress = new String(String.valueOf(receivePacket.getAddress()));
                String port = new String(String.valueOf(receivePacket.getPort()));

                // Received message
                if(json.get("method").getAsString().equals("message")) {
                    System.out.println("UDP MESSAGE RECEIVED / serial: " + json.get("serial"));
                    serial = json.get("serial").getAsInt();
                    send_ack();
                }
                // Received request
                if(json.get("method").getAsString().equals("request")) {
                    System.out.println("UDP CHAT REQUEST RECEIVED / serial: " + json.get("serial"));
                    serial = json.get("serial").getAsInt();
                    send_ack();
                }
                // Received request
                if(json.get("method").getAsString().equals("ack")) {
                    System.out.println("ACK RECEIVED / serial: " + json.get("serial"));
                }

            }
        }

    }

    public void send_ack() {
        // Build ackMap
        Map ackMap = new HashMap();
        ackMap.put("method", "ack");
        ackMap.put("serial", serial);
        Gson gson = new Gson();
        byte[] ackBytes = gson.toJson(ackMap).getBytes();

        try {
            udp_send(ackBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password) throws IOException {
        // Request login at server
        Map<String, String> loginData = new HashMap();
        loginData.put("method", "login");
        loginData.put("username", username);
        loginData.put("password", password);
        loginData.put("tcpport", portTCP);
        Gson gson = new Gson();
        String loginString = gson.toJson(loginData);
        String response = sendText(loginString);
        System.out.println(response);
        loadList(response);

    }

    private void loadList(String response) {
        JsonObject json = parseJson(response);
        JsonArray data = json.getAsJsonArray("data");
        System.out.println(data);
        for(JsonElement j: data) {
            JsonObject user = j.getAsJsonObject();
            activeusers.add(new User(user.get("username").getAsString(), user.get("ip").getAsString()));
        }
        System.out.println(activeusers);
    }

    public ObservableList getUsers() {
        return activeusers;
    }

    private JsonObject parseJson(String jsonString) {
        System.out.println(jsonString);
        JsonParser parser = new com.google.gson.JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();
        System.out.println(json);
        return json;
    }

    public String sendText(final String object) throws IOException {
        writer.write(object + "\n");
        writer.flush();
        String response = reader.readLine();
        return response;
    }

    public void close() throws IOException {
        socket.close();
    }

    public User getUserByUsername(String username) {
        User user = null;
        for(User u : activeusers) {
            if(u.toString() == username) {
                user = u;
            }
        }
        return user;
    }

    public String getTimestamp() {
        SimpleDateFormat curTime = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date now = new Date();
        return curTime.format(now);
    }

    public void sendChatRequest(String username) throws Exception {
        User user = getUserByUsername(username);
        String timestamp = getTimestamp();
        user.getChat().add(new Message("Du hast eine Chat-Anfrage erhalten", getTimestamp()));

        // Build messageMap
        Map messageMap = new HashMap();
        messageMap.put("method", "request");
        messageMap.put("username", username);
        messageMap.put("timestamp", timestamp);
        messageMap.put("serial", ++serial);
        Gson gson = new Gson();
        byte[] messageBytes = gson.toJson(messageMap).getBytes();

        // UDP Send
        udp_send(messageBytes);
    }

    public void udp_send(byte[] messageBytes) throws Exception {
        int goalPort;
        if(listenOnPort == 8010) { goalPort = 9010;}
        else { goalPort = 8010; }

        // Build datagram, goalIP
        DatagramSocket clientSendSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        // Send packet
        DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length, IPAddress, goalPort);
        clientSendSocket.send(sendPacket);

    }


}



