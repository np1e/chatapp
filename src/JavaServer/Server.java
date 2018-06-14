package JavaServer;
import com.google.gson.Gson;
import com.sun.javafx.collections.ObservableMapWrapper;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
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


        for( int i = 0; i <4; i++) {
            activeUsersObservable.put("user" + i, "127.0.0.1");

        }
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
                    activeUsersObservable.put(request.get("username"), connectionSocket.getInetAddress().toString());
                    sendActiveUserList(writer);
                } else {
                    setLogs("Authentication failed.");
                }

                break;
            case "register":
                setLogs("requesting register");
                if(!userData.exists(request.get("username"))) {
                    if (request.get("password") != request.get("confirm")) {
                        setLogs("passwords must match");
                    } else {
                        userData.insert(request.get("username"), request.get("password"));
                    }

                }
        }

    }

    private boolean checkAuthentication(String username, String password_hash) {

        return userData.getPW(username) == password_hash;

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
