package gui.ui;

import model.Robot;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import static model.Robot.*;

public class GameVisualizer extends JPanel {
    private final Robot robot;
    private int panelWidth = 0;
    private int panelHeight = 0;
    private boolean isPanelInitialized = false;

    // Экземпляр координатной сетки (например, 20x20)
    private final CoordinateGrid grid;

    public GameVisualizer() {
        // Инициализируем робота – позиция будет скорректирована при первом отрисовывании
        this.robot = new Robot(0, 0, 0);
        // Инициализируем координатную сетку (20 столбцов и 20 строк)
        this.grid = new CoordinateGrid(20, 20);

        // Настраиваем обработку нажатий клавиш
        setFocusable(true);
        requestFocusInWindow();
        setupKeyBindings();
    }

    /**
     * Настройка привязок клавиш для управления роботом.
     */
    private void setupKeyBindings() {
        // Стрелка вверх
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "moveUp");
        getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRobot(0, -1);
            }
        });
        // Стрелка вниз
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRobot(0, 1);
            }
        });
        // Стрелка влево
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRobot(-1, 0);
            }
        });
        // Стрелка вправо
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRobot(1, 0);
            }
        });
    }

    /**
     * Перемещает робота на одну клетку в направлении (dx, dy).
     * Телепортация реализована, если робот выходит за пределы сетки.
     *
     * @param dx изменение по горизонтали (-1, 0, 1)
     * @param dy изменение по вертикали (-1, 0, 1)
     */
    private void moveRobot(int dx, int dy) {
        int cellSize = grid.getCellSize(panelWidth, panelHeight);
        Point start = grid.getStartCoordinates(panelWidth, panelHeight);
        int columns = grid.getColumns();
        int rows = grid.getRows();

        // Вычисляем текущий индекс клетки. Используем floor, так как позиция робота находится в центре клетки.
        int cellX = (int) Math.floor((robot.getPositionX() - start.x) / cellSize);
        int cellY = (int) Math.floor((robot.getPositionY() - start.y) / cellSize);

        int newCellX = cellX + dx;
        int newCellY = cellY + dy;

        // Телепортация по горизонтали
        if (newCellX < 0) {
            newCellX = columns - 1;
        } else if (newCellX >= columns) {
            newCellX = 0;
        }
        // Телепортация по вертикали
        if (newCellY < 0) {
            newCellY = rows - 1;
        } else if (newCellY >= rows) {
            newCellY = 0;
        }

        // Новые координаты – центр выбранной клетки
        double newX = start.x + newCellX * cellSize + cellSize / 2.0;
        double newY = start.y + newCellY * cellSize + cellSize / 2.0;
        robot.setPosition(newX, newY);

        // Обновляем направление робота для отрисовки
        if (dx == 1) {
            robot.setDirection(0);
        } else if (dx == -1) {
            robot.setDirection(Math.PI);
        } else if (dy == 1) {
            robot.setDirection(Math.PI / 2);
        } else if (dy == -1) {
            robot.setDirection(3 * Math.PI / 2);
        }

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        panelWidth = this.getWidth();
        panelHeight = this.getHeight();
        if (!isPanelInitialized) {
            isPanelInitialized = true;
            // Устанавливаем робота в центр центральной клетки
            Point start = grid.getStartCoordinates(panelWidth, panelHeight);
            int cellSize = grid.getCellSize(panelWidth, panelHeight);
            int centerCellX = grid.getColumns() / 2;
            int centerCellY = grid.getRows() / 2;
            robot.setPosition(start.x + centerCellX * cellSize + cellSize / 2.0,
                    start.y + centerCellY * cellSize + cellSize / 2.0);
        }

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        // Отрисовка координатной сетки
        grid.drawGrid(g2d, panelWidth, panelHeight);

        // Отрисовка робота
        drawRobot(g2d, robot.getPositionX(), robot.getPositionY(), robot.getDirection());

        g2d.setTransform(originalTransform);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, double x, double y, double direction) {
        AffineTransform currentTransform = g.getTransform();

        g.translate(x, y);
        g.rotate(direction);

        g.setColor(Color.MAGENTA);
        fillOval(g, 0, 0, ROBOT_WIDTH, ROBOT_HEIGHT);
        g.setColor(Color.BLACK);
        drawOval(g, 0, 0, ROBOT_WIDTH, ROBOT_HEIGHT);

        g.setColor(Color.WHITE);
        fillOval(g, EYE_OFFSET_X, 0, EYE_SIZE, EYE_SIZE);
        g.setColor(Color.BLACK);
        drawOval(g, EYE_OFFSET_X, 0, EYE_SIZE, EYE_SIZE);

        g.setTransform(currentTransform);
    }
}
