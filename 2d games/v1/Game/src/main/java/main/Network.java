package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Network {
    public static final int HEADER = 10;
    private static final int PORT = 9991;
    private static final String server = "t-auer.com";
    public static PrintWriter pr;
    private static Socket socket;
    private static final boolean keepConnected = true;
    private final GamePanel gp;

    String id = null;
    String worldX = null;
    String worldY = null;
    String direction = null;

    public Network(GamePanel gp) {
        this.gp = gp;
    }


    public boolean connectToServer() {
        while (true) {
            try {
                socket = new Socket(server, PORT);
                socket.setReuseAddress(true);
                pr = new PrintWriter(socket.getOutputStream());
                System.out.println("Connected to the server.");

                // Start the socket listener in a new thread
                Thread listenerThread = new Thread(this::startSocketListener);
                listenerThread.start();

                break;
            } catch (IOException e) {
                System.out.println("Connection failed, retrying in 5 seconds: " + e.getMessage());
                try {
                    Thread.sleep(5000); // Retry interval
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        return true;
    }

    private void startSocketListener() {
        System.out.println("Starting socket listener...");
        try {
            InputStream input = socket.getInputStream();
            byte[] headerBuffer = new byte[HEADER];
            long lastHeartbeat = System.currentTimeMillis();

            while (true) {
                // Check for heartbeat timeout
                if ((System.currentTimeMillis() - lastHeartbeat) >= 7000) {
                    System.out.println("HOST IS DOWN!! Attempting to reconnect...");
                    break;
                }

                // Check if there's data to read
                if (input.available() > 0) {
                    System.out.println("Got message from server");
                    int bytesRead = input.read(headerBuffer);
                    if (bytesRead == -1) {
                        throw new IOException("Connection closed by server");
                    }

                    String headerStr = new String(headerBuffer, 0, bytesRead, StandardCharsets.UTF_8).trim();
                    int messageLength = Integer.parseInt(headerStr);
                    byte[] messageBuffer = new byte[messageLength];
                    bytesRead = input.read(messageBuffer);
                    if (bytesRead == -1) {
                        throw new IOException("Connection closed by server");
                    }

                    String message = new String(messageBuffer, 0, bytesRead, StandardCharsets.UTF_8);
                    System.out.println("Received message: " + message);

                    if (message.equals("!heartbeat")) {
                        sendMsg("!BEAT");
                        lastHeartbeat = System.currentTimeMillis();
                    } else if (message.contains("!newPlayer=")) {
                        String sendId = message.substring("!newPlayer=".length());
                        System.out.println("New player connected: " + sendId);
                        if (!sendId.equals(gp.id.toString())) {
                            gp.newPlayer(sendId);
                            gp.shareCurrentPos();
                        }
                    }
                    else if (message.contains("!posUpdate=")) {
                        extractValues(message);
                        gp.updateOnlinePlayer(Integer.parseInt(worldX), Integer.parseInt(worldY), direction);
                    }
                    else if (message.contains("!oldPlayer=")) {
                        extractValues(message);
                        gp.spawnOldPlayer(direction,Integer.parseInt(worldX), Integer.parseInt(worldY));
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Connection lost: " + e.getMessage());
        }
    }

    private void extractValues(String message) {
        message = message.substring(1);

        // Split the string by commas to get key-value pairs
        String[] pairs = message.split(",");
        id = pairs[0];
        worldX = pairs[1];
        worldY = pairs[2];
        direction = pairs[3];
    }


    public void sendMsg(String msg) {
        byte[] message = msg.getBytes(StandardCharsets.UTF_8);
        int msgLen = message.length;
        String sendLen = String.format("%-" + HEADER + "s", msgLen);
        System.out.println("SENDLEN: '" + sendLen + "'");
        pr.print(sendLen);
        pr.flush();

        System.out.println("Sending message: " + msg);
        pr.print(msg);
        pr.flush();
    }

}