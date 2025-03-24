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
        cellX += dx;
        cellY += dy;

        // Телепортация при выходе за границы
        if (cellX < 0) cellX = columns - 1;
        if (cellX >= columns) cellX = 0;
        if (cellY < 0) cellY = rows - 1;
        if (cellY >= rows) cellY = 0;
    }
}
