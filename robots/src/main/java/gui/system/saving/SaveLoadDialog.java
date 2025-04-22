package gui.system.saving;

import gui.system.localization.LocalizationManager;
import gui.ui.drawing.GameVisualizer;

import javax.swing.*;
import java.util.List;

public class SaveLoadDialog {
    public static void showSaveDialog(JFrame parentFrame, GameVisualizer gameVisualizer) {
        String saveName = JOptionPane.showInputDialog(
                parentFrame,
                LocalizationManager.getInstance().getString("dialog.save.name"),
                LocalizationManager.getInstance().getString("dialog.save.title"),
                JOptionPane.QUESTION_MESSAGE
        );

        if (saveName != null && !saveName.trim().isEmpty()) {
            String savePath = GameSaver.saveGameState(gameVisualizer.getGameObjects(), parentFrame, saveName);
            if (savePath != null) {
                MessageDisplayer.showCenteredMessage(parentFrame, "message.saved");
            } else {
                MessageDisplayer.showCenteredMessage(parentFrame, "message.save.failed");
            }
        }
    }

    public static void showLoadDialog(JFrame parentFrame, GameVisualizer gameVisualizer) {
        List<String> saveFiles = GameSaver.getSaveFiles();

        if (saveFiles.isEmpty()) {
            MessageDisplayer.showCenteredMessage(parentFrame, "message.no.saves");
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                parentFrame,
                LocalizationManager.getInstance().getString("dialog.load.prompt"),
                LocalizationManager.getInstance().getString("dialog.load.title"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                saveFiles.toArray(),
                saveFiles.get(0)
        );

        if (selected != null) {
            if (GameLoader.loadGameState(gameVisualizer, parentFrame, selected)) {
                gameVisualizer.repaint();
                MessageDisplayer.showCenteredMessage(parentFrame, "message.loaded");
            } else {
                MessageDisplayer.showCenteredMessage(parentFrame, "message.load.failed");
            }
        }
    }
}