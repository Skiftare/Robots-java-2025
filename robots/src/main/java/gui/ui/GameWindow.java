package gui.ui;

import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.system.processing.GameManager;
import gui.system.processing.PlayerController;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame implements LocaleChangeListener {
    private GridVisualizer gridVisualizer;
    private GameManager gameManager;
    private PlayerController playerController;

    public GameWindow() {
        super(LocalizationManager.getInstance().getString("game.window.title"), true, true, true, true);

        // Calculate grid dimensions for 2:3 aspect ratio
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int cellSize = 50; // Fixed cell size

        // Calculate grid width and height based on screen size and 2:3 aspect ratio
        int gridWidth = (int) (screenSize.width * 0.9) / cellSize;
        int gridHeight = (int) (screenSize.height * 0.9) / cellSize;

        // Ensure 2:3 ratio (width:height)
        if (gridWidth > gridHeight * 2 / 3) {
            gridWidth = gridHeight * 2 / 3;
        } else {
            gridHeight = gridWidth * 3 / 2;
        }

        // Create the grid visualizer with calculated dimensions
        gridVisualizer = new GridVisualizer(gridWidth, gridHeight, cellSize);
        gameManager = new GameManager(gridVisualizer.getGrid());
        gridVisualizer.setGameManager(gameManager);

        // Create player controller and attach it to the visualizer
        playerController = new PlayerController(gameManager, gridVisualizer, gridVisualizer.getGrid());
        gameManager.setupLevel();

        JPanel panel = new JPanel(new BorderLayout());
        LocalizationManager.getInstance().addListener(this);
        panel.add(gridVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        // Set to fullscreen
        setSize(screenSize);
        setLocation(0, 0);

        // Request focus for key events
        SwingUtilities.invokeLater(() -> playerController.requestFocus());
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