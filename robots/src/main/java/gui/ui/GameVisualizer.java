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
import java.awt.BasicStroke;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import log.Logger;

public class GameVisualizer extends JPanel {
    private final Robot robot;
    private final Target target;

    private int panelWidth = 0;
    private int panelHeight = 0;
    private boolean isPanelInitialized = false;

    // Для отладки - показывать ли дополнительную информацию
    private static final boolean SHOW_DEBUG_INFO = false;

    // Минимальное расстояние, при котором считается, что робот достиг цели
    private static final double TARGET_REACHED_DISTANCE = 1.0;

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
        }, 0, 10);

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
        Logger.debug("Target set to: " + p.x + ", " + p.y);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    // Евклидово расстояние между двумя точками
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return Math.atan2(diffY, diffX);
    }

    protected void onModelUpdateEvent() {
        if (!isPanelInitialized) return;

        double distanceToTarget = distance(target.getX(), target.getY(),
                robot.getPositionX(), robot.getPositionY());

        if (distanceToTarget < TARGET_REACHED_DISTANCE) {
            return;
        }

        double angleToTarget = angleTo(robot.getPositionX(), robot.getPositionY(),
                target.getX(), target.getY());

        double robotDirection = normalizeAngle(robot.getDirection());
        angleToTarget = normalizeAngle(angleToTarget);

        double angleDiff = normalizeAngleDifference(angleToTarget - robotDirection);

        double angularVelocity = 0;
        if (Math.abs(angleDiff) > 0.02) { // примерно 1 градус
            if (angleDiff > 0) {
                angularVelocity = Robot.getMaxAngularVelocity();
            } else {
                angularVelocity = -Robot.getMaxAngularVelocity();
            }
        }

        double velocity;
        if (Math.abs(angleDiff) > Math.PI / 6) {
            velocity = Robot.getMaxVelocity() * 0.3;
        } else if (Math.abs(angleDiff) > Math.PI / 18) {
            velocity = Robot.getMaxVelocity() * 0.7;
        } else {
            velocity = Robot.getMaxVelocity();
        }

        robot.move(velocity, angularVelocity, 10, panelWidth, panelHeight);
    }

    // Нормализация угла в диапазон [-PI, PI]
    private static double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    // Нормализация разницы углов для кратчайшего пути
    private static double normalizeAngleDifference(double angleDiff) {
        angleDiff = normalizeAngle(angleDiff);
        if (angleDiff > Math.PI) {
            angleDiff -= 2 * Math.PI;
        } else if (angleDiff < -Math.PI) {
            angleDiff += 2 * Math.PI;
        }
        return angleDiff;
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

        AffineTransform originalTransform = g2d.getTransform();


        drawTarget(g2d, target.getX(), target.getY());

        drawRobot(g2d, robot.getPositionX(), robot.getPositionY(), robot.getDirection());

        g2d.setTransform(originalTransform);
    }



    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, double x, double y, double direction) {
        int robotWidth = 30;
        int robotHeight = 10;
        int eyeOffsetX = 10;
        int eyeSize = 5;
        double dirLineLength = 30.0;

        AffineTransform currentTransform = g.getTransform();

        g.translate(x, y);
        g.rotate(direction);

        g.setColor(Color.MAGENTA);
        fillOval(g, 0, 0, robotWidth, robotHeight);
        g.setColor(Color.BLACK);
        drawOval(g, 0, 0, robotWidth, robotHeight);

        g.setColor(Color.WHITE);
        fillOval(g, eyeOffsetX, 0, eyeSize, eyeSize);
        g.setColor(Color.BLACK);
        drawOval(g, eyeOffsetX, 0, eyeSize, eyeSize);

        g.setTransform(currentTransform);

    }


    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}