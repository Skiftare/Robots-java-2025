package game.mods;

import gui.MainApplicationFrame;
import gui.system.profiling.Profile;
import gui.ui.ModManagementFrame;
import gui.ui.drawing.GameVisualizer;
import log.WindowLogger;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Global mod manager that persists across the entire application
 */
public class GlobalModManager {
    private static GlobalModManager instance;
    @Getter
    private final ModManager modManager = new ModManager();
    private ModManagementFrame modManagementFrame;
    @Getter
    @Setter
    private MainApplicationFrame mainApplicationFrame;

    private GlobalModManager() {
        // Private constructor for singleton
    }

    public static GlobalModManager getInstance() {
        if (instance == null) {
            instance = new GlobalModManager();
        }
        return instance;
    }

    public void showModManagementWindow() {
        if (mainApplicationFrame == null) {
            WindowLogger.error("Cannot show mod management window: no main frame set");
            return;
        }

        if (modManagementFrame == null || modManagementFrame.isClosed()) {
            modManagementFrame = new ModManagementFrame(modManager);
            mainApplicationFrame.addWindow(modManagementFrame);
        } else {
            try {
                modManagementFrame.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
                WindowLogger.error("Could not focus mod management window: " + e.getMessage());
            }
        }

    }

    // Modify the loadModsFromDirectory method in GlobalModManager
    public void loadModsFromDirectory(String directory) {
        File modsDir = new File(directory);
        if (!modsDir.exists() || !modsDir.isDirectory()) {
            WindowLogger.error("Mods directory does not exist: " + directory);
            return;
        }

        File[] jarFiles = modsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            WindowLogger.debug("No mod JAR files found in directory: " + directory);
            return;
        }

        Arrays.stream(jarFiles).forEach(file -> {
            try {
                modManager.loadModAndStore(file.toPath());
            } catch (Exception e) {
                WindowLogger.error("Failed to load mod from " + file.getName() + ": " + e.getMessage());
            }
        });
    }

    public void loadModsFromProfile(Profile profile) {
        if (profile == null || profile.getModPaths() == null) {
            WindowLogger.debug("Profile is null or has no mod paths");
            return;
        }

        WindowLogger.debug("Loading mods from profile: " + profile.getModPaths().size() + " paths");
        // Load all mods specified in the profile
        for (String path : profile.getModPaths()) {
            if (path == null) continue;

            WindowLogger.debug("Attempting to load mod from: " + path);
            File modFile = new File(path);
            if (modFile.exists() && modFile.isFile()) {
                try {
                    // Use loadModAndStore to ensure mods are properly stored
                    modManager.loadModAndStore(modFile.toPath());
                    WindowLogger.debug("Successfully loaded mod from: " + path);
                } catch (Exception e) {
                    WindowLogger.error("Failed to load mod from profile: " + path + ", error: " + e.getMessage());
                }
            } else {
                WindowLogger.debug("Mod file not found: " + path);
            }
        }
    }

    public void applyToGameVisualizer(GameVisualizer visualizer) {
        if (visualizer != null) {
            visualizer.setModManager(modManager);
        }
    }
    public void saveModsToProfile(Profile profile) {
        if (profile == null) return;

        // Clear existing mod paths
        profile.clearModPaths();

        // Save currently loaded mods to profile
        for (IMod mod : modManager.getLoadedMods()) {
            // Get the source file for this mod if available
            String sourcePath = modManager.getSourcePathForMod(mod.getName());
            if (sourcePath != null) {
                profile.addModPath(sourcePath);
            }
        }
    }


}