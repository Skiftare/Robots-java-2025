package gui.system.saving;

import game.model.GameObject;
import game.model.GameState;
import gui.ui.drawing.GameVisualizer;
import log.WindowLogger;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class GameLoader {
    /**
     * Load game from specified save file name
     */
    public static boolean loadGameState(GameVisualizer gameVisualizer, JFrame frame, String fileName) {
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
            gameVisualizer.rewriteGameObjects((ArrayList<GameObject>) state.getGameObjects());

            // Recalculate formulas and game state
            gameVisualizer.getMovementHandler().getFormulaHandler().processFormulas();
            gameVisualizer.getMovementHandler().recalculateGameState();

            // Check if the window was maximized and minimized
            final boolean wasMaximized = (state.getWindowExtendedState() & Frame.MAXIMIZED_BOTH) != 0;
            final boolean wasIconified = (state.getWindowExtendedState() & Frame.ICONIFIED) != 0;
            final int width = state.getWindowWidth();
            final int height = state.getWindowHeight();
            final int x = state.getWindowX();
            final int y = state.getWindowY();
            Logger.getAnonymousLogger().info("Game loaded from: " + savePath.toString() +
                    ", width: " + width + ", height: " + height +
                    ", x: " + x + ", y: " + y +
                    ", wasMaximized: " + wasMaximized +
                    ", wasIconified: " + wasIconified);
            SwingUtilities.invokeLater(() -> {
                // First reset to normal state
                frame.setExtendedState(Frame.NORMAL);

                // Set size and position
                frame.setSize(width, height);
                frame.setLocation(x, y);

                // Then handle maximization first
                if (wasMaximized) {

                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                }

                // Finally handle minimization - this must be in a separate invokeLater to work correctly
                if (wasIconified) {
                    SwingUtilities.invokeLater(() -> {
                        // Make sure we don't lose the maximized state if it was set
                        int currentState = frame.getExtendedState();
                        frame.setExtendedState(currentState | Frame.ICONIFIED);
                    });
                }
            });

            return true;

        } catch (Exception e) {
            WindowLogger.error("Failed to load game: " + e.getMessage());
            return false;
        }
    }}