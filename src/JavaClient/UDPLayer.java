package JavaClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UDPLayer {

    private int udp_port;
    private int serial;
    private Client client;

    public UDPLayer(String udp_port, Client client) throws IOException {
        this.serial = 0;
        this.client = client;
        // udp_rcv
        this.udp_port = Integer.parseInt(udp_port);
        new Thread(new udp_rcv(Integer.parseInt(udp_port))).start();
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    // ----- Receiving data ------

    // Waiting for arriving udp-messages
    public class udp_rcv implements Runnable {
        private int listenOnPort;
        public udp_rcv(int port) {
            listenOnPort = port;
        }
        public void run() {

            // Build datagram
            DatagramSocket serverSocket = null;
            try { serverSocket = new DatagramSocket(listenOnPort); }
            catch(SocketException e) { e.printStackTrace(); }

            // Build
            byte[] receiveData = new byte[1024];

            while(true) {
                // Wait for message
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try { serverSocket.receive(receivePacket); }
                catch (IOException e) { e.printStackTrace(); }

                // Received packet
                JsonObject json = udp_extract(new String(receivePacket.getData(), 0, receivePacket.getLength()));

                // Deliver data to client.deliver_data()
                client.deliver_data(json);
            }
        }
    }

    // Extract udp-messages
    public JsonObject udp_extract(String jsonString) {
        JsonParser parser = new com.google.gson.JsonParser();
        return parser.parse(jsonString).getAsJsonObject();
    }

    // ----- Sending data ------

    // Build pkt_map for chat-message
    public void make_chatmsg(String username, String content) {
        Map pkt_map = new HashMap();
        pkt_map.put("method", "message");
        pkt_map.put("username", username);
        pkt_map.put("message", content);
        pkt_map.put("timestamp", get_timestamp());
        pkt_map.put("serial", ++serial);

        make_pkt(pkt_map);
    }

    // Build pkt_map for chat-request
    public void make_chatreq(String username) {
        Map pkt_map = new HashMap();
        pkt_map.put("method", "request");
        pkt_map.put("username", username);
        pkt_map.put("timestamp", get_timestamp());
        pkt_map.put("serial", ++serial);

        make_pkt(pkt_map);
    }

    // Build pkt_map for ack
    public void make_ack() {
        Map pkt_map = new HashMap();
        pkt_map.put("method", "ack");
        pkt_map.put("serial", serial);

        make_pkt(pkt_map);
    }

    // Building packets (messages, ack, ...)
    public void make_pkt(Map pkt_map) {
        Gson gson = new Gson();
        byte[] bytes = gson.toJson(pkt_map).getBytes();

        try {
            udp_send(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send udp
    public void udp_send(byte[] messageBytes) throws Exception {
        int goalPort;
        if(udp_port == 8010) { goalPort = 9010;}
        else { goalPort = 8010; }

        // Build datagram, goalIP
        DatagramSocket clientSendSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        // Send packet
        DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length, IPAddress, goalPort);
        clientSendSocket.send(sendPacket);

    }

    // Get timestamp as string
    public String get_timestamp() {
        SimpleDateFormat curTime = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date now = new Date();
        return curTime.format(now);
    }

}
