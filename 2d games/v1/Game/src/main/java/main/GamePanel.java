package main;

import entity.OnlinePLayer;
import entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class GamePanel extends JPanel implements Runnable {
    //Screen settings
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public Network network = new Network(this);
    public OnlinePLayer onlinePlayer;

    //FPS
    final static double FPS = 60;

    //WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final String id = UUID.randomUUID().toString();

    public Thread gameThread;
    public KeyHandler keyH = new KeyHandler();
    public CollisonChecker cChecker = new CollisonChecker(this);
    public Player player = new Player(this, keyH, id);



    TileManager tileM = new TileManager(this);


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        if (network.connectToServer()) {
            network.sendMsg("newPlayer="+id);
        }


    }

    public void newPlayer(String id) {
        onlinePlayer = new OnlinePLayer(this, id);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0/FPS;
        double delta = 0;
        double lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        // Main game loop
        while (gameThread!= null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (long) (currentTime - lastTime);
            lastTime = currentTime;


            if (delta >= 1) {
                update();
                repaint();
                if (Objects.equals(System.getProperty("os.name"), "Linux")) Toolkit.getDefaultToolkit().sync(); // fix for linux's graphics scheduling 'bug'
                delta--;
            }
            if (timer >= 1000000000) {
                timer = 0;
            }
        }

    }
    public void update() {
        player.update(network);
    }
    public void updateOnlinePlayer(int x, int y, String direction) {
        onlinePlayer.updateCoords(x,y, direction);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileM.drawTiles(g2);
        player.draw(g2);
        if (onlinePlayer!= null) {
            onlinePlayer.draw(g2);
        }


        g2.dispose();

    }


    public void spawnOldPlayer(String direction, int worldX, int worldY) {
        this.onlinePlayer = new OnlinePLayer(this, "oldPlayer");
//        this.onlinePlayer.direction = direction;
        this.onlinePlayer.updateCoords(worldX, worldY, direction);

    }

    public void shareCurrentPos() {
        network.sendMsg("currentPos=" + id + "," + player.worldX + "," + player.worldY + "," + player.direction);
    }
}
