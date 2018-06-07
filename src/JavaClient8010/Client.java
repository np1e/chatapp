package JavaClient8010;

import java.net.*;

public class Client {

    private UDPServer server;
    private int receivePort;

    public Client() {
        receivePort = 9010;
        (new Thread(new UDPServer(receivePort))).start();
    }

    public void sendmessage(String message, String goalIP, int goalPort) throws Exception {

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(goalIP);
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        String sentence = message;
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, goalPort);
        clientSocket.send(sendPacket);
        //DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        //clientSocket.receive(receivePacket);
        //String modifiedSentence = new String(receivePacket.getData());
        //System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();
        System.out.println("message sent!");

    }
}
