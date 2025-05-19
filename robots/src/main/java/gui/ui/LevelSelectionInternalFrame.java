package gui.ui;

import gui.MainApplicationFrame;
import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.system.profiling.Profile;

import javax.swing.*;
import java.awt.*;

public class LevelSelectionInternalFrame extends JInternalFrame implements LocaleChangeListener {
    private Profile profile;
    private final MainApplicationFrame mainFrame;
    private final int totalLevels = 3;

    public LevelSelectionInternalFrame(MainApplicationFrame mainFrame, Profile profile) {
        super(LocalizationManager.getInstance().getString("level.selection.title"), true, true, true, true);
        this.profile = profile;
        this.mainFrame = mainFrame;

        LocalizationManager.getInstance().addListener(this);

        // Размер побольше
        int width = 600;
        int height = 400;
        setSize(width, height);

        // Убираем жёсткое setLocation(...)
        // Центрирование после того, как desktopPane получит размеры
        SwingUtilities.invokeLater(() -> {
            Dimension desktopSize = mainFrame.getDesktopPane().getSize();
            int x = (desktopSize.width - getWidth()) + 800;
            int y = (desktopSize.height - getHeight()) / 2;
            setLocation(x, y);
        });

        // Layout и кнопки
        getContentPane().setLayout(new GridLayout(0, 1, 5, 5));
        initButtons();

        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
                LocalizationManager.getInstance().removeListener(LevelSelectionInternalFrame.this);
            }
        });
    }

    public void updateProfile(Profile newProfile) {
        this.profile = newProfile;
        initButtons();
    }

    private void initButtons() {
        // Удаляем все компоненты из content pane, а не из JInternalFrame напрямую
        Container contentPane = getContentPane();
        contentPane.removeAll();

        int highestLevel = profile.getHighestLevelCompleted();

        for (int level = 1; level <= totalLevels; level++) {
            String btnText = String.format(
                    LocalizationManager.getInstance().getString("level.selection.button"),
                    level
            );
            JButton btn = new JButton(btnText + " " + level);
            btn.setEnabled(level <= highestLevel + 1);
            int lvl = level;
            btn.addActionListener(e -> {
                mainFrame.openLevel(lvl);
                this.dispose();
            });
            contentPane.add(btn);
        }

        // Важно: явно указать Layout, если его нет, и обновить окно
        contentPane.setLayout(new GridLayout(0, 1, 5, 5));

        contentPane.revalidate();
        contentPane.repaint();
    }

    @Override
    public void localeChanged() {
        setTitle(LocalizationManager.getInstance().getString("level.selection.title"));
        initButtons();
    }

    @Override
    public void dispose() {
        LocalizationManager.getInstance().removeListener(this);
        super.dispose();
    }
}
