package gui.system.profiling;

import org.junit.Test;
import java.awt.Rectangle;
import static org.junit.Assert.*;

public class ProfileTest {

    @Test
    public void testProfileCreation() {
        Profile profile = new Profile("TestProfile", "en");
        assertEquals("TestProfile", profile.getProfileName());
        assertEquals("en", profile.getLanguage());
    }

    @Test
    public void testFrameStateManagement() {
        Profile profile = new Profile("TestProfile", "en");
        Rectangle bounds = new Rectangle(10, 20, 800, 600);
        Profile.FrameState state = new Profile.FrameState(bounds, false, true, true);

        profile.setFrameState("mainFrame", state);
        Profile.FrameState retrievedState = profile.getFrameState("mainFrame");

        assertNotNull(retrievedState);
        assertEquals(bounds, retrievedState.bounds);
        assertFalse(retrievedState.isIcon);
        assertTrue(retrievedState.isMaximum);
        assertTrue(retrievedState.isVisible);
    }

    @Test
    public void testToString() {
        Profile profile = new Profile("TestProfile", "en");
        assertEquals("TestProfile", profile.toString());
    }
}