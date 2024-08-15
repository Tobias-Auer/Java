package main;

import entity.Player;
import net.GameClient;
import net.GameServer;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {
    //Screen settings
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    private GameClient socketClient;
    private GameServer socketServer;

    //FPS
    final static double FPS = 60;


    //WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;


    public Thread gameThread;
    public KeyHandler keyH = new KeyHandler();
    public CollisonChecker cChecker = new CollisonChecker(this);
    public Player player = new Player(this, keyH, JOptionPane.showInputDialog(this, "Username: "));
    TileManager tileM = new TileManager(this);


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);



    }


    public void startGameThread() {


        if (JOptionPane.showConfirmDialog(this, "Do you want to run the server") == 0 ) {
            socketServer = new GameServer(this);
            socketServer.start();
        }

        socketClient = new GameClient(this, "localhost");
        socketClient.start();

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        socketClient.sendData("ping".getBytes());
        double drawInterval = 1000000000.0/FPS;
        double delta = 0;
        double lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        // Main game loop
        while (gameThread!= null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
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
        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileM.drawTiles(g2);
        player.draw(g2);
        g2.dispose();

    }


}
