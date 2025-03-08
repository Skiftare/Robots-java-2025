package gui.ui;

import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame implements LocaleChangeListener {
    public GameWindow() {
        super(LocalizationManager.getInstance().getString("game.window.title"), true, true, true, true);
        GameVisualizer m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        LocalizationManager.getInstance().addListener(this);
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void localeChanged() {
        setTitle(LocalizationManager.getInstance().getString("game.window.title"));
    }
    @Override
    public void dispose() {
        LocalizationManager.getInstance().removeListener(this);
        super.dispose();
    }
}
