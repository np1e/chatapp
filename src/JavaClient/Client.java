package JavaClient;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Client {

    private ObservableList<Message> activechat;
    private ObservableList<User> activeusers;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private int portUDP;
    private int portTCP;
    private int serial;
    private SimpleStringProperty username;


    public Client(String portUDP, String portTCP, ObservableList activeUsers, ObservableList<Message> activeChat) throws IOException {
        username = new SimpleStringProperty();
        activeusers = activeUsers;
        activechat = activeChat;
        // TCP
        this.portTCP = Integer.parseInt(portTCP);
        socket = new Socket("127.0.1.1", 8080);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // UDP
        this.portUDP = Integer.parseInt(portUDP);

        serial = 0;
        new Thread(new Client.udpReceive(Integer.parseInt(portUDP))).start();

    }

    public SimpleStringProperty getUsername() {
        return username;
    }

    public void logout() throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("method", "logout");
        json.addProperty("username", this.username.getValue());
        Gson gson = new Gson();
        String jsonString = gson.toJson(json);
        System.out.println(jsonString);
        String response = sendText(jsonString);
        System.out.println(response);
    }

    public void exit() throws IOException {
        System.out.println("EXIT");
        logout();
        Platform.exit();
        System.exit(0);
    }

    public ObservableList<Message> getActiveChat() {
        return activechat;
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
                    for(User u : activeusers) {
                        if (u.toString().equals(json.get("username").toString().replace("\"", ""))) {
                            System.out.println("UDP CHAT REQUEST RECEIVED from " + json.get("username") + " / serial: " + json.get("serial"));
                            updateChatMessages("Chatanfrage erhalten!", json.get("username").toString());
                            setVisibleChat(json.get("username").toString());
                        }
                    }
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

    public void updateChatMessages(String message, String username) {
        //find chat by username
        for(User u : activeusers) {
            if(u.toString().equals(username.replace("\"", ""))) {
                //found correct user
                Platform.runLater(() -> {
                    u.getChat().add(new Message(message, getTimestamp()));
                });
            }
        }
    }

    public void setVisibleChat(String username) {
        //find chat by username
        for(User u : activeusers) {
            if(u.toString().equals(username.replace("\"", ""))) {
                //found correct user
                Platform.runLater(() -> {
                    activechat.clear();
                    activechat.setAll(u.getChat());
                });
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
        this.username.set(username);
        Map<String, String> loginData = new HashMap();
        loginData.put("method", "login");
        loginData.put("username", username);
        loginData.put("password", password);
        loginData.put("tcpport", String.valueOf(portTCP));
        loginData.put("udpport", String.valueOf(portUDP));
        Gson gson = new Gson();
        String loginString = gson.toJson(loginData);
        String response = sendText(loginString);
        System.out.println(response);
        //loadList(response);

    }

    private void loadList(String response) {
        JsonObject json = parseJson(response);
        JsonArray data = json.getAsJsonArray("data");
        System.out.println(data);
        ArrayList<User> users = new ArrayList();
        for(JsonElement j: data) {
            JsonObject user = j.getAsJsonObject();
            users.add(new User(user.get("username").getAsString(), user.get("ip").getAsString()));
        }
        activeusers.removeAll();
        activeusers.addAll(users);
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


    public String getTimestamp() {
        SimpleDateFormat curTime = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date now = new Date();
        return curTime.format(now);
    }

    public void sendChatRequest(String username) throws Exception {
        // Build messageMap
        Map messageMap = new HashMap();
        messageMap.put("method", "request");
        messageMap.put("username", getUsername().getValue());
        messageMap.put("timestamp", getTimestamp());
        messageMap.put("serial", ++serial);
        Gson gson = new Gson();
        byte[] messageBytes = gson.toJson(messageMap).getBytes();

        // UDP Send
        udp_send(messageBytes);
    }

    public void udp_send(byte[] messageBytes) throws Exception {
        int goalPort;
        if(portUDP == 8010) { goalPort = 9010;}
        else { goalPort = 8010; }

        // Build datagram, goalIP
        DatagramSocket clientSendSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        // Send packet
        DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length, IPAddress, goalPort);
        clientSendSocket.send(sendPacket);

    }


}



