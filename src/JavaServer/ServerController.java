package JavaServer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerController {

    private Server server;
    private Logger logger;

    public ServerController(Logger logger) {
        this.logger = logger;
        server = new Server(logger);

        server.logsProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("change");
                logger.log(newValue);
            }
        });
    }

    public void startServer(String port) {
        server.start(port);
    }

    public Server getServer() {
        return server;
    }


    public void stopServer() {
        server.stop();
    }

    public void getIps() {
        Enumeration e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                System.out.println(i.getHostAddress());
            }
        }
    }

    /**@TODO implement
     *
     * @param text
     */
    public void doCommand(String text) {

    }

    public boolean isServerRunning() {
        return server.welcomeSocket.isBound();
    }

    public ObservableList<User> getActiveUsers() {
        return server.getActiveUsers();
    }
}
