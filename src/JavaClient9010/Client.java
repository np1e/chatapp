package JavaClient9010;

import java.net.*;

public class Client {

    private UDPServer server;
    private int receivePort;

    public Client() throws Exception {
        receivePort = 9010;
        (new Thread(new UDPServer(receivePort))).start();
    }

    public void sendmessage(String message, String goalIP, int goalPort) throws Exception {

        // Send datagram
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(goalIP);
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        String sentence = message;
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, goalPort);
        clientSocket.send(sendPacket);
        System.out.println("sent: " + sentence);

        // Wait for acknowledgment
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("received acknowledgment for:" + modifiedSentence);
        clientSocket.close();

    }
}
