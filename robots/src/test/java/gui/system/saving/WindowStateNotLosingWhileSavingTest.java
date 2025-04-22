package gui.system.saving;

import game.model.GameObject;
import game.model.GameState;
import gui.ui.drawing.GameVisualizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WindowStateNotLosingWhileSavingTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    private GameVisualizer gameVisualizer;

    private JFrame testFrame;
    private String originalUserHome;
    private File homeDir;
    private File saveDir;
    private ArrayList<GameObject> gameObjects;
    private static final String TEST_SAVE_NAME = "windowStateTest";

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        originalUserHome = System.getProperty("user.home");

        homeDir = tempFolder.newFolder("home");
        System.setProperty("user.home", homeDir.getAbsolutePath());

        saveDir = new File(homeDir, GameSaver.SAVE_DIR);
        saveDir.mkdirs();

        testFrame = new JFrame("Test Frame");
        testFrame.setSize(800, 600);
        testFrame.setLocation(100, 100);

        gameObjects = new ArrayList<>();

        // Set up game visualizer mock
        when(gameVisualizer.getGameObjects()).thenReturn(gameObjects);
        when(gameVisualizer.getParent()).thenReturn(testFrame);
    }

    @After
    public void tearDown() {
        System.setProperty("user.home", originalUserHome);
        if (testFrame != null) {
            testFrame.dispose();
        }
    }

    @Test
    public void testWindowStatePreservedWhenMinimized() {
        // Setup initial window state
        Dimension originalSize = new Dimension(800, 600);
        Point originalLocation = new Point(100, 100);
        testFrame.setSize(originalSize);
        testFrame.setLocation(originalLocation);
        testFrame.setExtendedState(Frame.NORMAL);

        // Minimize window
        testFrame.setExtendedState(Frame.ICONIFIED);

        // Save using the actual GameSaver
        String savePath = GameSaver.saveGameState(gameObjects, TEST_SAVE_NAME);
        assertNotNull("Failed to save game state", savePath);

        // Change window properties to ensure restoration works
        testFrame.setExtendedState(Frame.NORMAL);
        testFrame.setSize(400, 300);
        testFrame.setLocation(200, 200);

        // Load using the actual GameLoader
        boolean loaded = GameLoader.loadGameState(gameVisualizer, TEST_SAVE_NAME + ".sav");
        assertTrue("Failed to load game state", loaded);

        // These assertions will fail with the current implementation
        // because window state isn't being saved
        assertEquals("Window should be minimized", Frame.ICONIFIED, testFrame.getExtendedState());

        // Return to normal state to check dimensions
        testFrame.setExtendedState(Frame.NORMAL);
        assertEquals("Window width should be preserved", originalSize.width, testFrame.getWidth());
        assertEquals("Window height should be preserved", originalSize.height, testFrame.getHeight());
        assertEquals("Window X position should be preserved", originalLocation.x, testFrame.getLocation().x);
        assertEquals("Window Y position should be preserved", originalLocation.y, testFrame.getLocation().y);
    }

    @Test
    public void testWindowStatePreservedWhenMaximized() {
        // Setup initial window state
        Dimension originalSize = new Dimension(800, 600);
        Point originalLocation = new Point(100, 100);
        testFrame.setSize(originalSize);
        testFrame.setLocation(originalLocation);
        testFrame.setExtendedState(Frame.NORMAL);

        // Maximize window
        testFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        // Save using the actual GameSaver
        String savePath = GameSaver.saveGameState(gameObjects, TEST_SAVE_NAME);
        assertNotNull("Failed to save game state", savePath);

        // Change window properties to ensure restoration works
        testFrame.setExtendedState(Frame.NORMAL);
        testFrame.setSize(400, 300);
        testFrame.setLocation(200, 200);

        // Load using the actual GameLoader
        boolean loaded = GameLoader.loadGameState(gameVisualizer, TEST_SAVE_NAME + ".sav");
        assertTrue("Failed to load game state", loaded);

        // These assertions will fail with the current implementation
        // because window state isn't being saved
        assertEquals("Window should be maximized", Frame.MAXIMIZED_BOTH, testFrame.getExtendedState());

        // Return to normal state to check dimensions
        testFrame.setExtendedState(Frame.NORMAL);
        assertEquals("Original width should be preserved", originalSize.width, testFrame.getWidth());
        assertEquals("Original height should be preserved", originalSize.height, testFrame.getHeight());
        assertEquals("Original X position should be preserved", originalLocation.x, testFrame.getLocation().x);
        assertEquals("Original Y position should be preserved", originalLocation.y, testFrame.getLocation().y);
    }
}