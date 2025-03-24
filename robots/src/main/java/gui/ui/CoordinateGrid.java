package gui.ui;

import java.awt.*;

public class CoordinateGrid {
    private final int columns;
    private final int rows;
    private final int desiredCellSize; // Размер клетки в пикселях при стандартном разрешении (~1 см)

    /**
     * @param columns Количество столбцов сетки
     * @param rows    Количество строк сетки
     */
    public CoordinateGrid(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        // Получаем dpi экрана и вычисляем размер 1 см в пикселях
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        this.desiredCellSize = (int) Math.round(dpi / 2.54);
    }

    /**
     * Вычисляет размер клетки в зависимости от размеров панели.
     */
    public int getCellSize(int panelWidth, int panelHeight) {
        int cellSize = desiredCellSize;
        if (columns * desiredCellSize > panelWidth || rows * desiredCellSize > panelHeight) {
            cellSize = Math.min(panelWidth / columns, panelHeight / rows);
        }
        return cellSize;
    }

    /**
     * Вычисляет координаты начала отрисовки сетки (для центрирования на панели).
     */
    public Point getStartCoordinates(int panelWidth, int panelHeight) {
        int cellSize = getCellSize(panelWidth, panelHeight);
        int gridWidth = cellSize * columns;
        int gridHeight = cellSize * rows;
        int startX = (panelWidth - gridWidth) / 2;
        int startY = (panelHeight - gridHeight) / 2;
        return new Point(startX, startY);
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Отрисовывает сетку так, чтобы весь её блок (колонки x строки) полностью помещался на панели.
     *
     * @param g           Графический контекст
     * @param panelWidth  Ширина области отрисовки
     * @param panelHeight Высота области отрисовки
     */
    public void drawGrid(Graphics2D g, int panelWidth, int panelHeight) {
        int cellSize = getCellSize(panelWidth, panelHeight);
        Point start = getStartCoordinates(panelWidth, panelHeight);
        int gridWidth = cellSize * columns;
        int gridHeight = cellSize * rows;

        g.setColor(Color.LIGHT_GRAY);
        // Рисуем вертикальные линии
        for (int i = 0; i <= columns; i++) {
            int x = start.x + i * cellSize;
            g.drawLine(x, start.y, x, start.y + gridHeight);
        }
        // Рисуем горизонтальные линии
        for (int j = 0; j <= rows; j++) {
            int y = start.y + j * cellSize;
            g.drawLine(start.x, y, start.x + gridWidth, y);
        }
    }
}
