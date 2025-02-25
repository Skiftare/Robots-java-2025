package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameVisualizerTest {
    private GameVisualizer gameVisualizer;

    @BeforeEach
    void setUp() {
        gameVisualizer = new GameVisualizer();
    }

    @Test
    void testThatTargetPositionChangesWhenClicked() {
        gameVisualizer.setTargetPosition(new Point(200, 300));
        assertEquals(200, gameVisualizer.m_targetPositionX, "Target X coordinate should be updated");
        assertEquals(300, gameVisualizer.m_targetPositionY, "Target Y coordinate should be updated");
    }

    @Test
    void testThatDistanceCalculationIsCorrect() {
        double distance = GameVisualizer.distance(0, 0, 3, 4);
        assertEquals(5.0, distance, 0.01, "Distance should be calculated using Pythagorean theorem");
    }


    @Test
    void testThatRobotWrapsAroundWhenExceedingBounds() {
        gameVisualizer.moveRobot(1000, 0, 10);
        assertTrue(gameVisualizer.m_robotPositionX >= 0 && gameVisualizer.m_robotPositionX <= gameVisualizer.getWidth() + 375,
                "Robot X should wrap around");
        assertTrue(gameVisualizer.m_robotPositionY >= 0 && gameVisualizer.m_robotPositionY <= gameVisualizer.getHeight() + 100,
                "Robot Y should wrap around");
    }

}
