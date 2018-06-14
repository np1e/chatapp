package JavaServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.javafx.collections.ObservableMapWrapper;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Server {


    private PostgreSQLJDBC db;
    private SimpleStringProperty logs;
    private String port;
    private Map<String, String> activeUsers = new HashMap<String, String>();
    private ObservableMap<String, String> activeUsersObservable;
    private Data userData;


    public ObservableMap<String, String> getActiveUsers() {
        return activeUsersObservable;
    }

    ServerSocket welcomeSocket;
    public Server() {

        userData = new Data();
        logs = new SimpleStringProperty();
        //db = new PostgreSQLJDBC();
        activeUsersObservable = FXCollections.observableMap(activeUsers);

    }

    public void start(final String port) {

        try {
            welcomeSocket = new ServerSocket(Integer.parseInt(port));
            setLogs("Server started on " + welcomeSocket.getInetAddress().getLocalHost().getHostAddress() + ":" + welcomeSocket.getLocalPort());
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
                                handleRequest(connectionSocket, reader, writer);
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
    private void handleRequest(Socket connectionSocket, BufferedReader reader, BufferedWriter writer) {

        com.google.gson.JsonObject request = parseJson(reader);
        System.out.println("Json parsed");

        String username = request.get("username").getAsString();
        String password = request.get("password").getAsString();

        switch(request.get("method").getAsString()){
            case "login":
                setLogs("requesting login");
                if(checkAuthentication(username,password)) {
                    activeUsersObservable.put(username, connectionSocket.getInetAddress().toString());
                    setLogs(username + " logged in successfully.");
                    sendActiveUserList(writer);
                } else {
                    setLogs("Authentication failed.");
                }

                break;
            case "register":
                setLogs("requesting register");
                if(!userData.exists(username)) {
                    if (!password.equals(request.get("confirm").getAsString())) {
                        setLogs("passwords must match");
                    } else {
                        userData.insert(username, password);
                    }

                }
        }

    }

    private boolean checkAuthentication(String username, String password_hash) {

        return userData.getPW(username).equals(password_hash);

    }

    private void sendActiveUserList(BufferedWriter writer) {
        Gson gson = new Gson();
        String json = gson.toJson(activeUsersObservable);
        System.out.println("json = " + json);
        try {
            writer.write(json, 0, json.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void setLogs(String log) {
        SimpleDateFormat curTime = new SimpleDateFormat("dd-MM-yyy HH:mm:ss.SSS");
        Date now = new Date();
        String timeStamp = curTime.format(now);
        String string = "[" + timeStamp + "]" + "\n\t" + log;
        logs.set(string);
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

    /**@TODO implement
     *
     * @param text
     */
    public void doCommand(String text) {
    }
}
