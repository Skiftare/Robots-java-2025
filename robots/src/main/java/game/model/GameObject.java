package game.model;

import gui.system.rectoring.ResourceLoader;

import java.awt.*;
import java.util.EnumSet;
import java.util.Set;

public class GameObject {
    private int cellX;
    private int cellY;
    private Image texture;
    private String texturePath;
    private String label;
    private String type; // Тип объекта (например, "wall", "baba", "flag")
    private Set<ObjectProperty> properties = EnumSet.noneOf(ObjectProperty.class);

    public GameObject(int x, int y, String texturePath, String label, String type) {
        this.cellX = x;
        this.cellY = y;
        this.texturePath = texturePath;
        this.label = label;
        this.type = type;

        if (texturePath != null) {
            texture = ResourceLoader.getInstance().loadImage(texturePath);
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

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    // Методы для управления свойствами
    public boolean hasProperty(ObjectProperty property) {
        return properties.contains(property);
    }

    public void addProperty(ObjectProperty property) {
        properties.add(property);
    }

    public void removeProperty(ObjectProperty property) {
        properties.remove(property);
    }

    public void clearProperties() {
        properties.clear();
    }

    public void draw(Graphics2D g, int cellSize, Point start) {
        int objectWidth = (int) (cellSize * 0.8);
        int objectHeight = (int) (cellSize * 0.8);
        int x = start.x + cellX * cellSize + (cellSize - objectWidth) / 2;
        int y = start.y + cellY * cellSize + (cellSize - objectHeight) / 2;

        if (texture != null) {
            g.drawImage(texture, x, y, objectWidth, objectHeight, null);
        } else {
            // Запасной вариант отрисовки с текстом
            g.setColor(Color.GRAY);
            g.fillRect(x, y, objectWidth, objectHeight);
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getHeight();
            g.drawString(label, x + (objectWidth - textWidth) / 2,
                    y + (objectHeight + textHeight) / 2 - fm.getDescent());
        }
    }
}