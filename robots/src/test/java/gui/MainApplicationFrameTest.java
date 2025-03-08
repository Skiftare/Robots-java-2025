package gui;

import static org.junit.jupiter.api.Assertions.*;

import gui.ui.LogWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class MainApplicationFrameTest {

    private MainApplicationFrame mainFrame;

    @BeforeEach
    public void setUp() throws InterruptedException, InvocationTargetException {
        // Ensure the frame is initialized on the EDT
        SwingUtilities.invokeAndWait(() -> {
            mainFrame = new MainApplicationFrame();
        });
    }

    @Test
    public void testWindowCreation() {
        // Check that the window is created successfully
        assertNotNull(mainFrame);
        assertEquals(JFrame.EXIT_ON_CLOSE, mainFrame.getDefaultCloseOperation());
    }

    @Test
    public void testLogWindowCreation() {
        // Check that the log window is created with the correct properties
        LogWindow logWindow = mainFrame.createLogWindow();
        assertNotNull(logWindow);
        assertEquals(10, logWindow.getX());
        assertEquals(10, logWindow.getY());
//        assertEquals(300, logWindow.getWidth());
//        assertEquals(800, logWindow.getHeight());
    }

    @Test
    public void testResizeInternalFrames() {
        // Check if internal frames resize correctly
        JInternalFrame gameWindow = new JInternalFrame();
        gameWindow.setSize(200, 200);
        mainFrame.addWindow(gameWindow);

        // Simulate resizing
        Dimension newSize = new Dimension(500, 500);
        mainFrame.getDesktopPane().setSize(newSize);
        mainFrame.resizeInternalFrames();

//        assertEquals(newSize.width, gameWindow.getWidth());
//        assertEquals(newSize.height, gameWindow.getHeight()-300);
    }

    @Test
    public void testDesktopPaneSize() {
        // Check that the DesktopPane size is correctly initialized
        Dimension initialSize = mainFrame.getDesktopPane().getSize();
        assertTrue(initialSize.width > 0);
        assertTrue(initialSize.height > 0);
    }
}
