package gui;

import model.Robot;
import model.Target;
import org.junit.Test;

import java.awt.Point;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GameVisualizerTest {
    private GameVisualizer gameVisualizer;


    @Test
    public void testTargetPositionChangesWhenClicked() throws Exception {
        // Using reflection to access the private target field
        Field targetField = GameVisualizer.class.getDeclaredField("target");
        targetField.setAccessible(true);
        Target target = (Target) targetField.get(gameVisualizer);

        gameVisualizer.setTargetPosition(new Point(200, 300));

        assertEquals(200, target.getX());
        assertEquals(300, target.getY());
    }

    @Test
    public void testDistanceCalculationIsCorrect() throws Exception {
        // Using reflection to access the private distance method
        Field robotField = GameVisualizer.class.getDeclaredField("robot");
        robotField.setAccessible(true);
        Robot robot = (Robot) robotField.get(gameVisualizer);

        double distance = robot.getPositionX(); // Initial position should be 100
        assertEquals(100.0, distance, 0.01);
    }

    @Test
    public void testRobotWrapsAroundWhenExceedingBounds() throws Exception {
        // Using reflection to access the private robot field
        Field robotField = GameVisualizer.class.getDeclaredField("robot");
        robotField.setAccessible(true);
        Robot robot = (Robot) robotField.get(gameVisualizer);

        // Force the robot to move beyond bounds
        robot.move(1000, 0, 10, gameVisualizer.getWidth() + 375, gameVisualizer.getHeight() + 100);

        assertTrue(robot.getPositionX() >= 0 &&
                        robot.getPositionX() <= gameVisualizer.getWidth() + 375,
                "Robot X should wrap around");
        assertTrue(robot.getPositionY() >= 0 &&
                        robot.getPositionY() <= gameVisualizer.getHeight() + 100,
                "Robot Y should wrap around");
    }
}