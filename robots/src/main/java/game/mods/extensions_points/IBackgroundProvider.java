package game.mods.extensions_points;

import java.awt.*;

public interface IBackgroundProvider {
    void drawBackground(Graphics2D g, int width, int height, long timestamp);
    boolean isDynamic(); // If true, background will be refreshed regularly
}
