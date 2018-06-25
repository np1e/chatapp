package JavaClient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.Checksum;

public class UDPLayer {

    private int udp_port;
    private int serial;
    private Map<Object, Map> serialized_chat;
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

                // Check if message has hashcode
                if(json.has("hashcode")) {
                    // Not corrupted
                    client.deliver_data(json);
                    if(!udp_corrupted(json))  {
                        // Deliver data to client.deliver_data(), client.deliver_data() triggers ack
                        client.deliver_data(json);
                    }
                    // Corrupted!
                    else {
                        // Send nak (and ask for repetition)!
                        make_nak();
                    }
                }
                // Has no hashcode -> ACK/NAK
                else {
                    // ACK
                    if(json.get("method").getAsString().equals("ack")) {
                        System.out.println("ACK!");
                        //serialized_chat.remove(json.get("serial").getAsInt());
                    }
                    // NAK
                    else if(json.get("method").getAsString().equals("nak")) {
                        System.out.println("NAK!");
                        //Map pkt_map = serialized_chat.get(json.get("serial").getAsInt());
                        //remake_pkt(pkt_map);
                    }
                }

            }
        }
    }

    // Extract udp-messages
    public JsonObject udp_extract(String jsonString) {
        JsonParser parser = new com.google.gson.JsonParser();
        return parser.parse(jsonString).getAsJsonObject();
    }

    // Check hashcode
    public boolean udp_corrupted(JsonObject json) {
        // Get transmitted hashcode
        int tran_hashcode = json.get("hashcode").getAsInt();

        // Remove hashcode field and re-calculate hashcode
        json.remove("hashcode");
        Map<String,String> pkt_map = new Gson().fromJson(json, Map.class);
        int calc_hashcode = pkt_map.hashCode();

        if(tran_hashcode == calc_hashcode) {
            return false;
        }
        return true;
    }

    // ----- Sending data ------

    // Build pkt_map for chat-message
    public void make_chatmsg(String content) {
        Map pkt_map = new HashMap();
        pkt_map.put("method", "message");
        pkt_map.put("username", client.getUsername().getValue());
        pkt_map.put("message", content);
        pkt_map.put("timestamp", get_timestamp());
        pkt_map.put("serial",  String.valueOf(++serial));
        pkt_map.put("hashcode", pkt_map.hashCode());

        make_pkt(serial, pkt_map);
    }

    // Build pkt_map for chat-request
    public void make_chatreq() {
        Map pkt_map = new HashMap();
        pkt_map.put("method", "request");
        pkt_map.put("username", client.getUsername().getValue());
        pkt_map.put("timestamp", get_timestamp());
        pkt_map.put("serial", ++serial);
        pkt_map.put("hashcode", pkt_map.hashCode());

        make_pkt(serial, pkt_map);
    }

    // Build pkt_map for chat-confirm
    public void make_chatconf() {

    }

    // Build pkt_map for ack
    public void make_ack() {
        Map pkt_map = new HashMap();
        pkt_map.put("method", "ack");
        pkt_map.put("serial", serial);

        make_pkt(serial, pkt_map);
    }

    // Build pkt_map for nak
    public void make_nak() {
        Map pkt_map = new HashMap();
        pkt_map.put("method", "nak");
        pkt_map.put("serial", serial);

        make_pkt(serial, pkt_map);
    }

    // Building packets (messages, ack, ...)
    public void make_pkt(int serial, Map pkt_map) {
        // Store pkt_map in serialized_chat, pkt_map gets removed, if ack is received
        //serialized_chat.put(serial, pkt_map);
        System.out.println("makepkt");

        Gson gson = new Gson();
        byte[] bytes = gson.toJson(pkt_map).getBytes();

        try {
            udp_send(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Rebuilding packets (messages, ack, ...)
    public void remake_pkt(Map pkt_map) {
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
