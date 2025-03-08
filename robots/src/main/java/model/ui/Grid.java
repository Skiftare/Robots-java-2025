package model.ui;

import model.Position;

import java.util.HashMap;
import java.util.Map;

public class Grid {
    private final int width;
    private final int height;
    private final int cellSize;
    private final Map<Position, Cell> cells = new HashMap<>();

    public Grid(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        initializeGrid();
    }

    private void initializeGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells.put(new Position(x, y), new Cell());
            }
        }
    }

    public Cell getCell(int x, int y) {
        return cells.get(new Position(x, y));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCellSize() {
        return cellSize;
    }
}