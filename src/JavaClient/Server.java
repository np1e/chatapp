package JavaClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private ServerSocket welcomeSocket;
    private ObservableList<User> activeUsers;

    public Server(String port, ObservableList activeUsers) {
        this.port = Integer.parseInt(port);
        this.activeUsers = activeUsers;

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
            JsonObject user = j.getAsJsonObject();
            users.add(new User(user.get("username").getAsString(), user.get("ip").getAsString()));
        }
        activeUsers.removeAll();
        activeUsers.addAll(users);

    }

    private JsonObject parseJson(BufferedReader reader) {

        StringBuilder sb = new StringBuilder();
        String jsonString = null;
        try {
            jsonString = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jsonString);
        JsonParser parser = new com.google.gson.JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();
        System.out.println(json);
        return json;

    }
}
