package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RobotTest {
    private Robot robot;
    private static final int COLUMNS = 10;
    private static final int ROWS = 10;

    @BeforeEach
    public void setUp() {
        robot = new Robot(5, 5, 0);
    }

    @Test
    public void testInitialPosition() {
        robot = new Robot(3, 4, 1.5);
        int[] position = robot.getPositionInCell();

        assertArrayEquals(new int[]{3, 4}, position, "Initial position should be (3,4)");
        assertEquals(1.5, robot.getDirection(), 0.001, "Initial direction should be 1.5");
    }

    @Test
    public void testSetPosition() {
        robot.setPositionInCell(7, 8);
        int[] position = robot.getPositionInCell();

        assertArrayEquals(new int[]{7, 8}, position, "Position should be updated to (7,8)");
    }

    @Test
    public void testSetDirection() {
        robot.setDirection(2.5);
        assertEquals(2.5, robot.getDirection(), 0.001, "Direction should be updated to 2.5");
    }

    @Test
    public void testMovement() {
        robot = new Robot(5, 5, 0);
        robot.move(2, 3, COLUMNS, ROWS);
        int[] position = robot.getPositionInCell();

        assertArrayEquals(new int[]{7, 8}, position, "Robot should move to (7,8)");
    }

    @Test
    public void testBoundaryLimits() {
        // Test right edge boundary
        robot = new Robot(9, 5, 0);
        robot.move(2, 0, COLUMNS, ROWS);
        assertArrayEquals(new int[]{9, 5}, robot.getPositionInCell(), "Robot should stay at right edge");

        // Test left edge boundary
        robot = new Robot(0, 5, 0);
        robot.move(-1, 0, COLUMNS, ROWS);
        assertArrayEquals(new int[]{0, 5}, robot.getPositionInCell(), "Robot should stay at left edge");

        // Test bottom edge boundary
        robot = new Robot(5, 9, 0);
        robot.move(0, 2, COLUMNS, ROWS);
        assertArrayEquals(new int[]{5, 9}, robot.getPositionInCell(), "Robot should stay at bottom edge");

        // Test top edge boundary
        robot = new Robot(5, 0, 0);
        robot.move(0, -1, COLUMNS, ROWS);
        assertArrayEquals(new int[]{5, 0}, robot.getPositionInCell(), "Robot should stay at top edge");
    }
}