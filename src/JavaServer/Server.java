package JavaServer;
import com.google.gson.*;
import com.sun.javafx.collections.ObservableMapWrapper;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Server {


    private PostgreSQLJDBC db;
    private SimpleStringProperty logs;
    private String port;
    private ObservableList<User> activeUsersObservable;
    private Data userData;
    private Logger logger;
    private TCPClient client;
    private String input;


    public ObservableList<User> getActiveUsers() {
        return activeUsersObservable;
    }

    ServerSocket welcomeSocket;
    public Server(Logger l, ObservableList list, TCPClient client) {

        this.client = client;
        userData = new Data();
        logs = new SimpleStringProperty();
        logger = l;
        //db = new PostgreSQLJDBC();
        activeUsersObservable = list;

    }

    public void start(final String port) {

        try {
            welcomeSocket = new ServerSocket(8080);

            setLogs("Server started on " + welcomeSocket.getInetAddress().getLocalHost().getHostAddress() + ":" + welcomeSocket.getLocalPort());
            System.out.println("1");
            while(true) {
                try {
                    setLogs("Waiting for client...");
                    final Socket connectionSocket = welcomeSocket.accept();

                    setLogs("Client connected.");

                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("thread started");
                            final InputStream stream;
                            try {
                                final BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
                                while((input = reader.readLine()) != null) {
                                    handleRequest(connectionSocket, input, writer);
                                }
                                setLogs("Request handled");
                            } catch (IOException e) {
                                setLogs(e.getMessage());
                            }
                        }
                    });

                    thread.start();
                } catch (IOException e) {
                    setLogs(e.getMessage());
                }
            }

        } catch (IOException e) {
            setLogs(e.getMessage());
        }

    }

    // handles the request from a client
    private void handleRequest(Socket connectionSocket, String jsonString, BufferedWriter writer) {

        JsonObject request = parseJson(jsonString);
        System.out.println("Json parsed");

        String username;
        String password;
        String port;

        switch(request.get("method").getAsString()){
            case "login":
                username = request.get("username").getAsString();
                password = request.get("password").getAsString();
                port = request.get("tcpport").getAsString();
                setLogs("requesting login");
                if(checkAuthentication(username,password)) {
                    activeUsersObservable.add(new User(username, connectionSocket.getInetAddress().getHostAddress(), Integer.parseInt(port)));
                    setLogs(username + " logged in successfully.");
                    sendConfirmation("login", "1", writer);
                    sendActiveUserList(writer);
                } else {
                    setLogs("Authentication failed.");
                }

                break;
            case "register":
                username = request.get("username").getAsString();
                password = request.get("password").getAsString();
                setLogs("requesting register");
                if(!userData.exists(username)) {
                    if (!password.equals(request.get("confirm").getAsString())) {
                        setLogs("passwords must match");
                    } else {
                        userData.insert(username, password);
                    }
                }
                break;
            case "logout":
                username = request.get("username").getAsString();
                setLogs("requesting logout");
                for(User u: activeUsersObservable) {
                    if(u.toString().equals(username)) {
                        activeUsersObservable.remove(u);
                        setLogs("User <" + username + "> logged out");
                        sendConfirmation("logout", "1", writer);
                        sendActiveUserList(writer);
                        break;
                    }
                }

        }

    }

    private void sendConfirmation(String method, String status, BufferedWriter writer) {
        JsonObject json = new JsonObject();
        json.addProperty("method", "confirmation");
        json.addProperty("type", method);
        json.addProperty("status", status);
        sendMessage(toJson(json), writer);
    }

    private boolean checkAuthentication(String username, String password_hash) {

        return userData.getPW(username).equals(password_hash);

    }

    private String toJson(JsonObject json) {
        Gson gson = new Gson();
        return gson.toJson(json);
    }

    private void sendActiveUserList(BufferedWriter writer) {
        JsonArray users = new JsonArray();
        JsonObject json = new JsonObject();
        json.addProperty("method","data");
        for(User u : activeUsersObservable) {
            JsonObject user = new JsonObject();
            user.addProperty("username", u.toString());
            user.addProperty("ip", u.getIp());
            users.add(user);
        }
        json.add("data",users);

        String jsonString = toJson(json);
        System.out.println("json = " + jsonString);
        //sendMessage(jsonString, writer);
        sendToAll(jsonString);
    }


    private void sendToAll(String jsonString) {
        client.sendToAll(jsonString);
    }

    private void sendMessage(String jsonString, BufferedWriter writer) {
        jsonString += "\n";
        try {
            writer.write(jsonString, 0, jsonString.length());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String addTerminator(String message) {
        return message + "\n";
    }



    private void setLogs(String log) {
        logger.log(log);
    }


    private JsonObject parseJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();
        return json;
    }


    public String getLogs() {
        return logs.get();
    }

    public SimpleStringProperty logsProperty() {
        return logs;
    }

    public void stop() {
        try {
            welcomeSocket.close();
            setLogs("Server closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
