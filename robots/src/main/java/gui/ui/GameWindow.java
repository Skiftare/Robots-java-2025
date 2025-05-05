package gui.ui;

import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.ui.drawing.GameVisualizer;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame implements LocaleChangeListener {
    private GameVisualizer m_visualizer;  // Ссылка на GameVisualizer

    public GameWindow() {
        super(LocalizationManager.getInstance().getString("game.window.title"), true, true, true, true);
        m_visualizer = new GameVisualizer();  // Инициализация GameVisualizer
        JPanel panel = new JPanel(new BorderLayout());
        LocalizationManager.getInstance().addListener(this);
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public GameVisualizer getGameVisualizer() {
        return m_visualizer;  // Возвращаем объект GameVisualizer
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
