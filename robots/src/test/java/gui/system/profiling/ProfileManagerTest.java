package gui.system.profiling;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.awt.Rectangle;
import java.util.List;
import static org.junit.Assert.*;

public class ProfileManagerTest {

    private ProfileManager profileManager;
    private static final String TEST_PROFILES_DIR = "profiles";

    @Before
    public void setUp() {
        // Create a fresh ProfileManager for each test
        profileManager = new ProfileManager();

        // Clear test profiles directory
        File dir = new File(TEST_PROFILES_DIR);
        if (dir.exists()) {
            for (File file : dir.listFiles((d, name) -> name.endsWith(".profile"))) {
                file.delete();
            }
        }
    }

    @After
    public void tearDown() {
        // Cleanup test files
        File dir = new File(TEST_PROFILES_DIR);
        if (dir.exists()) {
            for (File file : dir.listFiles((d, name) -> name.endsWith(".profile"))) {
                file.delete();
            }
        }
    }

    @Test
    public void testSaveAndLoadProfile() {
        // Create test profile with z-order information
        Profile testProfile = new Profile("TestProfile", "en");
        testProfile.setFrameState("mainFrame",
                new Profile.FrameState(new Rectangle(10, 20, 800, 600), false, true, true, 0));

        // Save profile
        profileManager.saveProfile(testProfile);

        // Check file was created
        File profileFile = new File(TEST_PROFILES_DIR, "TestProfile.profile");
        assertTrue("Profile file was not created", profileFile.exists());

        // Load profiles
        List<Profile> loadedProfiles = profileManager.loadProfiles();

        // Verify profile was loaded correctly
        assertFalse("No profiles were loaded", loadedProfiles.isEmpty());
        Profile loadedProfile = loadedProfiles.stream()
                .filter(p -> p.getProfileName().equals("TestProfile"))
                .findFirst()
                .orElse(null);

        assertNotNull("TestProfile wasn't loaded", loadedProfile);
        assertEquals("en", loadedProfile.getLanguage());

        Profile.FrameState loadedState = loadedProfile.getFrameState("mainFrame");
        assertNotNull("Frame state wasn't loaded", loadedState);
        assertEquals(10, loadedState.bounds.x);
        assertEquals(20, loadedState.bounds.y);
        assertEquals(0, loadedState.zOrder);
    }

    @Test
    public void testZOrderPreservation() {
        // Create test profile with multiple windows at different z-levels
        Profile testProfile = new Profile("ZOrderTest", "en");

        // Add frames with specific z-order values (bottom to top)
        testProfile.setFrameState("bottomFrame",
                new Profile.FrameState(new Rectangle(0, 0, 100, 100), false, false, true, 0));
        testProfile.setFrameState("middleFrame",
                new Profile.FrameState(new Rectangle(50, 50, 100, 100), false, false, true, 1));
        testProfile.setFrameState("topFrame",
                new Profile.FrameState(new Rectangle(100, 100, 100, 100), false, false, true, 2));

        // Save and reload the profile
        profileManager.saveProfile(testProfile);
        List<Profile> loadedProfiles = profileManager.loadProfiles();

        Profile loadedProfile = loadedProfiles.stream()
                .filter(p -> p.getProfileName().equals("ZOrderTest"))
                .findFirst()
                .orElse(null);

        assertNotNull("ZOrderTest profile wasn't loaded", loadedProfile);

        // Verify z-order values were preserved
        assertEquals(0, loadedProfile.getFrameState("bottomFrame").zOrder);
        assertEquals(1, loadedProfile.getFrameState("middleFrame").zOrder);
        assertEquals(2, loadedProfile.getFrameState("topFrame").zOrder);
    }

    @Test
    public void testDirectoryCreation() {
        // Delete profiles directory if it exists
        File dir = new File(TEST_PROFILES_DIR);
        if (dir.exists()) {
            dir.delete();
        }

        // Creating a new ProfileManager should create the directory
        new ProfileManager();

        assertTrue("Profiles directory was not created", dir.exists());
        assertTrue("Profiles path is not a directory", dir.isDirectory());
    }
}