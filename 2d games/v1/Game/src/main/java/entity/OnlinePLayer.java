package entity;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

public class OnlinePLayer extends Entity {
    public OnlinePLayer(GamePanel gp, String id) {
        super(gp);
        uuid = id;
        direction = "down";
        speed = 4;
        getImage();
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
    }
    public void getImage() {
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

    public void setAction(){

    }

}
