package model;

import org.junit.Test;

import static org.testng.Assert.assertEquals;

public class TargetTest {
    private Target target;


    @Test
    public void testInitialPosition() {
        target = new Target(150, 100);
        assertEquals(150, target.getX());
        assertEquals(100, target.getY());
    }

    @Test
    public void testSetPosition() {
        target = new Target(150, 100);
        target.setPosition(200, 300);
        assertEquals(200, target.getX());
        assertEquals(300, target.getY());
    }
}