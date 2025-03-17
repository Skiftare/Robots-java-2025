package gui.ui;

import java.awt.*;

public class MovableObject {
    private int cellX;
    private int cellY;
    private Image texture;

    public MovableObject(int x, int y, String texturePath) {
        this.cellX = x;
        this.cellY = y;

        // Загружаем текстуру объекта
        try {
            texture = Toolkit.getDefaultToolkit().getImage(texturePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosition(int x, int y) {
        this.cellX = x;
        this.cellY = y;
    }

    public int[] getPosition() {
        return new int[]{cellX, cellY};
    }

    public Image getTexture() {
        return texture;
    }

    public void draw(Graphics2D g, int cellSize, Point start) {
        int objectWidth = (int) (cellSize * 0.8);  // Пропорциональный размер
        int objectHeight = (int) (cellSize * 0.8);
        int x = start.x + cellX * cellSize + (cellSize - objectWidth) / 2;
        int y = start.y + cellY * cellSize + (cellSize - objectHeight) / 2;
        g.drawImage(texture, x, y, objectWidth, objectHeight, null);
    }
}
