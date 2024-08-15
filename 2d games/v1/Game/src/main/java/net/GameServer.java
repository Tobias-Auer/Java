package net;

import entity.Player;
import entity.PlayerMP;
import main.GamePanel;
import net.packet.Packet;
import net.packet.Packet00Login;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameServer extends Thread {
    private DatagramSocket socket;
    private GamePanel gp;

    private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();


    public GameServer(GamePanel gp) {
        this.gp = gp;
        try {
            this.socket = new DatagramSocket(1331);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }


    }

    public void run() {
        while (true) {

            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
//            String message = new String(packet.getData()).trim();
//            System.out.println("CLIENT[" + packet.getAddress() + ":" + packet.getPort() + "]> " + message);
//            if (message.equalsIgnoreCase("ping")) {
//                sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
//            }

        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0,2));
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                Packet00Login packet = new Packet00Login(data);
                System.out.println("[" + address.getHostAddress()+":"+port + "] " + packet.getUserName() + "has connected...");
                PlayerMP player = null;
                if (address.getHostAddress().equals("127.0.0.1")) {
                    player = new PlayerMP(gp,  gp.keyH, packet.getUserName(), address, port);
                } else {
                    player = new PlayerMP(gp, packet.getUserName(), address, port);
                }
                if(player != null) {
                    this.connectedPlayers.add(player);

                }


                break;
            case DISCONNECT:
                break;
        }
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
            sendData(data, p.ipAddress, p.port);
        }
    }
}
