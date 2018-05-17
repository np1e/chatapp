package JavaServer;
import javafx.beans.property.SimpleStringProperty;
import java.io.*;
import java.net.*;

public class Server {


    private SimpleStringProperty logs;
    private String port;

    public Server() {
        logs = new SimpleStringProperty();
    }

    public void start(final String port) {

        Thread server = new Thread() {
            @Override
            public void run() {
                System.out.println("test");
                String clientSentence;
                String capitalizedSentence;
                ServerSocket welcomeSocket = null;
                try {
                    welcomeSocket = new ServerSocket(Integer.parseInt(port));
                    setLogs("Server started.");
                } catch (IOException e) {

                }

                while (true) {
                    Socket connectionSocket = null;
                    try {
                        connectionSocket = welcomeSocket.accept();
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        clientSentence = inFromClient.readLine();

                        setLogs("Received: " + clientSentence);
                        //System.out.println("Received: " + clientSentence);

                        capitalizedSentence = clientSentence.toUpperCase() + '\n';
                        setLogs("Sent: " + capitalizedSentence);
                        outToClient.writeBytes(capitalizedSentence);

                    } catch (Exception e) {

                    }
                }

            }
        };

        server.start();

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
