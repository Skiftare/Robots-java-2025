package model;

public class Robot {
    private int cellX;
    private int cellY;
    private double direction;

    public static final int ROBOT_WIDTH = 30;
    public static final int ROBOT_HEIGHT = 10;
    public static final int EYE_OFFSET_X = 10;
    public static final int EYE_SIZE = 5;

    public Robot(int x, int y, double direction) {
        this.cellX = x;
        this.cellY = y;
        this.direction = direction;
    }

    public void setPositionInCell(int x, int y) {
        this.cellX = x;
        this.cellY = y;
    }

    public int[] getPositionInCell() {
        return new int[]{cellX, cellY};
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getDirection() {
        return direction;
    }

    public void move(int dx, int dy, int columns, int rows) {
        // Calculate potential new position
        int newX = cellX + dx;
        int newY = cellY + dy;

        //Не-выход за рамки
        if (newX >= 0 && newX < columns) {
            cellX = newX;
        }

        if (newY >= 0 && newY < rows) {
            cellY = newY;
        }
    }
}