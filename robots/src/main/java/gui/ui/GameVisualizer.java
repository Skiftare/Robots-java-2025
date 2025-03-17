package gui.ui;

import model.Robot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Paths;

import static model.Robot.*;

public class GameVisualizer extends JPanel {
    private final Robot robot;
    private int panelWidth = 0;
    private int panelHeight = 0;
    private boolean isPanelInitialized = false;

    // Экземпляр координатной сетки (например, 20x20)
    private final CoordinateGrid grid;

    // Массив для хранения объектов на клетках
    private final Object[][] objects;

    // Изображение робота
    private Image robotImage;
    private double robotImageWidth;
    private double robotImageHeight;

    public GameVisualizer() {
        // Инициализируем робота с начальными координатами в клетке
        this.robot = new Robot(0, 0, 0);
        // Инициализируем координатную сетку (20 столбцов и 20 строк)
        this.grid = new CoordinateGrid(20, 20);

        // Создаём массив объектов для каждой клетки
        this.objects = new Object[grid.getRows()][grid.getColumns()];

        // Настраиваем обработку нажатий клавиш
        setFocusable(true);
        requestFocusInWindow();
        setupKeyBindings();

        // Загружаем изображение робота
        try {
            // Правильный путь для загрузки изображения
            String imagePath = Paths.get("robots/src/main/resources/robot.png").toString();
            robotImage = ImageIO.read(new File(imagePath));
            // Получаем исходные размеры изображения
            robotImageWidth = robotImage.getWidth(null);
            robotImageHeight = robotImage.getHeight(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                moveRobotInCells(0, -1);
            }
        });
        // Стрелка вниз
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRobotInCells(0, 1);
            }
        });
        // Стрелка влево
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRobotInCells(-1, 0);
            }
        });
        // Стрелка вправо
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveRobotInCells(1, 0);
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
    private void moveRobotInCells(int dx, int dy) {
        robot.move(dx, dy, grid.getColumns(), grid.getRows());
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
            robot.setPositionInCell(centerCellX, centerCellY);
        }

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        // Отрисовка фона
        g2d.setColor(new Color(50, 50, 50));  // Темный фон
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Отрисовка координатной сетки с более темными линиями
        g2d.setColor(new Color(100, 100, 100));  // Темная сетка
        grid.drawGrid(g2d, panelWidth, panelHeight);

        // Отрисовка робота с текстурой
        drawRobot(g2d, robot.getPositionInCell()[0], robot.getPositionInCell()[1]);

        g2d.setTransform(originalTransform);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int cellX, int cellY) {
        int cellSize = grid.getCellSize(panelWidth, panelHeight);
        Point start = grid.getStartCoordinates(panelWidth, panelHeight);

        // Пропорциональное изменение размера изображения, чтобы оно не растягивалось
        int robotWidth = (int) (robotImageWidth * (cellSize / 30.0));  // Пропорционально размеру клетки
        int robotHeight = (int) (robotImageHeight * (cellSize / 30.0));

        // Ограничиваем размер изображения, чтобы оно помещалось в клетку
        robotWidth = Math.min(robotWidth, cellSize);
        robotHeight = Math.min(robotHeight, cellSize);

        // Отрисовка робота с текстурой
        int x = start.x + cellX * cellSize + cellSize / 2 - robotWidth / 2;
        int y = start.y + cellY * cellSize + cellSize / 2 - robotHeight / 2;
        g.drawImage(robotImage, x, y, robotWidth, robotHeight, null);
    }

    // Механизм расстановки объектов в клетках (например, для создания препятствий)
    public void setObjectInCell(int x, int y, Object object) {
        objects[y][x] = object;
    }

    public Object getObjectInCell(int x, int y) {
        return objects[y][x];
    }
}
