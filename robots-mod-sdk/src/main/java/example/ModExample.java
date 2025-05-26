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
    private final FractalGenerator fractalGenerator = new FractalGenerator();
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
        return "2.1";
    }

    @Override
    public void initialize(ModRegistry registry) {
        WindowLogger.debug("[MOD] Starting registration of " + getName());

        registry.registerBackgroundProvider(this);
        WindowLogger.debug("[MOD] BackgroundProvider registered");

        registry.registerControlAdapter(this);
        WindowLogger.debug("[MOD] ControlAdapter registered");

        registry.registerRenderAdapter(this);
        WindowLogger.debug("[MOD] RenderAdapter registered");

        registry.registerGameMechanic(this);
        WindowLogger.debug("[MOD] GameMechanic registered");

        WindowLogger.debug("[MOD] Ultimate Mod Example initialization complete!");
    }

    @Override
    public void shutdown() {
        WindowLogger.debug("Ultimate Mod Example shutting down!");
        fractalGenerator.shutdown();
    }


    @Override
    public void drawBackground(Graphics2D g, int width, int height, long timestamp) {
        // Background gradient
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(5, 5, 20),
                width, height, new Color(15, 15, 40)
        );
        g.setPaint(bgGradient);
        g.fillRect(0, 0, width, height);

        // Show mod status information
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Ultimate Fractal Mod - Press keys to grow the fractal", 20, 20);
        g.drawString("Press 1-9 to change symmetry, F to change fractal type, C to clear", 20, 40);

        // Draw fractal points with their colors
        List<FractalGenerator.ColoredPoint> points = fractalGenerator.getFractalPoints();
        g.drawString("Points: " + points.size(), 20, 60);

        // Draw each point
        for (FractalGenerator.ColoredPoint point : points) {
            // Scale and center the point with better zoom
            int x = (int) (width / 2 + point.x() * width/3.5);
            int y = (int) (height / 2 + point.y() * height/3.5);

            // Skip if outside window
            if (x < 0 || x >= width || y < 0 || y >= height) continue;

            // Draw with appropriate color
            g.setColor(point.color());
            int size = Math.min(3, 1 + (int)(Math.log1p(point.hitCount()) / 3));
            g.fillRect(x, y, size, size);
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
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            WindowLogger.debug("[MOD] Key pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
            fractalGenerator.addEvent(e);
        }

        if (e.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }

        int keyCode = e.getKeyCode();

        // Toggle rainbow mode with R key
        if (keyCode == KeyEvent.VK_R) {
            rainbowMode = !rainbowMode;
            WindowLogger.debug("[MOD] Rainbow mode toggled: " + (rainbowMode ? "ON" : "OFF"));
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

    // IRenderAdapter implementation with fixed rainbow effect
    @Override
    public boolean customizeDraw(GameObject object, Graphics2D g, int cellSize, java.awt.Point start) {
        // Add rainbow effect to player objects when enabled
        if (rainbowMode && object.hasProperty(ObjectProperty.PLAYER)) {
            // Update rotation
            playerRotation += 0.8f;
            if (playerRotation > 360) playerRotation = 0;

            AffineTransform originalTransform = g.getTransform();
            Paint originalPaint = g.getPaint();

            int objX = object.getPosition()[0];
            int objY = object.getPosition()[1];
            int pixelX = start.x + (objX * cellSize);
            int pixelY = start.y + (objY * cellSize);
            int centerX = pixelX + cellSize / 2;
            int centerY = pixelY + cellSize / 2;

            float hue = (playerRotation % 360) / 360.0f;
            Color mainColor = Color.getHSBColor(hue, 0.9f, 1.0f);

            RadialGradientPaint gradient = new RadialGradientPaint(
                    centerX, centerY, cellSize * 0.7f,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{
                            Color.WHITE,
                            mainColor,
                            Color.getHSBColor((hue + 0.2f) % 1.0f, 1.0f, 0.8f)
                    }
            );
            g.setPaint(gradient);

            g.fillRoundRect(pixelX, pixelY, cellSize, cellSize, cellSize / 3, cellSize / 3);

            g.setColor(Color.BLACK);
            int eyeSize = cellSize / 6;
            g.fillOval(pixelX + cellSize / 4 - eyeSize / 2, pixelY + cellSize / 3 - eyeSize / 2, eyeSize, eyeSize);
            g.fillOval(pixelX + 3 * cellSize / 4 - eyeSize / 2, pixelY + cellSize / 3 - eyeSize / 2, eyeSize, eyeSize);

            g.setStroke(new BasicStroke(2));
            g.drawArc(pixelX + cellSize / 4, pixelY + cellSize / 2, cellSize / 2, cellSize / 4, 0, 180);

            g.setTransform(originalTransform);
            g.setPaint(originalPaint);

            return true;
        }

        return false;
    }

    // IGameMechanic implementation
    @Override
    public void onObjectDestroyed(GameObject object, MovementHandler handler) {
        WindowLogger.debug("Object destroyed: " + object.getLabel());
    }

    @Override
    public void beforeMovement(MovementHandler handler) {
    }

    @Override
    public void afterMovement(MovementHandler handler) {
    }

    @Override
    public void onFormulaProcessed(Formula formula, MovementHandler handler) {
        WindowLogger.debug("Formula processed: " + formula);
    }
}