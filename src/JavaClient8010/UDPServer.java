package JavaClient8010;

import java.io.IOException;
import java.net.*;

class UDPServer implements Runnable
{

    private static int receivePort;
    public UDPServer(int receivePort1) {
        receivePort = receivePort1;
    }

    public void run() {
        System.out.println("server established");
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
            System.out.println("waiting for data");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sentence = new String(receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);

        }
    }
}