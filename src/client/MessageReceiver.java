package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageReceiver extends Thread {

    private DatagramSocket datagramSocket;
    private InetAddress IPAddress;
    private final int MAX_BUF = 65000;

    public MessageReceiver(DatagramSocket datagramSocket, InetAddress IPAddress) {
        this.datagramSocket = datagramSocket;
        this.IPAddress = IPAddress;
    }

    public void run() {
        while(true) {
            try {
                byte[] data = new byte[MAX_BUF];
                DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
                this.datagramSocket.receive(receivedPacket);
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                if (message.equals("end")) {
                    // fecha client
                    this.datagramSocket.close();
                    System.exit(1);
                } else {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

