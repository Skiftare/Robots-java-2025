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
import java.awt.image.BufferedImage;
public class ModExample implements IMod, IBackgroundProvider, IControlAdapter, IRenderAdapter, IGameMechanic {
    private static final Random random = new Random();
    private final FractalGenerator fractalGenerator = new FractalGenerator();
    private float playerRotation = 0;
    private boolean rainbowMode = false;
    private BufferedImage fractalBuffer;
    private int lastWidth, lastHeight;

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

        registry.registerBackgroundProvider(this);

        registry.registerControlAdapter(this);

        registry.registerRenderAdapter(this);

        registry.registerGameMechanic(this);

    }

    @Override
    public void shutdown() {
        WindowLogger.debug("Ultimate Mod Example shutting down!");
        fractalGenerator.shutdown();
    }


    @Override
    public void drawBackground(Graphics2D g, int width, int height, long timestamp) {
        // Re-initialize buffer if size changed
        if (fractalBuffer == null || width != lastWidth || height != lastHeight) {
            fractalBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D bufferG = fractalBuffer.createGraphics();

            // Draw initial gradient
            GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(5, 5, 20),
                    width, height, new Color(15, 15, 40)
            );
            bufferG.setPaint(bgGradient);
            bufferG.fillRect(0, 0, width, height);
            bufferG.dispose();

            lastWidth = width;
            lastHeight = height;

            // Force redraw all points
            List<FractalGenerator.ColoredPoint> allPoints = fractalGenerator.getFractalPoints();
            drawPointsToBuffer(allPoints, width, height);
        }

        // Draw only new points to buffer
        List<FractalGenerator.ColoredPoint> newPoints = fractalGenerator.getNewPoints();
        if (!newPoints.isEmpty()) {
            drawPointsToBuffer(newPoints, width, height);
        }

        // Draw the buffer to screen
        g.drawImage(fractalBuffer, 0, 0, null);

    }

    private void drawPointsToBuffer(List<FractalGenerator.ColoredPoint> points, int width, int height) {
        Graphics2D bufferG = fractalBuffer.createGraphics();

        // Draw each point
        for (FractalGenerator.ColoredPoint point : points) {
            // Scale and center the point with better zoom
            int x = (int) (width / 2 + point.x() * width/3.5);
            int y = (int) (height / 2 + point.y() * height/3.5);

            // Skip if outside window
            if (x < 0 || x >= width || y < 0 || y >= height) continue;

            // Draw with appropriate color
            bufferG.setColor(point.color());
            int size = Math.min(3, 1 + (int)(Math.log1p(point.hitCount()) / 3));
            bufferG.fillRect(x, y, size, size);
        }

        bufferG.dispose();
    }

    // Add this method to clear the buffer when needed
    private void clearFractalBuffer() {
        fractalBuffer = null; // Force redraw on next frame
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
            fractalGenerator.addEvent(e);
        }

        if (e.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (e.getKeyCode() == KeyEvent.VK_F ||
                    e.getKeyCode() == KeyEvent.VK_C ||
                    (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_9)) {
                clearFractalBuffer();
            }
        }

        int keyCode = e.getKeyCode();

        // Toggle rainbow mode with R key
        if (keyCode == KeyEvent.VK_R) {
            rainbowMode = !rainbowMode;
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