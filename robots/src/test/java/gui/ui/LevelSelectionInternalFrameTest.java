package gui.ui;

import gui.MainApplicationFrame;
import gui.system.profiling.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.*;

public class LevelSelectionInternalFrameTest {

    private LevelSelectionInternalFrame frame;
    private DummyMainFrame dummyMainFrame;
    private Profile profile;

    private static class DummyMainFrame extends MainApplicationFrame {
        public int openedLevel = -1;

        public DummyMainFrame() {
            super(new Profile("dummy", "en"));
        }

        @Override
        public void showLevelSelectionMenu(Profile profile) {
            // no-op
        }

        @Override
        protected LogWindow createLogWindow() {
            // skip log window creation
            return null;
        }


        @Override
        public void openLevel(int level) {
            this.openedLevel = level;
        }
    }

    @BeforeEach
    public void setUp() {
        profile = new Profile("test", "en");
        profile.setHighestLevelCompleted(0);
        dummyMainFrame = new DummyMainFrame();
        frame = new LevelSelectionInternalFrame(dummyMainFrame, profile);
    }
/*
    @Test
    public void testInitialButtonsState() {
        Component[] comps = frame.getContentPane().getComponents();
        assertEquals(3, comps.length, "Should have three buttons");

        JButton btn1 = (JButton) comps[0];
        JButton btn2 = (JButton) comps[1];
        JButton btn3 = (JButton) comps[2];

        assertTrue(btn1.isEnabled(), "Level 1 button should be enabled");
        assertFalse(btn2.isEnabled(), "Level 2 button should be disabled");
        assertFalse(btn3.isEnabled(), "Level 3 button should be disabled");

        assertTrue(btn1.getText().contains("1"));
        assertTrue(btn2.getText().contains("2"));
        assertTrue(btn3.getText().contains("3"));
    }

    @Test
    public void testUpdateProfileEnablesButtons() {
        profile.setHighestLevelCompleted(1);
        frame.updateProfile(profile);
        Component[] comps = frame.getContentPane().getComponents();
        JButton btn2 = (JButton) comps[1];
        JButton btn3 = (JButton) comps[2];

        assertTrue(btn2.isEnabled(), "Level 2 button should be enabled after profile update");
        assertFalse(btn3.isEnabled(), "Level 3 button should remain disabled");
    }

    @Test
    public void testButtonClickOpensLevelAndDisposesFrame() {
        JButton btn1 = (JButton) frame.getContentPane().getComponent(0);
        btn1.doClick();

        assertEquals(1, dummyMainFrame.openedLevel, "Clicking button should call openLevel(1)");
        assertFalse(frame.isDisplayable(), "Frame should be disposed after button click");
    }*/
}
