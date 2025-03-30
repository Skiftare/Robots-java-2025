package gui.system.profiling;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final String PROFILES_DIR = "profiles";

    public ProfileManager() {
        File dir = new File(PROFILES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveProfile(Profile profile) {
        try {
            File file = new File(PROFILES_DIR, profile.getProfileName() + ".profile");
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(profile);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                        e.printStackTrace();
                    }
                }
            }
        }
        return profiles;
    }
}
