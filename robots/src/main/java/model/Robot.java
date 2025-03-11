package model;

import log.Logger;

public class Robot {
    private double positionX;
    private double positionY;
    private double direction;
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    public static final int ROBOT_WIDTH = 30;
    public static final int ROBOT_HEIGHT = 10;
    public static final int EYE_OFFSET_X = 10;
    public static final int EYE_SIZE = 5;

    public Robot(double x, double y, double direction) {
        this.positionX = x;
        this.positionY = y;
        this.direction = direction;
    }

    public void move(double velocity, double angularVelocity, double duration, int panelWidth, int panelHeight) {
        direction += angularVelocity * duration;
        direction = asNormalizedRadians(direction);

        double newX = positionX + velocity * duration * Math.cos(direction);
        double newY = positionY + velocity * duration * Math.sin(direction);

        // 3. Проверка границ с большим запасом
        if (newX < 0) {
            newX = panelWidth;

        } else if (newX > panelWidth ) {
            newX = 0;
        }

        if (newY < 0) {
            newY = panelHeight;
        } else if (newY > panelHeight ) {
            newY = 0;
        }

        positionX = newX;
        positionY = newY;


    }

    private double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getDirection() {
        return direction;
    }

    public static double getMaxVelocity() {
        return MAX_VELOCITY;
    }

    public static double getMaxAngularVelocity() {
        return MAX_ANGULAR_VELOCITY;
    }
}