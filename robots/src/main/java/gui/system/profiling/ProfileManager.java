package gui.system.profiling;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProfileManager {
    private static final String PROFILES_DIR = "profiles";

    public ProfileManager() {
        File dir = new File(PROFILES_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Logger.getAnonymousLogger().info("Failed to create profiles directory: " + PROFILES_DIR);
                throw new RuntimeException("Failed to create profiles directory");
            }
        }
    }

    public void saveProfile(Profile profile) {
        try {
            File file = new File(PROFILES_DIR, profile.getProfileName() + ".profile");
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(profile);
            }
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("Could not save profile " + profile.getProfileName());
        }
    }

    public List<Profile> loadProfiles() {
        List<Profile> profiles = new ArrayList<>();
        File dir = new File(PROFILES_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".profile"));
            if (files != null) {
                for (File file : files) {
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                        Profile profile = (Profile) in.readObject();
                        profiles.add(profile);
                    } catch (Exception e) {
                        Logger.getAnonymousLogger().info("Could not load profile " + file.getName());
                    }
                }
            }
        }
        return profiles;
    }
}
