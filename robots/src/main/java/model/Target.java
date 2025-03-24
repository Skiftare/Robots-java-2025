package model;

public class Target {
    private int x;
    private int y;

    public Target(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // New method with boundary checking
    public void setPositionWithBounds(int x, int y, int columns, int rows) {
        if (x >= 0 && x < columns && y >= 0 && y < rows) {
            this.x = x;
            this.y = y;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}