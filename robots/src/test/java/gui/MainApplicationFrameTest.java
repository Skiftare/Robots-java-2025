package gui;

import gui.ui.GameWindow;
import gui.ui.LevelSelectionInternalFrame;
import gui.system.profiling.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationFrameTest {

    private Profile profile;
    private MainApplicationFrame frame;

    @BeforeEach
    public void setUp() {
        // Initialize profile with default highest level 0
        profile = new Profile("testProfile", "en");
        profile.setHighestLevelCompleted(0);
        frame = new MainApplicationFrame(profile);
    }

    @Test
    public void testInitialState() {
        // Profile should be set correctly
        assertEquals(profile, frame.getProfile());

        // No game window initially
        assertNull(frame.getGameWindow(), "GameWindow should be null initially");

        // LevelSelectionInternalFrame should be added in constructor
        JInternalFrame[] frames = frame.getDesktopPane().getAllFrames();
        boolean hasLevelMenu = false;
        for (JInternalFrame f : frames) {
            if (f instanceof LevelSelectionInternalFrame) {
                hasLevelMenu = true;
                break;
            }
        }
        assertTrue(hasLevelMenu, "LevelSelectionInternalFrame should be present");
    }

    @Test
    public void testShowLevelSelectionMenuIdempotent() {
        int before = frame.getDesktopPane().getAllFrames().length;
        frame.showLevelSelectionMenu(profile);
        int after = frame.getDesktopPane().getAllFrames().length;
        assertEquals(before, after, "Repeated call should not add another LevelSelectionInternalFrame");
    }

    @Test
    public void testOpenLevelCreatesGameWindow() {
        int before = frame.getDesktopPane().getAllFrames().length;
        frame.openLevel(2);

        GameWindow gw = frame.getGameWindow();
        assertNotNull(gw, "GameWindow should be created after openLevel");
        assertTrue(gw instanceof GameWindow);

        int after = frame.getDesktopPane().getAllFrames().length;
        assertEquals(before + 1, after, "openLevel should add a GameWindow to the desktopPane");
    }

    @Test
    public void testUpdateProgress() {
        // Increase to a higher level
        frame.updateProgress(1);
        assertEquals(1, profile.getHighestLevelCompleted(), "Profile should update to higher level");

        // Calling with same level should not decrease or change
        frame.updateProgress(1);
        assertEquals(1, profile.getHighestLevelCompleted());

        // Calling with lower level should not change
        frame.updateProgress(0);
        assertEquals(1, profile.getHighestLevelCompleted());
    }
}
