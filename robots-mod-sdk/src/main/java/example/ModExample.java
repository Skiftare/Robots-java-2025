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
    private final FractalGenerator fractalGenerator = new FractalGenerator();

    // Use parallel arrays instead of a Star class
    private final List<Integer> starX = new ArrayList<>();
    private final List<Integer> starY = new ArrayList<>();
    private final List<Float> starBrightness = new ArrayList<>();
    private final List<Integer> starSize = new ArrayList<>();

    private int prevWidth = 800;
    private int prevHeight = 600;
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
        return "2.1";
    }

    @Override
    public void initialize(ModRegistry registry) {
        registry.registerBackgroundProvider(this);
        registry.registerControlAdapter(this);
        registry.registerRenderAdapter(this);
        registry.registerGameMechanic(this);
        WindowLogger.debug("Ultimate Mod Example initialized with all extensions!");
    }

    @Override
    public void shutdown() {
        WindowLogger.debug("Ultimate Mod Example shutting down!");
    }


    @Override
    public void drawBackground(Graphics2D g, int width, int height, long timestamp) {
        // Отрисовка фрактальных точек
        List<Point> points = fractalGenerator.getFractalPoints();
        g.setColor(Color.WHITE);
        for (Point point : points) {
            int x = (int) (width / 2 + point.x() * 100); // Масштабирование и центрирование
            int y = (int) (height / 2 + point.y() * 100);
            g.fillRect(x, y, 2, 2); // Рисуем точку
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
            fractalGenerator.addEvent(e); // Добавляем событие в очередь
            return true;
        }
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