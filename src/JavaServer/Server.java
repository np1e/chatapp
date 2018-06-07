package JavaServer;
import javafx.beans.property.SimpleStringProperty;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {


    private PostgreSQLJDBC db;
    private SimpleStringProperty logs;
    private String port;

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
                setLogs("login");
                if(checkAuthentication(request.get("username"), request.get("password"))) {
                    sendActiveUserList(connectionSocket);
                } else {
                    //sendErrorToClient()
                }

                break;
            case "register":
                setLogs("register");
                // add new user to db
        }

    }

    private boolean checkAuthentication(String username, String password_hash) {

    }

    private void sendActiveUserList(Socket connectionSocket) {

    }

    private Map parseJson(InputStream in) {

        Map<String, String> data = new HashMap();
        JsonParser parser = Json.createParser(in);
        JsonParser.Event e = null;
        while(e != JsonParser.Event.END_OBJECT) {
            e = parser.next();
            if(e == JsonParser.Event.KEY_NAME) {
                switch (parser.getString()) {
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
                }
            }
        }
        return data;
    }

    public void setLogs(String logs) {
        this.logs.set(logs);
    }

    public String getLogs() {
        return logs.get();
    }

    public SimpleStringProperty logsProperty() {
        return logs;
    }

}
