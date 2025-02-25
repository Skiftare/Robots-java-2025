package model;


import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

public class RobotTest {
    private Robot robot;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;


    @Test
    public void testInitialPosition() {
        robot = new Robot(100, 100, 0);
        assertEquals(100, robot.getPositionX(), "Initial X position should be 100");
        assertEquals(100, robot.getPositionY(), "Initial Y position should be 100");
        assertEquals(0, robot.getDirection(), "Initial direction should be 0");
    }

    @Test
    public void testMovement() {
        robot = new Robot(100, 100, 0);
        robot.move(0.1, 0, 10, PANEL_WIDTH, PANEL_HEIGHT);
        assertTrue(robot.getPositionX() > 100);
        assertEquals(100, robot.getPositionY());
    }

    @Test
    public void testVelocityLimits() {
        robot = new Robot(100, 100, 0);

        double maxVelocity = Robot.getMaxVelocity();
        robot.move(maxVelocity * 2, 0, 10, PANEL_WIDTH, PANEL_HEIGHT);

        double expectedMaxDistance = maxVelocity * 10 + 100; // Initial position + max movement
        assertTrue(robot.getPositionX() <= expectedMaxDistance);
    }

    @Test
    public void testAngularVelocityLimits() {
        robot = new Robot(100, 100, 0);
        double maxAngularVelocity = Robot.getMaxAngularVelocity();
        robot.move(0.1, maxAngularVelocity * 2, 10, PANEL_WIDTH, PANEL_HEIGHT);

        assertTrue(robot.getDirection() <= maxAngularVelocity * 10);
    }
}