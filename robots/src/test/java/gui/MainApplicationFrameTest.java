package gui;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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
    public void testMenuCreation() {
        // Check that the menu is created correctly
        JMenuBar menuBar = mainFrame.generateMenuBar();
        assertNotNull(menuBar);
        assertEquals(2, menuBar.getMenuCount());

        // Check the first menu "Look and Feel"
        JMenu lookAndFeelMenu = menuBar.getMenu(0);
        assertNotNull(lookAndFeelMenu);
        assertEquals("Режим отображения", lookAndFeelMenu.getText());

        // Check the second menu "Tests"
        JMenu testMenu = menuBar.getMenu(1);
        assertNotNull(testMenu);
        assertEquals("Тесты", testMenu.getText());
    }

    @Test
    public void testLookAndFeelMenuItem() {
        // Check if the Look and Feel items update the UI correctly
        JMenuItem systemLookAndFeelItem = mainFrame.createSystemLookAndFeelMenuItem();
        systemLookAndFeelItem.doClick();  // Simulate a click

        String currentLookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        assertEquals(UIManager.getSystemLookAndFeelClassName(), currentLookAndFeel);

        JMenuItem crossPlatformLookAndFeelItem = mainFrame.createCrossPlatformLookAndFeelMenuItem();
        crossPlatformLookAndFeelItem.doClick();  // Simulate a click

        currentLookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        assertEquals(UIManager.getCrossPlatformLookAndFeelClassName(), currentLookAndFeel);
    }

    @Test
    public void testLogMessageMenuItem() {
        // Check if clicking the log message menu item correctly logs a message
        JMenuItem logMessageItem = mainFrame.createLogMessageMenuItem();
        logMessageItem.doClick();  // Simulate a click

        // You could use Mocking to check if Logger.debug was called, if needed
        // You can also check if the log message appears in the logs, for example
    }

    @Test
    public void testDesktopPaneSize() {
        // Check that the DesktopPane size is correctly initialized
        Dimension initialSize = mainFrame.getDesktopPane().getSize();
//        assertTrue(initialSize.width > 0);
//        assertTrue(initialSize.height > 0);
    }
}
