package gui;

import gui.system.ApplicationMenu;
import gui.system.closing.DefaultFrameClosingStrategy;
import gui.system.closing.FrameCloseConfirmationDecorator;
import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.ui.GameWindow;
import gui.ui.LogWindow;
import gui.system.profiling.Profile;
import gui.system.profiling.ProfileManager;
import log.Logger;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static java.lang.Math.round;

public class MainApplicationFrame extends JFrame implements LocaleChangeListener {
    @Getter
    private final JDesktopPane desktopPane = new JDesktopPane();
    private int oldWidth = -1;
    private int oldHeight = -1;
    private final DefaultFrameClosingStrategy closeStrategy;

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

        ApplicationMenu applicationMenu = new ApplicationMenu(this);
        setJMenuBar(applicationMenu);

        updateTitle();

        this.closeStrategy = new DefaultFrameClosingStrategy(
                LocalizationManager.getInstance().getString("close.app.confirm"),
                LocalizationManager.getInstance().getString("close.app.confirm.title")
        );
        FrameCloseConfirmationDecorator.addCloseConfirmation(
                MainApplicationFrame.this,
                closeStrategy,
                () -> {
                    MainApplicationFrame.this.saveProfileOnExit();
                    System.exit(0);
                }
        );

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeInternalFrames();
            }
        });
    }

    public GameWindow getGameWindow() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof GameWindow) {
                return (GameWindow) frame;
            }
        }
        return null;
    }

    void resizeInternalFrames() {
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

    void updateTitle() {
        setTitle(LocalizationManager.getInstance().getString("application.title"));
    }

    @Override
    public void localeChanged() {
        updateTitle();
        SwingUtilities.updateComponentTreeUI(this);
    }

    // Собираем состояние внутренних окон, включая их видимость
    public Profile createProfile(String profileName) {
        Profile profile = new Profile(profileName, LocalizationManager.getInstance().getCurrentLanguage().getLocale().getLanguage());

        // Get all frames ordered by z-order (bottom to top)
        JInternalFrame[] orderedFrames = desktopPane.getAllFrames();
        // Higher z-order means the window is more in front
        int zOrderCounter = 0;

        for (JInternalFrame frame : orderedFrames) {
            String key = frame.getClass().getSimpleName();
            Rectangle bounds = frame.getBounds();
            boolean isIcon = false;
            boolean isMaximum = false;
            try {
                isIcon = frame.isIcon();
                isMaximum = frame.isMaximum();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean isVisible = frame.isVisible();
            // Assign z-order value (lower values for frames at the bottom)
            Profile.FrameState state = new Profile.FrameState(bounds, isIcon, isMaximum, isVisible, zOrderCounter++);
            profile.setFrameState(key, state);
        }
        return profile;
    }
    public void applyProfile(Profile profile) {
        // First apply all basic properties
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String key = frame.getClass().getSimpleName();
            Profile.FrameState state = profile.getFrameState(key);
            if (state != null) {
                frame.setBounds(state.bounds);
                try {
                    frame.setIcon(state.isIcon);
                    frame.setMaximum(state.isMaximum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                frame.setVisible(state.isVisible);
            } else {
                frame.setVisible(false);
            }
        }

        // Process z-order restoration in REVERSE order (highest to lowest)
        // This ensures windows with higher z-order end up on top
        profile.getFrameStates().entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().zOrder, entry1.getValue().zOrder))
                .forEach(entry -> {
                    for (JInternalFrame frame : desktopPane.getAllFrames()) {
                        if (frame.getClass().getSimpleName().equals(entry.getKey()) && frame.isVisible()) {
                            desktopPane.moveToFront(frame);
                            break;
                        }
                    }
                });

        // Force repaint to ensure correct visual appearance
        desktopPane.revalidate();
        desktopPane.repaint();
    }
    // При выходе из приложения запрашивается имя профиля и сохраняется его состояние
    public void saveProfileOnExit() {
        String defaultProfileName = "profile1";
        String profileName = JOptionPane.showInputDialog(this,
                LocalizationManager.getInstance().getString("profile.save.prompt"),
                defaultProfileName);
        if (profileName != null && !profileName.trim().isEmpty()) {
            Profile profile = createProfile(profileName.trim());
            new ProfileManager().saveProfile(profile);
        }
    }
}
