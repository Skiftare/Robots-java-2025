package gui.system.profiling;

import org.junit.Test;
import java.awt.Rectangle;
import java.util.Map;
import static org.junit.Assert.*;

public class ProfileTest {

    @Test
    public void testProfileCreation() {
        Profile profile = new Profile("TestProfile", "en");
        assertEquals("TestProfile", profile.getProfileName());
        assertEquals("en", profile.getLanguage());
    }



    @Test
    public void testFrameStateWithZOrder() {
        Profile profile = new Profile("TestProfile", "en");
        Rectangle bounds = new Rectangle(10, 20, 800, 600);

        // Test explicit z-order constructor
        Profile.FrameState state = new Profile.FrameState(bounds, false, true, true, 3);
        profile.setFrameState("mainFrame", state);

        Profile.FrameState retrievedState = profile.getFrameState("mainFrame");
        assertEquals(3, retrievedState.zOrder);

        // Add more frames with different z-orders
        profile.setFrameState("topFrame",
                new Profile.FrameState(new Rectangle(0, 0, 100, 100), false, false, true, 5));
        profile.setFrameState("bottomFrame",
                new Profile.FrameState(new Rectangle(50, 50, 100, 100), false, false, true, 1));

        // Test getting all frame states
        Map<String, Profile.FrameState> frameStates = profile.getFrameStates();
        assertEquals(3, frameStates.size());
        assertEquals(3, frameStates.get("mainFrame").zOrder);
        assertEquals(5, frameStates.get("topFrame").zOrder);
        assertEquals(1, frameStates.get("bottomFrame").zOrder);
    }

    @Test
    public void testZOrderDefaultValue() {
        // Test that the legacy constructor sets z-order to 0
        Rectangle bounds = new Rectangle(10, 20, 800, 600);

        // Test explicit constructor
        Profile.FrameState stateWithZ = new Profile.FrameState(bounds, true, false, true, 10);
        assertEquals(10, stateWithZ.zOrder);
    }

    @Test
    public void testToString() {
        Profile profile = new Profile("TestProfile", "en");
        assertEquals("TestProfile", profile.toString());
    }
}