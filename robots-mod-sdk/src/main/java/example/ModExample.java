package example;

import game.model.GameObject;
import game.mods.IMod;
import game.mods.ModManager;
import game.mods.extensions_points.IBackgroundProvider;
import gui.ui.drawing.GameVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;
/**
 * Example mod that demonstrates various modding capabilities:
 * - Custom background with stars
 * - Custom key handling (press 'S' to spawn stars)
 * - Object rendering customization (adds glowing effect to objects)
 */
public class ModExample implements IMod {
    private static final Random random = new Random();
    private final Color starColor = new Color(255, 255, 180);
    private final java.util.List<Point> stars = new java.util.ArrayList<>();
    private boolean showStars = true;

    @Override
    public String getName() {
        return "Example Mod";
    }

    @Override
    public String getDescription() {
        return "Demonstrates modding capabilities with visual effects and custom controls";
    }

    @Override
    public String getAuthor() {
        return "SDK Team";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void initialize(ModManager modManager) {
        // Register a background provider to draw stars
        modManager.registerBackgroundProvider(new StarryBackground());

        // Generate initial stars
        generateStars(100);
    }

    @Override
    public boolean interceptKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S && e.getID() == KeyEvent.KEY_PRESSED) {
            showStars = !showStars;
            return true; // We've handled this key
        }
        return false; // Let other handlers process the key
    }

    @Override
    public void setupCustomControls(GameVisualizer visualizer) {
        // Add a button to toggle star effects
        JButton toggleButton = new JButton("Toggle Stars");
        toggleButton.addActionListener(e -> showStars = !showStars);

        // We could add this to a toolbar or panel in the game UI
    }

    @Override
    public boolean customizeObjectDrawing(GameObject obj, Graphics2D g, int cellSize, Point start) {
        // Let the default drawing happen first
        return false;
    }

    @Override
    public void onObjectInteraction(GameObject obj1, GameObject obj2) {
        // React to object interactions
    }

    /**
     * Generate random stars for the background
     */
    private void generateStars(int count) {
        stars.clear();
        for (int i = 0; i < count; i++) {
            stars.add(new Point(random.nextInt(800), random.nextInt(600)));
        }
    }

    /**
     * Background provider implementation for starry sky effect
     */
    private class StarryBackground implements IBackgroundProvider {
        @Override
        public void drawBackground(Graphics2D g, int width, int height, long timestamp) {
            if (!showStars) return;

            g.setColor(starColor);
            for (Point star : stars) {
                // Make stars twinkle based on timestamp
                int brightness = 150 + (int) (100 * Math.sin(timestamp * 0.001 + star.x * star.y));
                g.setColor(new Color(brightness, brightness, 200));

                g.fillOval(star.x, star.y, 2, 2);
            }
        }

        @Override
        public boolean isDynamic() {
            return true; // Stars will twinkle
        }

        @Override
        public int getPriority() {
            return 10; // Lower priority backgrounds are drawn first
        }
    }
}