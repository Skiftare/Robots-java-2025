package gui.ui;

import gui.MainApplicationFrame;
import gui.system.profiling.Profile;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class GameWindowTest {

    // Dummy frame to satisfy constructor and override UI interactions
    private static class DummyMainFrame extends MainApplicationFrame {
        public DummyMainFrame() {
            super(null);
        }

        @Override
        public void updateProgress(int nextLevel) {
            // no-op for testing
        }

        @Override
        public void showLevelSelectionMenu(Profile profile) {
            // no-op for testing
        }

        @Override
        public Profile getProfile() {
            return null;
        }
    }

    @Test
    public void testConstructorSetsProperties() {
        DummyMainFrame frame = new DummyMainFrame();
        GameWindow window = new GameWindow(1, frame);

        // Verify basic JInternalFrame settings
        assertTrue(window.isClosable(), "Window should be closable");
        assertTrue(window.isIconifiable(), "Window should be iconifiable");
        assertTrue(window.isResizable(), "Window should be resizable");
        assertTrue(window.isMaximizable(), "Window should be maximizable");

        // Verify size and position
        assertEquals(800, window.getWidth(), "Width should be 800");
        assertEquals(600, window.getHeight(), "Height should be 600");
        assertEquals(30, window.getX(), "X location should be 30");
        assertEquals(30, window.getY(), "Y location should be 30");

        // Verify visualizer initialization
        assertNotNull(window.getGameVisualizer(), "Visualizer should be initialized");
    }

    @Test
    public void testLoadLevelValidAndInvalid() throws Exception {
        DummyMainFrame frame = new DummyMainFrame();
        GameWindow window = new GameWindow(1, frame);

        // Access private loadLevel method via reflection
        Method loadLevel = GameWindow.class.getDeclaredMethod("loadLevel", int.class);
        loadLevel.setAccessible(true);

        // Valid levels should not throw
        for (int lvl = 1; lvl <= 3; lvl++) {
            final int level = lvl;
            assertDoesNotThrow(() -> loadLevel.invoke(window, level), "loadLevel(" + level + ") should not throw");
        }

        // Invalid level should throw IllegalArgumentException
        Exception ex = assertThrows(Exception.class, () -> loadLevel.invoke(window, 99));
        Throwable cause = ex.getCause();
        assertTrue(cause instanceof IllegalArgumentException, "Expected IllegalArgumentException for invalid level");
    }
}