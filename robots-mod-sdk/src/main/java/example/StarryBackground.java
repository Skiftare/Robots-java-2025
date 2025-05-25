package example;

import game.mods.extensions_points.IBackgroundProvider;
import java.awt.*;
import java.util.Random;

public class StarryBackground implements IBackgroundProvider {
    private final Random random = new Random();

    @Override
    public void drawBackground(Graphics2D g, int width, int height, long timestamp) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int size = 1 + random.nextInt(3);
            g.fillOval(x, y, size, size);
        }
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public int getPriority() {
        return 10;
    }
}