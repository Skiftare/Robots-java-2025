package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TargetTest {
    private Target target;
    private static final int COLUMNS = 20;
    private static final int ROWS = 20;

    @BeforeEach
    public void setUp() {
        target = new Target(10, 10);
    }

    @Test
    public void testInitialPosition() {
        target = new Target(15, 10);
        assertEquals(15, target.getX(), "Initial X position should be 15");
        assertEquals(10, target.getY(), "Initial Y position should be 10");
    }

    @Test
    public void testSetPosition() {
        target.setPosition(12, 13);
        assertEquals(12, target.getX(), "X position should be updated to 12");
        assertEquals(13, target.getY(), "Y position should be updated to 13");
    }

    @Test
    public void testSetPositionWithBoundsValid() {
        target.setPositionWithBounds(15, 15, COLUMNS, ROWS);
        assertEquals(15, target.getX(), "X position should be updated to 15 when within bounds");
        assertEquals(15, target.getY(), "Y position should be updated to 15 when within bounds");
    }

    @Test
    public void testSetPositionWithBoundsOutOfBoundsX() {
        target.setPositionWithBounds(-1, 10, COLUMNS, ROWS);
        assertEquals(10, target.getX(), "X position should not change when new position is negative");
        assertEquals(10, target.getY(), "Y position should not change when X is invalid");

        target.setPositionWithBounds(COLUMNS, 10, COLUMNS, ROWS);
        assertEquals(10, target.getX(), "X position should not change when new position exceeds columns");
        assertEquals(10, target.getY(), "Y position should not change when X is invalid");
    }

    @Test
    public void testSetPositionWithBoundsOutOfBoundsY() {
        target.setPositionWithBounds(10, -1, COLUMNS, ROWS);
        assertEquals(10, target.getX(), "X position should not change when Y is invalid");
        assertEquals(10, target.getY(), "Y position should not change when new position is negative");

        target.setPositionWithBounds(10, ROWS, COLUMNS, ROWS);
        assertEquals(10, target.getX(), "X position should not change when Y is invalid");
        assertEquals(10, target.getY(), "Y position should not change when new position exceeds rows");
    }
}