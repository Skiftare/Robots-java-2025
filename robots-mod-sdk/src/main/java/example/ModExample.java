package example;

import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.ObjectProperty;
import game.model.formula.Formula;
import game.mods.IMod;
import game.mods.ModRegistry;
import game.mods.extensions_points.IBackgroundProvider;
import game.mods.extensions_points.IControlAdapter;
import game.mods.extensions_points.IGameMechanic;
import game.mods.extensions_points.IRenderAdapter;
import log.WindowLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModExample implements IMod, IBackgroundProvider, IControlAdapter, IRenderAdapter, IGameMechanic {
    private static final Random random = new Random();
    private final Color starColor = new Color(255, 255, 180);
    private final List<Point> stars = new ArrayList<>();
    private long lastStarUpdate = 0;
    private float playerRotation = 0;
    private boolean rainbowMode = false;

    @Override
    public String getName() {
        return "Ultimate Mod Example";
    }

    @Override
    public String getDescription() {
        return "Demonstrates extensive modding capabilities with visual effects, controls and game mechanics";
    }

    @Override
    public String getAuthor() {
        return "SDK Team";
    }

    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void initialize(ModRegistry registry) {
        registry.registerBackgroundProvider(this);
        registry.registerControlAdapter(this);
        registry.registerRenderAdapter(this);
        registry.registerGameMechanic(this);
        generateStars(100);
        WindowLogger.debug("Ultimate Mod Example initialized with all extensions!");
    }

    @Override
    public void shutdown() {
        WindowLogger.debug("Ultimate Mod Example shutting down!");
    }

    private void generateStars(int count) {
        stars.clear();
        for (int i = 0; i < count; i++) {
            stars.add(new Point(random.nextInt(800), random.nextInt(600)));
        }
    }

    // IBackgroundProvider implementation
    @Override
    public void drawBackground(Graphics2D g, int width, int height, long timestamp) {
        // Occasionally update star positions for twinkling effect
        if (timestamp - lastStarUpdate > 500) {
            for (Point star : stars) {
                if (random.nextInt(10) < 3) {
                    star.x = random.nextInt(width);
                    star.y = random.nextInt(height);
                }
            }
            lastStarUpdate = timestamp;
        }

        // Draw stars with yellow color
        g.setColor(starColor);
        for (Point star : stars) {
            int size = 1 + random.nextInt(3);
            g.fillOval(star.x, star.y, size, size);
        }
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public int getPriority() {
        return 10;
    }

    // IControlAdapter implementation for WASD controls
    @Override
    public void setupAdditionalControls(JComponent component) {
        WindowLogger.debug("WASD controls activated by mod");
    }

    @Override
    public boolean interceptKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }

        int keyCode = e.getKeyCode();

        // Toggle rainbow mode with R key
        if (keyCode == KeyEvent.VK_R) {
            rainbowMode = !rainbowMode;
            WindowLogger.debug("Rainbow mode: " + (rainbowMode ? "ON" : "OFF"));
            return true;
        }

        // Handle WASD keys as arrow keys
        int newKeyCode = switch (keyCode) {
            case KeyEvent.VK_W -> KeyEvent.VK_UP;
            case KeyEvent.VK_A -> KeyEvent.VK_LEFT;
            case KeyEvent.VK_S -> KeyEvent.VK_DOWN;
            case KeyEvent.VK_D -> KeyEvent.VK_RIGHT;
            default -> 0;
        };

        if (newKeyCode != 0) {
            // Create new key event with arrow key code
            KeyEvent newEvent = new KeyEvent(
                    e.getComponent(),
                    e.getID(),
                    e.getWhen(),
                    e.getModifiersEx(),
                    newKeyCode,
                    e.getKeyChar(),
                    e.getKeyLocation()
            );

            // Dispatch the new event
            e.getComponent().dispatchEvent(newEvent);
            return true;
        }

        return false;
    }

    // IRenderAdapter implementation for custom rendering
    @Override
    public boolean customizeDraw(GameObject object, Graphics2D g, int cellSize, Point start) {
        // Add rainbow effect to player objects when enabled
        if (rainbowMode && object.hasProperty(ObjectProperty.PLAYER)) {
            playerRotation += 0.1f;
            if (playerRotation > 360) playerRotation = 0;

            int hue = (int)(playerRotation % 360);
            Color rainbowColor = Color.getHSBColor(hue/360.0f, 0.8f, 1.0f);

            // Save original transform and color
            AffineTransform originalTransform = g.getTransform();
            Color originalColor = g.getColor();

            // Set rainbow color and apply rotation
            g.setColor(rainbowColor);
            g.translate(start.x + cellSize / 2, start.y + cellSize / 2);
            g.rotate(Math.toRadians(playerRotation));
            g.translate(-cellSize / 2, -cellSize / 2);

            // Draw a fancy player shape
            int margin = cellSize / 5;
            g.fillRoundRect(margin, margin, cellSize - 2*margin, cellSize - 2*margin, cellSize/3, cellSize/3);

            // Restore original settings
            g.setTransform(originalTransform);
            g.setColor(originalColor);

            return true;
        }

        return false;
    }

    // IGameMechanic implementation
    @Override
    public void onObjectDestroyed(GameObject object, MovementHandler handler) {
        // Create particle effect when objects are destroyed
        WindowLogger.debug("Object destroyed: " + object.getLabel());
    }

    @Override
    public void beforeMovement(MovementHandler handler) {
        // You could add custom logic before each movement
    }

    @Override
    public void afterMovement(MovementHandler handler) {
        // You could add custom logic after each movement
    }

    @Override
    public void onFormulaProcessed(Formula formula, MovementHandler handler) {
        // You could react to specific formulas being processed
        WindowLogger.debug("Formula processed: " + formula);
    }
}