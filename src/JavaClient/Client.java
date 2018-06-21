package JavaClient;

import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Client {

    private ObservableList<User> activeusers;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private int listenOnPort;
    private int serial;


    public Client(String port) throws IOException {
        activeusers = FXCollections.observableArrayList();
        // TCP
        socket = new Socket("localhost", 8080);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // UDP
        listenOnPort = Integer.parseInt(port);
        serial = 0;

    }



    public void login(String username, String password) throws IOException {
        // Request login at server
        Map<String, String> loginData = new HashMap();
        loginData.put("method", "login");
        loginData.put("username", username);
        loginData.put("password", password);
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

    public String getIpByUsername(String username) {
        String ip = "";
        for(User u : activeusers) {
            if(u.toString() == username) {
                ip = u.getIp();
            }
        }
        return ip;
    }

    public String getTimestamp() {
        SimpleDateFormat curTime = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date now = new Date();
        return curTime.format(now);
    }

    public void sendChatRequest(String username) {

        String ip = getIpByUsername(username);
        String timestamp = getTimestamp();

        // Build chatRequestData
        Map chatRequestData = new HashMap();
        chatRequestData.put("method", "message");
        chatRequestData.put("username", username);
        chatRequestData.put("timestamp", timestamp);
        chatRequestData.put("serial", serial++);
        Gson gson = new Gson();
        byte[] chatRequestBytes = gson.toJson(chatRequestData).getBytes();
    }

}



