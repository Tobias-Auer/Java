package entity;

import main.GamePanel;
import main.KeyHandler;
import main.Network;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Player extends Entity {

    KeyHandler keyH;
    public final int screenX, screenY;
    private boolean debug;
    private int sendCounter = 0;
    private String lastNetworkMessage;
//    private String username;

    public Player(GamePanel gp, KeyHandler keyH, String id) {
        super(gp);
        this.keyH = keyH;
        uuid = id;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        setDefaultValues();
        getPlayerImage();
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/walking_sprites/boy_right_2.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }

    public void update(Network network) {

        if (keyH.downPressed || keyH.upPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                direction = "up";
            }
            if (keyH.downPressed) {
                direction = "down";
            }
            if (keyH.leftPressed) {
                direction = "left";
            }
            if (keyH.rightPressed) {
                direction = "right";
            }
            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }
            spriteCounter++;
            if (spriteCounter >= 11) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }if (sendCounter >= 3){
            sendCounter = 0;
            String networkMessage = "posUpdate=" + gp.id + "," + worldX + "," + worldY + "," + direction;
            if (!networkMessage.equals(lastNetworkMessage)) {
                network.sendMsg(networkMessage);
                lastNetworkMessage = networkMessage;
            }
        } else {
            sendCounter +=1;
        }
        this.debug = keyH.debug;


    }
}

