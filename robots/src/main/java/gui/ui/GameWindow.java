package gui.ui;

import gui.MainApplicationFrame;
import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.ui.drawing.GameVisualizer;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame implements LocaleChangeListener {
    private final int currentLevel;
    private final MainApplicationFrame mainFrame;
    private final GameVisualizer visualizer;

    public GameWindow(int level, MainApplicationFrame mainFrame) {
        super("", true, true, true, true);
        this.currentLevel = level;
        this.mainFrame = mainFrame;

        LocalizationManager.getInstance().addListener(this);
        updateTitle();

        setSize(800, 600);
        setLocation(30, 30);

        visualizer = new GameVisualizer();
        visualizer.setGameWindow(this);
        getContentPane().add(visualizer, BorderLayout.CENTER);


        loadLevel(level);

        this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
                LocalizationManager.getInstance().removeListener(GameWindow.this);
            }
        });
    }

    private void updateTitle() {
        String titleTemplate = LocalizationManager.getInstance().getString("game.window.title");
        setTitle(String.format(titleTemplate, currentLevel));
    }

    private void loadLevel(int level) {
        visualizer.getMovementHandler().clearGameObjects();

        switch (level) {
            case 1:
                game.factory.GameObjectFactory.createLevel1(visualizer.getMovementHandler());
                break;
            case 2:
                game.factory.GameObjectFactory.createLevel2(visualizer.getMovementHandler());
                break;
            case 3:
                game.factory.GameObjectFactory.createLevel3(visualizer.getMovementHandler());
                break;
            default:
                throw new IllegalArgumentException(
                        LocalizationManager.getInstance().getString("game.window.unknown.level") + ": " + level);
        }

        visualizer.getMovementHandler().getFormulaHandler().processFormulas();
        visualizer.getMovementHandler().recalculateGameState();

        visualizer.repaint();
    }

    public void onLevelWon() {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    LocalizationManager.getInstance().getString("game.level.won.message"),
                    LocalizationManager.getInstance().getString("game.level.won.title"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{
                            LocalizationManager.getInstance().getString("game.level.won.next"),
                            LocalizationManager.getInstance().getString("game.level.won.retry"),
                            LocalizationManager.getInstance().getString("game.level.won.menu")
                    },
                    LocalizationManager.getInstance().getString("game.level.won.next")
            );

            if (choice == 0) {
                int nextLevel = currentLevel + 1;
                mainFrame.updateProgress(nextLevel); // <--- Обновляем прогресс на следующий уровень
                if (nextLevel <= 3) {
                    loadLevel(nextLevel);
                    updateTitle();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            LocalizationManager.getInstance().getString("game.all.levels.completed"),
                            LocalizationManager.getInstance().getString("game.level.won.title"),
                            JOptionPane.INFORMATION_MESSAGE);
                    openMenu();
                }
            } else if (choice == 1) {
                mainFrame.updateProgress(currentLevel);
                loadLevel(currentLevel);
            } else {
                mainFrame.updateProgress(currentLevel);
                openMenu();
            }
        });
    }

    public void onLevelLost() {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    LocalizationManager.getInstance().getString("game.level.lost.message"),
                    LocalizationManager.getInstance().getString("game.level.lost.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    new Object[]{
                            LocalizationManager.getInstance().getString("game.level.won.retry"),
                            LocalizationManager.getInstance().getString("game.level.won.menu")
                    },
                    LocalizationManager.getInstance().getString("game.level.won.retry")
            );

            if (choice == 0) {
                loadLevel(currentLevel);
            } else {
                openMenu();
            }
        });
    }

    private void openMenu() {
        try {
            this.setClosed(true);  // Закрываем текущее окно игры
        } catch (Exception ignored) {}
        mainFrame.showLevelSelectionMenu(mainFrame.getProfile());
    }

    @Override
    public void localeChanged() {
        updateTitle();
    }

    @Override
    public void dispose() {
        LocalizationManager.getInstance().removeListener(this);
        super.dispose();
    }

    public GameVisualizer getGameVisualizer() {
        return visualizer;
    }
}
