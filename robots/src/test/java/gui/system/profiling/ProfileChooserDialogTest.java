package gui.system.profiling;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProfileChooserDialogTest {

    private List<Profile> testProfiles;
    private JFrame frame;

    @BeforeClass
    public static void setUpClass() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                new JFrame().getGraphics();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        // Create test profiles
        testProfiles = new ArrayList<>();
        testProfiles.add(new Profile("Profile1", "en"));
        testProfiles.add(new Profile("Profile2", "ru"));

        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new JFrame("Test Frame");
                frame.setSize(400, 300);
                //Тестовое окружение, поэтому так. В целом можно убрать.
                frame.setVisible(false);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDialogCreation() {
        try {
            final ProfileChooserDialog[] dialog = new ProfileChooserDialog[1];

            SwingUtilities.invokeAndWait(() -> {
                dialog[0] = new ProfileChooserDialog(
                        frame, testProfiles, "Select a profile", "Profile Selection");
            });

            assertEquals("Profile Selection", dialog[0].getTitle());
            assertTrue(dialog[0].isModal());
            assertNull(dialog[0].getSelectedProfile());

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testProfileSelection() {
        try {
            final ProfileChooserDialog[] dialog = new ProfileChooserDialog[1];

            SwingUtilities.invokeAndWait(() -> {
                dialog[0] = new ProfileChooserDialog(
                        frame, testProfiles, "Select a profile", "Profile Selection");

                //Да, рефлексия. Простите. Но getter-ы ради тестов кажется тоже так себе идея.
                try {
                    java.lang.reflect.Field field = ProfileChooserDialog.class.getDeclaredField("selectedProfile");
                    field.setAccessible(true);
                    field.set(dialog[0], testProfiles.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Profile selected = dialog[0].getSelectedProfile();
            assertNotNull("Selected profile should not be null", selected);
            assertEquals("Profile1", selected.getProfileName());

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}