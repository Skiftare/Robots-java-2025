package gui;

import javax.swing.*;
import java.awt.*;
import gui.system.profiling.Profile;
import gui.system.profiling.ProfileManager;
import gui.system.profiling.ProfileChooserDialog;
import gui.system.localization.LocalizationManager;
import gui.system.localization.Language;

public class RobotsProgram {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("InternalFrame.iconifyButtonToolTip", "Свернуть");
            UIManager.put("InternalFrame.maximizeButtonToolTip", "Открыть на полный экран");
            UIManager.put("InternalFrame.closeButtonToolTip", "Закрыть");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ProfileManager profileManager = new ProfileManager();
            java.util.List<Profile> profiles = profileManager.loadProfiles();
            Profile selectedProfile = null;

            if (!profiles.isEmpty()) {
                if (profiles.size() == 1) {
                    int result = JOptionPane.showConfirmDialog(null,
                            LocalizationManager.getInstance().getString("profile.restore.prompt"),
                            LocalizationManager.getInstance().getString("profile.restore.title"),
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        selectedProfile = profiles.get(0);
                    }
                } else {
                    ProfileChooserDialog chooser = new ProfileChooserDialog(null, profiles,
                            LocalizationManager.getInstance().getString("profile.choose.prompt"),
                            LocalizationManager.getInstance().getString("profile.choose.title"));
                    chooser.setVisible(true);
                    selectedProfile = chooser.getSelectedProfile();
                }
            }

            if (selectedProfile == null) {
                selectedProfile = new Profile("default", "en");
            }

            if ("ru".equals(selectedProfile.getLanguage())) {
                LocalizationManager.getInstance().setLanguage(Language.RUSSIAN);
            } else if ("en".equals(selectedProfile.getLanguage())) {
                LocalizationManager.getInstance().setLanguage(Language.ENGLISH);
            }

            MainApplicationFrame frame = new MainApplicationFrame(selectedProfile);
            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }
}

