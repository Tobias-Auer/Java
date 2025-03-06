package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    GamePanel gp;

    // Current position
    public int worldX = 21;
    public int worldY = 21;
    public int speed;
    public String uuid;

    // Previous positions for interpolation
    private float prevX = worldX;
    private float prevY = worldY;
    private long lastUpdateTime;
    private long nextUpdateTime;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea = new Rectangle(8, 16, 31, 31);
    public boolean collisionOn = false;

    public Entity(GamePanel gp) {
        this.gp = gp;
        lastUpdateTime = System.currentTimeMillis();
        nextUpdateTime = lastUpdateTime + 50; // Assuming updates every 50 ms
    }

    public void setAction() {
        // Define actions if needed
    }

    public void update(String givenDirection) {
        // Calculate new position based on direction
        switch (givenDirection) {
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

        // Handle sprite animation
        spriteCounter++;
        if (spriteCounter >= 11) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void updateCoords(int x, int y, String directionP) {
        // Update previous and next positions
        prevX = worldX;
        prevY = worldY;
        worldX = x;
        worldY = y;
        direction = directionP;

        // Update time for interpolation
        lastUpdateTime = System.currentTimeMillis();
        nextUpdateTime = lastUpdateTime + 50; // Assuming updates every 100 ms

        // Handle sprite animation
        spriteCounter++;
        if (spriteCounter >= 2) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        // Interpolate position
        long currentTime = System.currentTimeMillis();
        float alpha = (float)(currentTime - lastUpdateTime) / (nextUpdateTime - lastUpdateTime);
        alpha = Math.max(0, Math.min(1, alpha)); // Clamp alpha between 0 and 1

        float interpolatedX = prevX + (worldX - prevX) * alpha;
        float interpolatedY = prevY + (worldY - prevY) * alpha;

        int screenX = (int)(interpolatedX - gp.player.worldX + gp.player.screenX);
        int screenY = (int)(interpolatedY - gp.player.worldY + gp.player.screenY);

        if (interpolatedX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                interpolatedX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                interpolatedY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                interpolatedY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            switch (direction) {
                case "up":
                    image = (spriteNum == 1) ? up1 : up2;
                    break;
                case "down":
                    image = (spriteNum == 1) ? down1 : down2;
                    break;
                case "left":
                    image = (spriteNum == 1) ? left1 : left2;
                    break;
                case "right":
                    image = (spriteNum == 1) ? right1 : right2;
                    break;
            }
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
