package gui.system.saving;

import game.model.GameState;
import gui.ui.drawing.GameVisualizer;
import log.WindowLogger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameLoader {
    /**
     * Load game from specified save file name
     */
    public static boolean loadGameState(GameVisualizer gameVisualizer, String fileName) {
        // First try user home directory
        String userHome = System.getProperty("user.home");
        Path savePath = Paths.get(userHome, GameSaver.SAVE_DIR, fileName);

        if (!Files.exists(savePath)) {
            // If not found, try current directory
            savePath = Paths.get(fileName);
            if (!Files.exists(savePath)) {
                WindowLogger.error("Save file not found: " + fileName);
                return false;
            }
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(savePath.toFile())))) {

            GameState state = (GameState) ois.readObject();
            gameVisualizer.rewriteGameObjects(state.getGameObjects());

            return true;
        } catch (Exception e) {
            WindowLogger.error("Failed to load game: " + e.getMessage());
            return false;
        }
    }

}