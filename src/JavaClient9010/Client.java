package JavaClient9010;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jmx.snmp.Timestamp;

import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Client {

    private int receivePort;

    public Client() throws SocketException {
        receivePort = 9010;
        new Thread(new clientReceive(receivePort)).start();
    }

    public void clientSend(String message, String goalIP, int goalPort) throws Exception {
        // Build datagram, goalIP
        DatagramSocket clientSendSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(goalIP);

        // Create timestamp

        // Build messagePacket
        Map sendData = new HashMap();
        sendData.put("message", message);
        Gson gson = new Gson();
        byte[] sendBytes = gson.toJson(sendData).getBytes();

        // Send packet
        DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, IPAddress, goalPort);
        clientSendSocket.send(sendPacket);

        // Wait for acknowledgment
        byte[] receiveData = new byte[sendBytes.length];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSendSocket.receive(receivePacket);
        // Receive acknowledgment
        String ack = new String(receivePacket.getData());

        // Close socket
        clientSendSocket.close();
    }

    public class clientReceive implements Runnable {
        private int receivePort;
        public clientReceive(int listenTo) {
            receivePort = listenTo;
        }
        public void run() {

            // Build datagram
            DatagramSocket serverSocket = null;
            try { serverSocket = new DatagramSocket(receivePort); }
            catch(SocketException e) { e.printStackTrace(); }

            // Build
            byte[] receiveData = new byte[1024];
            byte[] ackData;

            while(true) {
                // Wait for message
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try { serverSocket.receive(receivePacket); }
                catch (IOException e) { e.printStackTrace(); }

                // Received message
                String jsonString = new String(receivePacket.getData(), 0, receivePacket.getLength());
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                JsonObject json = parser.parse(jsonString).getAsJsonObject();
                System.out.println("____________________");
                System.out.println("message:"+ json.get("message"));
                System.out.println("timestamp:"+ json.get("timestamp"));
                String adress = new String(String.valueOf(receivePacket.getAddress()));
                String port = new String(String.valueOf(receivePacket.getPort()));

                // Build and send acknowledgment
                InetAddress goalIP = receivePacket.getAddress();
                int goalPort = receivePacket.getPort();
                ackData = jsonString.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(ackData, ackData.length, goalIP, goalPort);
                try { serverSocket.send(sendPacket); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }

    }


}
