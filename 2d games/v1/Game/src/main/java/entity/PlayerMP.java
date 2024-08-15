package entity;

import main.GamePanel;
import main.KeyHandler;

import java.net.InetAddress;

public class PlayerMP extends Player{

    public InetAddress ipAddress;
    public int port;

    public PlayerMP(GamePanel gp, KeyHandler keyH, String username, InetAddress ipAddress, int port) {
        super(gp, keyH, username);
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public PlayerMP(GamePanel gp, String username, InetAddress ipAddress, int port) {
        super(gp, null, username);
        this.ipAddress = ipAddress;
        this.port = port;
    }
}
