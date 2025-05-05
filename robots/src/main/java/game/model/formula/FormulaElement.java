package game.model.formula;

import game.model.GameObject;
import game.model.ObjectProperty;

import java.awt.*;

public class FormulaElement extends GameObject {
    public enum ElementType {
        NOUN,    // Object name (e.g., "baba", "wall", "flag")
        VERB,    // Connecting verb (e.g., "is")
        PROPERTY // Property (e.g., "you", "stop", "win")
    }

    private ElementType elementType;
    private String value;
    private ObjectProperty correspondingProperty; // Only for PROPERTY type elements
    private Color backgroundColor;

    public FormulaElement(int x, int y, String texturePath, ElementType elementType, String value) {
        super(x, y, texturePath, value, "formula");
        this.elementType = elementType;
        this.value = value;
        this.correspondingProperty = null;
        addProperty(ObjectProperty.PUSHABLE); // Formula elements can be pushed
    }

    public FormulaElement(int x, int y, String texturePath, ElementType elementType,
                          String value, ObjectProperty correspondingProperty) {
        this(x, y, texturePath, elementType, value);
        this.correspondingProperty = correspondingProperty;
    }

    public void setColor(Color color) {
        this.backgroundColor = color;
    }

    @Override
    public void draw(Graphics2D g, int cellSize, Point start) {
        int objectWidth = (int) (cellSize * 0.8);
        int objectHeight = (int) (cellSize * 0.8);
        int x = start.x + getPosition()[0] * cellSize + (cellSize - objectWidth) / 2;
        int y = start.y + getPosition()[1] * cellSize + (cellSize - objectHeight) / 2;

        // Draw background rectangle with element type color
        g.setColor(backgroundColor != null ? backgroundColor : Color.GRAY);
        g.fillRect(x, y, objectWidth, objectHeight);

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, objectWidth, objectHeight);

        // Draw text
        g.setColor(Color.WHITE);
        // Scale font to fit in box
        Font originalFont = g.getFont();
        int fontSize = getFittingFontSize(g, getValue(), objectWidth, objectHeight);
        g.setFont(new Font(originalFont.getName(), Font.BOLD, fontSize));

        // Center text
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(getValue());
        int textHeight = fm.getHeight();
        g.drawString(getValue(), x + (objectWidth - textWidth) / 2,
                y + (objectHeight + textHeight) / 2 - fm.getDescent());

        // Restore original font
        g.setFont(originalFont);
    }

    private int getFittingFontSize(Graphics g, String text, int width, int height) {
        Font baseFont = g.getFont();
        int minSize = 4;   // теперь можно очень мелко
        int maxSize = 30;
        int fittedSize = minSize;

        while (minSize <= maxSize) {
            int mid = (minSize + maxSize) / 2;
            Font testFont = new Font(baseFont.getName(), Font.BOLD, mid);
            FontMetrics fm = g.getFontMetrics(testFont);

            boolean fitsWidth  = fm.stringWidth(text) <= width;
            boolean fitsHeight = fm.getHeight() <= height;

            if (fitsWidth && fitsHeight) {
                fittedSize = mid;     // этот размер влезает, запоминаем его
                minSize = mid + 1;    // пробуем найти побольше
            } else {
                maxSize = mid - 1;    // слишком большой — уменьшаем
            }
        }

        return fittedSize - 2;
    }


    public ElementType getElementType() {
        return elementType;
    }

    public String getValue() {
        return value;
    }

    public ObjectProperty getCorrespondingProperty() {
        return correspondingProperty;
    }
}