package JavaServer;
import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import jdk.nashorn.internal.parser.JSONParser;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Server {


    private PostgreSQLJDBC db;
    private SimpleStringProperty logs;
    private String port;
    private Map<String, String> activeUsers;

    public Server() {

        logs = new SimpleStringProperty();
        db = new PostgreSQLJDBC();
    }

    public void start(final String port) {

        try {
            final ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(port));

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
                                stream = connectionSocket.getInputStream();
                                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
                                handleRequest(connectionSocket, stream, writer);
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
    private void handleRequest(Socket connectionSocket, InputStream stream, BufferedWriter writer) {

        Map<String, String> request = parseJson(stream);
        System.out.println("Json parsed");

        switch(request.get("method").toLowerCase()){
            case "login":
                setLogs("requesting login");
                if(checkAuthentication(request.get("username"), request.get("password"))) {
                    activeUsers.put(request.get("username"), connectionSocket.getInetAddress().toString());
                    sendActiveUserList(writer);
                } else {
                    setLogs("Authentication failed.");
                }

                break;
            case "register":
                setLogs("requesting register");
                try {
                    ResultSet user = db.getUser(request.get("username"));
                    setLogs("User already exists.");
                } catch (SQLException e) {
                    if(request.get("password") != request.get("confirm")) {
                        setLogs("passwords must match");
                    } else {
                        try {
                            db.insert(request.get("username"), request.get("password"), connectionSocket.getInetAddress().toString());
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


        }

    }

    private boolean checkAuthentication(String username, String password_hash) {
        ResultSet user = null;
        try {
            user = db.getUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String password = null;
        try {
            password = user.getString(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return password == password_hash;
    }

    private void sendActiveUserList(BufferedWriter writer) {
        Gson gson = new Gson();
        String json = gson.toJson(activeUsers);
        System.out.println("json = " + json);
        try {
            writer.write(json, 0, json.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void setLogs(String log) {
        logs.set(log);
    }


    private Map parseJson(InputStream in) {

        Map<String, String> data = new HashMap();

        JsonParser parser = Json.createParser(in);
        JsonParser.Event e = null;
        while(e != JsonParser.Event.END_OBJECT) {
            e = parser.next();
            if(e == JsonParser.Event.KEY_NAME) {
                switch(parser.getString()) {
                    case "method":
                        parser.next();
                        data.put("method", parser.getString());
                        break;
                    case "username":
                        parser.next();
                        data.put("username", parser.getString());
                        break;
                    case "password":
                        parser.next();
                        data.put("password", parser.getString());
                        break;
                }
            }
        }
        return data;
    }


    public String getLogs() {
        return logs.get();
    }

    public SimpleStringProperty logsProperty() {
        return logs;
    }

}
