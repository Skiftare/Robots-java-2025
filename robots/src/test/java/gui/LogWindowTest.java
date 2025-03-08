package gui;

import gui.ui.LogWindow;
import log.LogChangeListener;
import log.LogLevel;
import log.LogWindowSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LogWindowTest {

    private TestLogSource logSource;
    private LogWindow logWindow;
    private JFrame testFrame;
    private JDesktopPane desktop;

    // Custom test LogWindowSource that tracks listener registrations
    static class TestLogSource extends LogWindowSource {
        private int listenerCount = 0;

        public TestLogSource(int queueLength) {
            super(queueLength);
        }

        @Override
        public void registerListener(LogChangeListener listener) {
            listenerCount++;
            super.registerListener(listener);
        }

        @Override
        public void unregisterListener(LogChangeListener listener) {
            listenerCount--;
            super.unregisterListener(listener);
        }

        public int getListenerCount() {
            return listenerCount;
        }
    }

    @Before
    public void setUp() {
        // Skip tests if running in a headless environment
        Assume.assumeFalse("Skipping test in headless environment",
                GraphicsEnvironment.isHeadless());

        // Create test frame with desktop pane to host the internal frame
        testFrame = new JFrame("Test Frame");
        desktop = new JDesktopPane();
        testFrame.setContentPane(desktop);
        testFrame.setSize(800, 600);

        // Create the test log source
        logSource = new TestLogSource(10);

        // Add some initial log entries
        logSource.append(LogLevel.Debug, "Initial log message 1");
        logSource.append(LogLevel.Error, "Initial error message");

        try {
            SwingUtilities.invokeAndWait(() -> {
                // Create log window
                logWindow = new LogWindow(logSource);
                desktop.add(logWindow);

                try {
                    logWindow.setSelected(true);
                } catch (Exception e) {
                    // Ignore - just for display purposes
                }

                testFrame.setVisible(true);
            });
        } catch (Exception e) {
            fail("Exception in test setup: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        if (testFrame != null) {
            testFrame.dispose();
        }
    }

    @Test
    public void testLogWindowInitialization() {
        // Skip tests if running in a headless environment
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        // Check that the window was created properly
        assertNotNull(logWindow);

        // Instead of checking visibility directly, check that it was added to the desktop
        assertTrue("LogWindow should be in the desktop's component list",
                isComponentInContainer(logWindow, desktop));

        // Check the content has been populated
        TextArea logContent = getLogContent();
        assertNotNull(logContent);

        String content = logContent.getText();
        assertTrue(content.contains("Initial log message 1"));
        assertTrue(content.contains("Initial error message"));
    }

    @Test
    public void testLogUpdateNotification() {
        // Skip tests if running in a headless environment
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        // Add a new log entry
        logSource.append(LogLevel.Debug, "New test message");

        // Since UI updates are asynchronous, need to wait a bit
        waitForSwingThread();

        // Check the content has been updated
        TextArea logContent = getLogContent();
        String content = logContent.getText();
        assertTrue(content.contains("New test message"));
    }

    @Test
    public void testWindowDisposal() {
        // This test doesn't require GUI to be visible
        assertEquals(1, logSource.getListenerCount());

        // Dispose the window
        try {
            SwingUtilities.invokeAndWait(() -> logWindow.dispose());
        } catch (Exception e) {
            fail("Exception while disposing window: " + e.getMessage());
        }

        // Verify listener was unregistered
        assertEquals(0, logSource.getListenerCount());

        // The field should be nulled
        LogWindowSource fieldValue = getLogSourceField();
        assertNull(fieldValue);
    }

    @Test
    public void testFrameClosing() {
        // Skip tests if running in a headless environment
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        assertEquals(1, logSource.getListenerCount());

        // Simulate frame closing
        try {
            SwingUtilities.invokeAndWait(() -> {
                InternalFrameEvent e = new InternalFrameEvent(logWindow,
                        InternalFrameEvent.INTERNAL_FRAME_CLOSING);
                logWindow.doDefaultCloseAction();
                logWindow.dispose();
            });
        } catch (Exception e) {
            fail("Exception while closing window: " + e.getMessage());
        }

        // Verify listener count
        assertEquals(0, logSource.getListenerCount());
    }

    @Test
    public void testLogContentDisplayFormat() {
        // Skip tests if running in a headless environment
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        logSource.clear();

        // Add specific entries to test formatting
        logSource.append(LogLevel.Debug, "Debug message");
        logSource.append(LogLevel.Error, "Error message");

        waitForSwingThread();

        // Get content
        TextArea logContent = getLogContent();
        String content = logContent.getText();

        // Verify formatting
        String expected = "Debug message\nError message\n";
        assertEquals(expected, content);
    }

    // Helper method to check if a component is in a container
    private boolean isComponentInContainer(Component component, Container container) {
        Component[] components = container.getComponents();
        for (Component c : components) {
            if (c == component) {
                return true;
            }
        }
        return false;
    }

    // Helper method to wait for Swing thread operations
    private void waitForSwingThread() {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            SwingUtilities.invokeLater(() -> latch.countDown());
            latch.await(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            fail("Exception while waiting for Swing thread: " + e.getMessage());
        }
    }

    // Helper method to access private TextArea field
    private TextArea getLogContent() {
        try {
            Field field = LogWindow.class.getDeclaredField("m_logContent");
            field.setAccessible(true);
            return (TextArea) field.get(logWindow);
        } catch (Exception e) {
            fail("Could not access m_logContent field: " + e.getMessage());
            return null;
        }
    }

    // Helper method to access private LogWindowSource field
    private LogWindowSource getLogSourceField() {
        try {
            Field field = LogWindow.class.getDeclaredField("m_logSource");
            field.setAccessible(true);
            return (LogWindowSource) field.get(logWindow);
        } catch (Exception e) {
            fail("Could not access m_logSource field: " + e.getMessage());
            return null;
        }
    }
}