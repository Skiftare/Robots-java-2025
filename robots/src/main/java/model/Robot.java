package model;


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

    public double getX() {
        return positionX;
    }

    public double getY() {
        return positionY;
    }

    public double getDirection() {
        return direction;
    }

    public void moveTowards(double targetX, double targetY, double duration) {
        double distance = distance(targetX, targetY, positionX, positionY);
        if (distance < 0.5) return;

        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(positionX, positionY, targetX, targetY);
        double angularVelocity = 0;
        if (angleToTarget > direction) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        if (angleToTarget < direction) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

    }


    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    private static double applyLimits(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}