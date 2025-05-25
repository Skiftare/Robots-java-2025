package game.mods.extensions_points;

import game.model.GameObject;

import java.awt.*;

public interface IRenderAdapter {
    boolean customizeDraw(GameObject object, Graphics2D g, int cellSize, Point start);
}
