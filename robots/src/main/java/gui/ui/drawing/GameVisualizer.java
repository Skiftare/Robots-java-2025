package gui.ui.drawing;

import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.ObjectProperty;
import gui.ui.CoordinateGrid;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GameVisualizer extends JPanel {
    private final MovementHandler movementHandler;
    private int panelWidth = 0;
    private int panelHeight = 0;
    private final CoordinateGrid grid;

    public GameVisualizer() {
        // Инициализация сетки
        this.grid = new CoordinateGrid(20, 20);

        // Инициализация обработчика движения
        this.movementHandler = new MovementHandler(grid);

        // Создаем игрока (бывший Robot)
        GameObject player = new GameObject(grid.getColumns()/2, grid.getRows()/2,
                "robots/src/main/resources/robot.png", "Игрок", "player");
        player.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player);




        GameObject player2 = new GameObject(2, 2,
                "robots/src/main/resources/robot.png", "Игрок", "player");
        player2.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player2);


        // Создаем блок (бывший MovableObject)
        GameObject box1 = new GameObject(5, 5, "robots/src/main/resources/object.png", "Ящик", "box");
        box1.addProperty(ObjectProperty.PUSHABLE);
        box1.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box1);

        GameObject box2 = new GameObject(4, 4, "robots/src/main/resources/object.png", "Ящик", "box");
        box2.addProperty(ObjectProperty.PUSHABLE);
        box2.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box2);


        GameObject wall = new GameObject(16,16, "robots/src/main/resources/wall.png", "Стена", "wall");
        wall.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall);

        // Настройка клавиатурного ввода
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
        movementHandler.movePlayers(dx, dy);
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