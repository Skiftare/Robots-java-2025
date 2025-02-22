package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import model.Robot;
import model.Target;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private final Robot robot;
    private final Target target;

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameVisualizer() {
        robot = new Robot(100, 100, 0);
        target = new Target(150, 100);

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout());

        initializeTimers();
        addMouseListeners();
        setDoubleBuffered(true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
    }

    private void initializeTimers() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 5);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 5);
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Получаем координаты относительно компонента GameVisualizer
                Point clickPoint = e.getPoint();

                // Преобразуем координаты компонента в координаты экрана
                SwingUtilities.convertPointToScreen(clickPoint, GameVisualizer.this);

                // Преобразуем экранные координаты в координаты родительского контейнера
                 SwingUtilities.convertPointFromScreen(clickPoint, getParent());

                setTargetPosition(clickPoint.x, clickPoint.y);
                repaint();
            }
        });
    }

    protected void setTargetPosition(int x, int y) {
        Point componentPos = getLocation();
        target.setPosition(x - componentPos.x, y - componentPos.y);
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
        double distance = distance(target.getX(), target.getY(),
                robot.getX(), robot.getY());
        if (distance < 0.5) {
            return;
        }
        
        double velocity = Robot.getMaxVelocity();
        double angleToTarget = angleTo(robot.getX(), robot.getY(), 
                target.getX(), target.getY());
        
        double angularVelocity = 0;
        if (angleToTarget > robot.getDirection()) {
            angularVelocity = Robot.getMaxAngularVelocity();
        }
        if (angleToTarget < robot.getDirection()) {
            angularVelocity = -Robot.getMaxAngularVelocity();
        }
        
        robot.move(velocity, angularVelocity, 10);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
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
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(robot.getX()), round(robot.getY()), robot.getDirection());
        drawTarget(g2d, target.getX(), target.getY());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        // Корректируем позицию центра робота
        int robotCenterX = x + 15;  // 30/2 = 15
        int robotCenterY = y + 5;   // 10/2 = 5
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
