package model;

import log.Logger;

public class Robot {
    private double positionX;
    private double positionY;
    private double direction;
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    public Robot(double x, double y, double direction) {
        this.positionX = x;
        this.positionY = y;
        this.direction = direction;
    }

    public void move(double velocity, double angularVelocity, double duration, int panelWidth, int panelHeight) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = positionX + velocity * duration * Math.cos(direction);
        double newY = positionY + velocity * duration * Math.sin(direction);

        if (newX < 0) {
            newX = panelWidth;
        }
        if (newX > panelWidth){
            newX = 0;
        }
        if (newY < 0) {
            newY = panelHeight;
        }
        if (newY > panelHeight) {
            newY = 0;
        }

        positionX = newX;
        positionY = newY;
        direction = asNormalizedRadians(direction + angularVelocity * duration);
    }

    private double applyLimits(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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