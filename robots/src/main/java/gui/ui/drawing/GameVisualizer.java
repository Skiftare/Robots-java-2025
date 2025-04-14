package gui.ui.drawing;

import game.factory.GameObjectFactory;
import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.ObjectProperty;
import gui.system.localization.LocalizationManager;
import gui.ui.CoordinateGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class GameVisualizer extends JPanel {
    private final MovementHandler movementHandler;
    private int panelWidth = 0;
    private int panelHeight = 0;
    private final CoordinateGrid grid;

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
        // Check if player won
        if (movementHandler.isGameWon()) {
            showVictoryDialog();
            return;
        }

        // Check if game is over (no controllable entities)
        if (movementHandler.isGameOver()) {
            showGameOverDialog();
        }
    }

    /**
     * Display victory dialog with only restart option
     */
    /**
     * Display victory dialog with only restart option
     */
    private void showVictoryDialog() {
        SwingUtilities.invokeLater(() -> {
            LocalizationManager locManager = LocalizationManager.getInstance();
            JOptionPane pane = new JOptionPane(
                    locManager.getString("game.victory.message"),
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    new Object[]{locManager.getString("game.restart")},
                    locManager.getString("game.restart")
            );

            JDialog dialog = pane.createDialog(this, locManager.getString("game.victory.title"));
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setModal(true);
            dialog.setVisible(true);

            // Always restart since there's only one option
            restartGame();
        });
    }

    /**
     * Display game over dialog with only restart option
     */
    private void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            LocalizationManager locManager = LocalizationManager.getInstance();
            //Типичный выбор на КБ
            JOptionPane pane = new JOptionPane(
                    locManager.getString("game.over.message"),
                    JOptionPane.WARNING_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    new Object[]{locManager.getString("game.restart")},
                    locManager.getString("game.restart")
            );

            JDialog dialog = pane.createDialog(this, locManager.getString("game.over.title"));
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setModal(true);
            dialog.setVisible(true);


            restartGame();
        });
    }

    /**
     * Restart the game
     */
    private void restartGame() {
        // Reset game state flags first
        movementHandler.resetGameState();

        // Initialize game objects and formulas
        GameObjectFactory.initializeGame(movementHandler);

        // Request focus for keyboard input
        requestFocusInWindow();

        // Repaint the panel
        repaint();
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

        // Рисуем сетку
        g2d.setColor(new Color(100, 100, 100));
        grid.drawGrid(g2d, panelWidth, panelHeight);

        // Рисуем все игровые объекты
        int cellSize = grid.getCellSize(panelWidth, panelHeight);
        Point start = grid.getStartCoordinates(panelWidth, panelHeight);

        for (GameObject obj : movementHandler.getGameObjects()) {
            obj.draw(g2d, cellSize, start);
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