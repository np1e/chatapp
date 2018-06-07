package JavaClient9010;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class UDPServer implements Runnable
{

    private static int receivePort;
    public UDPServer(int receivePort1) {
        receivePort = receivePort1;
    }

    public void run() {
        System.out.println("-- server established --");
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(receivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while(true)
        {
            System.out.println("-- waiting for data --");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sentence = new String(receivePacket.getData());
            String sender = new String(String.valueOf(receivePacket.getAddress()));
            String portst = new String(String.valueOf(receivePacket.getPort()));
            System.out.println("received: " + sentence  + ", from: " + sender + ", port: " + portst);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            try {
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}