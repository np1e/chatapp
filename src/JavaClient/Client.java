package JavaClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    private int serial;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private ObservableList activeusers;

    public void login(String username, String password) throws IOException {
        // Request login at server
        Map<String, String> loginData = new HashMap();
        loginData.put("method", "login");
        loginData.put("username", username);
        loginData.put("password", password);
        Gson gson = new Gson();
        String loginString = gson.toJson(loginData);
        String response = sendText(loginString);

        loadList(response);

    }

    private void loadList(String response) {
        for(Map.Entry e: parseJson(response).entrySet()) {
            activeusers.add(new User(e.getKey(), e.getValue()));
        }
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

    public Client() throws IOException {
        socket = new Socket("localhost", 8080);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        activeusers = FXCollections.observableArrayList();
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

    public void sendChatRequest(String username) {
        // Get goalIP

        // Create timestamp
        SimpleDateFormat curTime = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date now = new Date();
        String timestamp = curTime.format(now);

        // Build chatRequestData
        Map chatRequestData = new HashMap();
        chatRequestData.put("method", "message");
        chatRequestData.put("username", username);
        chatRequestData.put("timestamp", timestamp);
        chatRequestData.put("serial", serial++);
        Gson gson = new Gson();
        byte[] chatRequestBytes = gson.toJson(chatRequestData).getBytes();
    }

    /*
    public void main(String[] args) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            final String line = reader.readLine();
            sendText(line);
        }
    }
    */

}



