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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameLoaderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    private GameVisualizer gameVisualizer;

    private String originalUserHome;
    private File homeDir;
    private File saveDir;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        originalUserHome = System.getProperty("user.home");

        // Setup temp directory as user.home
        homeDir = tempFolder.newFolder("home");
        System.setProperty("user.home", homeDir.getAbsolutePath());

        // Create save directory
        saveDir = new File(homeDir, GameSaver.SAVE_DIR);
        saveDir.mkdirs();
    }

    @After
    public void tearDown() {
        System.setProperty("user.home", originalUserHome);
    }

    private void createSaveFile(File saveFile, ArrayList<GameObject> gameObjects) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            GameState state = new GameState(gameObjects, saveFile.getName());
            oos.writeObject(state);
        }
    }

    @Test
    public void testLoadGameState_FromHomeDirectory() throws IOException {
        // Arrange
        String fileName = "test_home.sav";
        File saveFile = new File(saveDir, fileName);
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        createSaveFile(saveFile, gameObjects);

        // Act
        boolean result = GameLoader.loadGameState(gameVisualizer, fileName);

        // Assert
        assertTrue("Load should succeed when file exists in home directory", result);
        verify(gameVisualizer).rewriteGameObjects(any());
    }

    @Test
    public void testLoadGameState_FileNotFound() {
        // Act
        boolean result = GameLoader.loadGameState(gameVisualizer, "nonexistent.sav");

        // Assert
        assertFalse("Load should fail when file doesn't exist", result);
        verify(gameVisualizer, never()).rewriteGameObjects(any());
    }

    @Test
    public void testLoadGameState_FromCurrentDirectory() throws IOException {
        String fileName = "test_current.sav";
        File saveFile = new File(fileName);
        ArrayList<GameObject> gameObjects = new ArrayList<>();

        try {
            createSaveFile(saveFile, gameObjects);
            boolean result = GameLoader.loadGameState(gameVisualizer, fileName);
            assertTrue("Load should succeed when file exists in current directory", result);
            verify(gameVisualizer).rewriteGameObjects(any());
        } finally {
            saveFile.delete();
        }
    }

    @Test
    public void testLoadGameState_InvalidFile() throws IOException {
        // Arrange
        String fileName = "invalid.sav";
        File saveFile = new File(saveDir, fileName);

        // Create an invalid save file
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write("This is not a valid serialized object".getBytes());
        }

        // Act
        boolean result = GameLoader.loadGameState(gameVisualizer, fileName);

        // Assert
        assertFalse("Load should fail with invalid file content", result);
        verify(gameVisualizer, never()).rewriteGameObjects(any());
    }
}