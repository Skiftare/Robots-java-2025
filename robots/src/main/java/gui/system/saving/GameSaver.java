package gui.system.saving;

import game.model.GameObject;
import game.model.GameState;
import log.WindowLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameSaver {
    public static final String SAVE_DIR = "robotssaves";
    public static final String DEFAULT_SAVE_NAME = "save";

    /**
     * Returns the save directory path
     */
    private static Path getSaveDir() {
        // Get user home directory for cross-platform compatibility
        String userHome = System.getProperty("user.home");
        Path savePath = Paths.get(userHome, SAVE_DIR);

        // Create directory if it doesn't exist
        try {
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }
        } catch (IOException e) {
            WindowLogger.error("Failed to create save directory: " + e.getMessage());
        }

        return savePath;
    }

    /**
     * Save game state with automatic filename
     */
    public static String saveGameState(List<GameObject> gameObjects, String fileName) {
        if (!fileName.endsWith(".sav")) {
            fileName += ".sav";
        }

        // Try to save in user home directory
        Path savePath = getSaveDir().resolve(fileName);
        GameState state = new GameState(new ArrayList<>(gameObjects), fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(savePath.toFile())))) {
            oos.writeObject(state);
            return savePath.toString();
        } catch (IOException e) {
            // If failed, try to save in current directory
            WindowLogger.debug("Failed to save in home directory, trying local directory");
            try {
                savePath = Paths.get(fileName);
                try (ObjectOutputStream oos = new ObjectOutputStream(
                        new BufferedOutputStream(new FileOutputStream(savePath.toFile())))) {
                    oos.writeObject(state);
                    return savePath.toString();
                }
            } catch (IOException e2) {
                WindowLogger.error("Failed to save game: " + e2.getMessage());
                return null;
            }
        }
    }

    /**
     * Get list of all available save files from both directories
     */
    public static List<String> getSaveFiles() {
        List<String> saveFiles = new ArrayList<>();

        // Check home directory
        File saveDir = getSaveDir().toFile();
        if (saveDir.exists() && saveDir.isDirectory()) {
            File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
            if (files != null) {
                for (File file : files) {
                    saveFiles.add(file.getName());
                }
            }
        }

        // Check current directory
        File currentDir = new File(".");
        File[] localFiles = currentDir.listFiles((dir, name) -> name.endsWith(".sav"));
        if (localFiles != null) {
            for (File file : localFiles) {
                if (!saveFiles.contains(file.getName())) {
                    saveFiles.add(file.getName());
                }
            }
        }

        return saveFiles;
    }
}