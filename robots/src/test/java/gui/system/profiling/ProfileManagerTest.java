package gui.system.profiling;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
        // Create test profile
        Profile testProfile = new Profile("TestProfile", "en");
        testProfile.setFrameState("mainFrame",
                new Profile.FrameState(new java.awt.Rectangle(10, 20, 800, 600), false, true, true));

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