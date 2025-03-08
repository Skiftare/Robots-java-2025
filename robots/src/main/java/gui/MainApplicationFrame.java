package gui;

import gui.system.ApplicationMenu;
import gui.system.closing.DefaultFrameClosingStrategy;
import gui.system.closing.FrameCloseConfirmationDecorator;
import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.ui.GameWindow;
import gui.ui.LogWindow;
import log.Logger;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Math.round;

public class MainApplicationFrame extends JFrame implements LocaleChangeListener {
    @Getter
    private final JDesktopPane desktopPane = new JDesktopPane();
    private int oldWidth = -1;
    private int oldHeight = -1;

    private final ApplicationMenu applicationMenu;

    public MainApplicationFrame() {
        LocalizationManager.getInstance().addListener(this);

        int inset = 50;
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();
        Dimension screenSize = new Dimension(screenBounds.width, screenBounds.height);
        setBounds(inset, inset, screenSize.width, screenSize.height);

        setContentPane(desktopPane);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(screenSize.width, screenSize.height);
        addWindow(gameWindow);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        // Create and set menu bar
        applicationMenu = new ApplicationMenu(this);
        setJMenuBar(applicationMenu);

        updateTitle();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DefaultFrameClosingStrategy closingStrategy = new DefaultFrameClosingStrategy(
                        LocalizationManager.getInstance().getString("close.app.confirm"),
                        LocalizationManager.getInstance().getString("close.app.confirm.title"));

                int result = JOptionPane.showConfirmDialog(
                        MainApplicationFrame.this,
                        closingStrategy.getMessage(),
                        closingStrategy.getTitle(),
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeInternalFrames();
            }
        });
    }

    void resizeInternalFrames() {
        // Existing resize code remains the same
        SwingUtilities.invokeLater(() -> {
            int width = desktopPane.getWidth();
            int height = desktopPane.getHeight();

            if (width == 0 || height == 0 || oldWidth <= 0 || oldHeight <= 0) {
                oldWidth = width;
                oldHeight = height;
                return;
            }

            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                double widthRatio = (double) frame.getWidth() / oldWidth;
                double heightRatio = (double) frame.getHeight() / oldHeight;
                double xRatio = (double) frame.getX() / oldWidth;
                double yRatio = (double) frame.getY() / oldHeight;

                int newWidth = (int) round(width * widthRatio);
                int newHeight = (int) round(height * heightRatio);
                int newX = (int) round(width * xRatio);
                int newY = (int) round(height * yRatio);

                newWidth = Math.min(newWidth, width - newX);
                newHeight = Math.min(newHeight, height - newY);

                frame.setBounds(newX, newY, newWidth, newHeight);
                frame.revalidate();
                frame.repaint();
            }

            oldWidth = width;
            oldHeight = height;

            desktopPane.revalidate();
            desktopPane.repaint();
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(LocalizationManager.getInstance().getString("log.message.system.health"));
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        FrameCloseConfirmationDecorator.addCloseConfirmation(frame);
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void updateTitle() {
        setTitle(LocalizationManager.getInstance().getString("application.title"));
    }

    @Override
    public void localeChanged() {
        updateTitle();
        applicationMenu.updateLocalization();
        // Force UI refresh
        SwingUtilities.updateComponentTreeUI(this);
    }
}