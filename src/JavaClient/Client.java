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
    private SimpleStringProperty activeChatPartner;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private int portUDP;
    private int portTCP;
    private int serial;
    private SimpleStringProperty username;

    public UDPLayer udp;


    public Client(String portUDP, String portTCP, ObservableList activeUsers, ObservableList<Message> activeChat) throws IOException {
        username = new SimpleStringProperty();
        activeusers = activeUsers;
        activechat = activeChat;
        activeChatPartner = new SimpleStringProperty();
        // TCP
        this.portTCP = Integer.parseInt(portTCP);
        socket = new Socket("127.0.1.1", 8080);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // UDP
        this.udp = new UDPLayer(portUDP, this);
        this.portUDP = Integer.parseInt(portUDP);

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
        String response = sendText(jsonString);
    }

    public void exit() throws IOException {
        logout();
        close();
    }

    public ObservableList<Message> getActiveChat() {
        return activechat;
    }

    public void deliver_data(JsonObject json) {
        // Received message
        if(json.get("method").getAsString().equals("message")) {
            String username = json.get("username").toString().replace("\"", "");
            String message = json.get("message").toString().replace("\"", "");
            updateChatMessages(username, message, false);
            setVisibleChat(username);
            udp.setSerial(json.get("serial").getAsInt());
            udp.make_ack();
        }
        // Received request
        if(json.get("method").getAsString().equals("request")) {
            String username = json.get("username").toString().replace("\"", "");
            for(User u: activeusers) {
                if(u.toString().equals(username)) {
                    u.setRequested(true);
                }
            }
            updateChatMessages(username,"Chatanfrage erhalten!",false);
            updateChatMessages(username,"Annehmen?",true);
            setVisibleChat(username);
            udp.setSerial(json.get("serial").getAsInt());
            udp.make_ack();
        }
        // Received request
        if(json.get("method").getAsString().equals("ack")) {
            System.out.println("received ack / serial: " + json.get("serial"));
        }
        // Received confirmation
        if(json.get("method").getAsString().equals("confirm")) {
            String username = json.get("username").toString().replace("\"", "");
            for(User u: activeusers) {
                if(u.toString().equals(username)) {
                    u.setConfirmed(true);
                }
            }
            updateChatMessages(username, "Chatanfrage wurde akzeptiert!", false);
            setVisibleChat(username);

            udp.setSerial(json.get("serial").getAsInt());
            udp.make_ack();
        }
        // Received decline
        if(json.get("method").getAsString().equals("decline")) {
            String username = json.get("username").toString().replace("\"", "");
            for(User u: activeusers) {
                if(u.toString().equals(username)) {
                    u.setConfirmed(false);
                }
            }
            updateChatMessages(username, "Chatanfrage wurde abgelehnt!", false);
            setVisibleChat(username);

            udp.setSerial(json.get("serial").getAsInt());
            udp.make_ack();
        }
    }

    public void updateChatMessages(String username, String message, boolean confirm) {
        //find chat by username
        for(User u : activeusers) {
            if(u.toString().equals(username)) {
                //found correct user
                Platform.runLater(() -> {
                    u.getChat().add(new Message(message, getTimestamp(), u, confirm));
                    activechat.setAll(u.getChat());
                });
            }
        }
    }

    public String getActiveChatPartner() {
        return activeChatPartner.get();
    }

    public SimpleStringProperty activeChatPartnerProperty() {
        return activeChatPartner;
    }

    public void setActiveChatPartner(String activeChatPartner) {
        this.activeChatPartner.set(activeChatPartner);
    }

    public void setVisibleChat(String username) {
        //find chat by username
        for(User u : activeusers) {
            if(u.toString().equals(username)) {
                //found correct user
                Platform.runLater(() -> {
                    activechat.setAll(u.getChat());
                    activeChatPartner.set(u.toString());
                });
            }
        }
    }

    public void login(String username, String password) throws IOException {
        // Request login at server
        this.username.set(username);
        Map<String, String> loginData = new HashMap();
        loginData.put("method", "login");
        loginData.put("username", username);
        loginData.put("password", new StringBuilder(password).reverse().toString());
        loginData.put("tcpport", String.valueOf(portTCP));
        loginData.put("udpport", String.valueOf(portUDP));
        Gson gson = new Gson();
        String loginString = gson.toJson(loginData);
        String response = sendText(loginString);
    }

    public void register(String username, String password, String confirm) throws IOException {
        // Request login at server
        this.username.set(username);
        Map<String, String> registerData = new HashMap();
        registerData.put("method", "register");
        registerData.put("username", username);
        registerData.put("password", new StringBuilder(password).reverse().toString());
        registerData.put("confirm", new StringBuilder(confirm).reverse().toString());
        Gson gson = new Gson();
        String registerString = gson.toJson(registerData);
        String response = sendText(registerString);

        JsonObject json = parseJson(response);
        // Received message
        if(json.get("method").getAsString().equals("confirmation")) {
            if(json.get("type").getAsString().equals("register")) {
            int status = json.get("status").getAsInt();
            if(status == 0) {
                System.out.println("Passwords must match");
            }
            if(status == 1) {
                System.out.println("Username already in use");

            }
            if(status == 2) {
                System.out.println("Successful");
            }
            }
        }

    }

    private void loadList(String response) {
        JsonObject json = parseJson(response);
        JsonArray data = json.getAsJsonArray("data");
        ArrayList<User> users = new ArrayList();
        for(JsonElement j: data) {
            JsonObject user = j.getAsJsonObject();
            users.add(new User(user.get("username").getAsString(), user.get("ip").getAsString()));
        }
        activeusers.removeAll();
        activeusers.addAll(users);
    }

    private JsonObject parseJson(String jsonString) {
        JsonParser parser = new com.google.gson.JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();
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


}



