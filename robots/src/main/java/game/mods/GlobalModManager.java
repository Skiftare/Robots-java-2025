package game.mods;

import gui.MainApplicationFrame;
import gui.system.profiling.Profile;
import gui.ui.ModManagementFrame;
import gui.ui.drawing.GameVisualizer;
import log.WindowLogger;

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
    private final ModManager modManager = new ModManager();
    private ModManagementFrame modManagementFrame;
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

    public void setMainApplicationFrame(MainApplicationFrame frame) {
        this.mainApplicationFrame = frame;
    }
    public MainApplicationFrame getMainApplicationFrame() {
        return mainApplicationFrame;
    }

    public ModManager getModManager() {
        return modManager;
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
                modManager.loadMod(file.toPath());
            } catch (Exception e) {
                WindowLogger.error("Failed to load mod from " + file.getName() + ": " + e.getMessage());
            }
        });
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

    public void loadModsFromProfile(Profile profile) {
        if (profile == null || profile.getModPaths() == null) return;

        // Load all mods specified in the profile
        for (String path : profile.getModPaths()) {
            if (path == null) continue;

            File modFile = new File(path);
            if (modFile.exists() && modFile.isFile()) {
                try {
                    modManager.loadMod(modFile.toPath());
                } catch (Exception e) {
                    WindowLogger.error("Failed to load mod from profile: " + path);
                }
            } else {
                WindowLogger.debug("Mod file not found: " + path);
            }
        }
    }

}