package net;

import main.GamePanel;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class GameClient extends Thread  {
    private InetAddress ipAddress;
    private DatagramSocket socket;
    private GamePanel gp;


    public GameClient(GamePanel gp, String ipAddress) {
        this.gp = gp;
        try {this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }


    }

    public void run() {
        while (gp.gameThread!= null) {
            byte[] data = new byte [1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("SERVER: " + new String(packet.getData()).trim());
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
        try {
            //System.out.println(("Sending: " + Arrays.toString(data)).trim());
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
