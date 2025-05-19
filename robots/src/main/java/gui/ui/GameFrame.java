package gui.ui;

import gui.MainApplicationFrame;
import gui.system.profiling.Profile;
import gui.system.profiling.ProfileManager;
import game.factory.GameObjectFactory;
import gui.ui.drawing.GameVisualizer;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JInternalFrame {  // лучше сделать внутренним окном
    private final int currentLevel;
    private final Profile profile;
    private final int totalLevels = 3;

    private final GameVisualizer visualizer;
    private final MainApplicationFrame mainFrame;  // ссылка на главное окно

    public GameFrame(int level, Profile profile, MainApplicationFrame mainFrame) {
        super("Уровень " + level, true, true, true, true);
        this.currentLevel = level;
        this.profile = profile;
        this.mainFrame = mainFrame;

        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        visualizer = new GameVisualizer();
        add(visualizer, BorderLayout.CENTER);

        setSize(800, 600);
        setLocation(50, 50);

        loadLevel(level);
    }

    private void loadLevel(int level) {
        visualizer.getMovementHandler().clearGameObjects();

        switch (level) {
            case 1:
                GameObjectFactory.createLevel1(visualizer.getMovementHandler());
                break;
            case 2:
                GameObjectFactory.createLevel2(visualizer.getMovementHandler());
                break;
            case 3:
                GameObjectFactory.createLevel3(visualizer.getMovementHandler());
                break;
            default:
                throw new IllegalArgumentException("Неизвестный уровень: " + level);
        }

        visualizer.getMovementHandler().getFormulaHandler().processFormulas();
        visualizer.getMovementHandler().recalculateGameState();

        visualizer.repaint();
    }

    public void onLevelWon() {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Уровень пройден!",
                    "Поздравляем",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Следующий уровень", "Заново", "В меню"},
                    "Следующий уровень"
            );

            if (choice == 0) {
                if (currentLevel >= profile.getHighestLevelCompleted()) {
                    profile.setHighestLevelCompleted(currentLevel);
                    ProfileManager.saveProfile(profile);
                }
                int nextLevel = currentLevel + 1;
                if (nextLevel <= totalLevels) {
                    loadLevel(nextLevel);
                } else {
                    JOptionPane.showMessageDialog(this, "Вы прошли все уровни!", "Игра пройдена", JOptionPane.INFORMATION_MESSAGE);
                    openMenu();
                }
            } else if (choice == 1) {
                loadLevel(currentLevel);
            } else {
                openMenu();
            }
        });
    }

    public void onLevelLost() {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Уровень не пройден. Попробовать снова?",
                    "Поражение",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    new Object[]{"Заново", "В меню"},
                    "Заново"
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
            this.setClosed(true);  // закрыть текущее игровое внутреннее окно
        } catch (Exception ignored) {}

        // Показать меню уровней через главное окно
        mainFrame.showLevelSelectionMenu(profile);
    }

    public GameVisualizer getGameVisualizer() {
        return visualizer;
    }
}
