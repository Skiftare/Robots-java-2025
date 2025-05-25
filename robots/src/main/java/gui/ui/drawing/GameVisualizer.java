package gui.ui.drawing;

import game.factory.GameObjectFactory;
import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.ObjectProperty;
import game.mods.ModManager;
import game.mods.extensions_points.IBackgroundProvider;
import gui.system.sound.SoundManager;
import gui.ui.CoordinateGrid;
import gui.ui.GameWindow;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class GameVisualizer extends JPanel {
    @Getter
    private final MovementHandler movementHandler;
    private int panelWidth = 0;
    private int panelHeight = 0;
    private final CoordinateGrid grid;

    @Setter
    @Getter
    private ModManager modManager = new ModManager();
    private long lastFrameTimestamp = System.currentTimeMillis();

    private GameWindow gameWindow;

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public GameVisualizer() {
        // Initialize the grid
        this.grid = new CoordinateGrid(20, 20);

        // Initialize the movement handler
        this.movementHandler = new MovementHandler(grid);

        // Initialize game objects and formulas
        GameObjectFactory.initializeGame(movementHandler);

        // Set up keyboard input
        setFocusable(true);
        requestFocusInWindow();
        setupKeyBindings();

        this.movementHandler.setModManager(modManager);
        this.movementHandler.getFormulaHandler().setModManager(modManager);

        Timer backgroundTimer = new Timer(50, e -> {
            boolean needsRepaint = false;
            for (IBackgroundProvider provider : modManager.getBackgroundProviders()) {
                if (provider.isDynamic()) {
                    needsRepaint = true;
                    break;
                }
            }
            if (needsRepaint) {
                repaint();
            }
        });
        backgroundTimer.start();
    }

    private void setupKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "moveUp");
        getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerInCells(0, -1);
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerInCells(0, 1);
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerInCells(-1, 0);
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerInCells(1, 0);
            }
        });


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (modManager.interceptKeyEvent(e)) {
                    // Key was handled by a mod
                    return;
                }
                // Otherwise process normally
            }
        });

        // Let mods set up their controls
        modManager.setupCustomControls(this);
    }

    /**
     * Метод для совместимости с существующими тестами
     */
    void moveRobotInCells(int dx, int dy) {
        movePlayerInCells(dx, dy);
    }

    void movePlayerInCells(int dx, int dy) {
        boolean moved = movementHandler.movePlayers(dx, dy);

        // Check game state after movement
        if (moved) {
            checkGameState();
        }

        repaint();
    }

    /**
     * Check win/loss conditions and display appropriate dialog
     */
    private void checkGameState() {
        if (movementHandler.isGameWon()) {
            if (gameWindow != null) {
                gameWindow.onLevelWon();
            }
            SoundManager.playWin();
            return;
        }

        if (movementHandler.isGameOver()) {
            if (gameWindow != null) {
                gameWindow.onLevelLost();
            }
            SoundManager.playDeath();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        panelWidth = this.getWidth();
        panelHeight = this.getHeight();

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        // Рисуем фон
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        lastFrameTimestamp = System.currentTimeMillis();
        modManager.drawCustomBackgrounds(g2d, panelWidth, panelHeight, lastFrameTimestamp);

        // Рисуем сетку
        g2d.setColor(new Color(100, 100, 100));
        grid.drawGrid(g2d, panelWidth, panelHeight);

        // Рисуем все игровые объекты
        int cellSize = grid.getCellSize(panelWidth, panelHeight);
        Point start = grid.getStartCoordinates(panelWidth, panelHeight);

        for (GameObject obj : movementHandler.getGameObjects()) {
            // Let mods customize the drawing first
            if (!modManager.tryCustomizeDrawing(obj, g2d, cellSize, start)) {
                // If no mod handled it, use default drawing
                obj.draw(g2d, cellSize, start);
            }
        }

        g2d.setTransform(originalTransform);
    }

    public ArrayList<GameObject> getMovableObjects() {
        ArrayList<GameObject> arr = new ArrayList<>();
        for (GameObject obj : movementHandler.getGameObjects()) {
            if (obj.hasProperty(ObjectProperty.PUSHABLE)) {
                arr.add(obj);
            }
        }
        return new ArrayList<>(arr);
    }

    public ArrayList<GameObject> getGameObjects() {
        return new ArrayList<>(movementHandler.getGameObjects());
    }

    public void rewriteGameObjects(ArrayList<GameObject> newObjects) {
        // Очищаем список объектов в движке
        movementHandler.clearGameObjects();

        // Добавляем новые объекты
        for (GameObject obj : newObjects) {
            movementHandler.addGameObject(obj);
        }

        // Перерисовываем игровое поле
        repaint();
    }
}