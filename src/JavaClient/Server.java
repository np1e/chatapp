package JavaClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private ServerSocket welcomeSocket;
    private ObservableList<User> activeUsers;
    private SimpleStringProperty username;

    public Server(String port, ObservableList activeUsers, SimpleStringProperty username) {
        this.port = Integer.parseInt(port);
        this.activeUsers = activeUsers;
        this.username = username;

    }

    public void start() {
        try {
            welcomeSocket = new ServerSocket(port);
            System.out.println("Client server started on " + welcomeSocket.getInetAddress().getLocalHost().getHostAddress() + ":" + welcomeSocket.getLocalPort());

            while(true) {
                try {
                    System.out.println("Waiting for server...");
                    final Socket connectionSocket = welcomeSocket.accept();
                    System.out.println("Server connected.");

                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("thread started");
                            final InputStream stream;
                            try {
                                final BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                                loadList(reader);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    });

                    thread.start();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadList(BufferedReader reader) {
        JsonObject json = parseJson(reader);
        JsonArray data = json.getAsJsonArray("data");
        System.out.println(data);
        ArrayList<User> users = new ArrayList();
        for (JsonElement j : data) {
            System.out.println("User: " + j);
            JsonObject user = j.getAsJsonObject();
            users.add(new User(user.get("username").getAsString(), user.get("ip").getAsString()));
        }
        Platform.runLater(() -> {

            activeUsers.clear();
            activeUsers.addAll(users);

            for(User u: activeUsers) {
                if(u.toString().equals(username.getValue())){
                    System.out.println("found " + u.toString());
                    System.out.println("username: " + username.getValue());
                    User me = u;
                    activeUsers.remove(me);
                }
            }
            System.out.println("Updated list");
        });

    }

    private JsonObject parseJson(BufferedReader reader) {

        StringBuilder sb = new StringBuilder();
        String jsonString = null;
        try {
            jsonString = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonParser parser = new com.google.gson.JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();
        return json;

    }

    public void exit() {
        try {
            welcomeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
