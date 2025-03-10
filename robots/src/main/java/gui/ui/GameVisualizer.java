package gui.ui;

import model.Robot;
import model.Target;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Robot robot;
    private final Target target;

    private int panelWidth = 0;
    private int panelHeight = 0;
    private boolean isPanelInitialized = false;

    public GameVisualizer() {
        Timer timer = new Timer("events generator", true);
        this.robot = new Robot(100, 100, 0);
        this.target = new Target(150, 100);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 10);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 5);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point p) {
        target.setPosition(p.x, p.y);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent() {
        if (!isPanelInitialized) return;

        double distance = distance(target.getX(), target.getY(),
                robot.getPositionX(), robot.getPositionY());
        if (distance < 0.5) {
            return;
        }

        double angleToTarget = angleTo(robot.getPositionX(), robot.getPositionY(),
                target.getX(), target.getY());
        double angularVelocity = getAngularVelocity(angleToTarget);

        // Pass current panel dimensions
        robot.move(Robot.getMaxVelocity(), angularVelocity, 10, getWidth(), getHeight());
    }

    private double getAngularVelocity(double angleToTarget) {
        double angularVelocity = 0;

        // Calculate angle difference
        double angleDiff = angleToTarget - robot.getDirection();
        if (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
        if (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

        // Set angular velocity based on angle difference
        if (angleDiff > 0) {
            angularVelocity = Robot.getMaxAngularVelocity();
        } else if (angleDiff < 0) {
            angularVelocity = -Robot.getMaxAngularVelocity();
        }
        return angularVelocity;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        panelWidth = this.getWidth();
        panelHeight = this.getHeight();

        if (!isPanelInitialized) {
            isPanelInitialized = true;
        }

        Graphics2D g2d = (Graphics2D) g;

        // Save original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Draw the target first with no transformations
        drawTarget(g2d, target.getX(), target.getY());

        // Then draw the robot
        drawRobot(g2d, robot.getPositionX(), robot.getPositionY(), robot.getDirection());

        // Restore original transform
        g2d.setTransform(originalTransform);
    }

    private static void fillOval(Graphics2D g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics2D g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, double x, double y, double direction) {
        int robotCenterX = round(x);
        int robotCenterY = round(y);

        // Save original transform before applying rotation
        AffineTransform originalTransform = g.getTransform();

        // Create a new transform for rotation around the robot's center
        AffineTransform rotationTransform = AffineTransform.getRotateInstance(
                direction, robotCenterX, robotCenterY);

        // Apply the rotation
        g.setTransform(rotationTransform);

        // Draw robot body
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);

        // Draw robot eye
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);

        // Restore original transform
        g.setTransform(originalTransform);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}