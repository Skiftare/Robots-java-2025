package gui;

import gui.system.ApplicationMenu;
import gui.system.closing.DefaultFrameClosingStrategy;
import gui.system.closing.FrameCloseConfirmationDecorator;
import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.system.profiling.Profile;
import gui.system.profiling.ProfileManager;
import gui.ui.GameWindow;
import gui.ui.LogWindow;
import log.WindowLogger;
import lombok.Getter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.round;

public class MainApplicationFrame extends JFrame implements LocaleChangeListener {
    @Getter
    private final JDesktopPane desktopPane = new JDesktopPane();
    private int oldWidth = -1, oldHeight = -1;
    private final DefaultFrameClosingStrategy closeStrategy;
    private Profile currentProfile;

    public MainApplicationFrame() {
        LocalizationManager.getInstance().addListener(this);

        // размеры главного окна
        int inset = 50;
        Rectangle screen = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getBounds();
        setBounds(inset, inset, screen.width, screen.height);
        setContentPane(desktopPane);

        // рабочие окна
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(screen.width, screen.height);
        addWindow(gameWindow);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        setJMenuBar(new ApplicationMenu(this));
        updateTitle();

        // на закрытие — спросить, сохранить профиль
        closeStrategy = new DefaultFrameClosingStrategy(
                LocalizationManager.getInstance().getString("close.app.confirm"),
                LocalizationManager.getInstance().getString("close.app.confirm.title")
        );
        FrameCloseConfirmationDecorator.addCloseConfirmation(
                this, closeStrategy,
                () -> {
                    saveProfileOnExit();
                    System.exit(0);
                }
        );

        // слушаем изменение размеров, чтобы ресайзить внутренние фреймы
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeInternalFrames();
            }
        });
    }

    protected void addWindow(JInternalFrame frame) {
        FrameCloseConfirmationDecorator.addCloseConfirmation(frame);
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    protected LogWindow createLogWindow() {
        LogWindow w = new LogWindow(WindowLogger.getDefaultLogSource());
        w.setLocation(10, 10);
        w.setSize(300, 800);
        setMinimumSize(w.getSize());
        w.pack();
        WindowLogger.debug(LocalizationManager.getInstance()
                .getString("log.message.system.health"));
        return w;
    }

    @Override
    public void localeChanged() {
        updateTitle();
        SwingUtilities.updateComponentTreeUI(this);
    }

    void updateTitle() {
        setTitle(LocalizationManager.getInstance().getString("application.title"));
    }

    public GameWindow getGameWindow() {
        for (JInternalFrame f : desktopPane.getAllFrames())
            if (f instanceof GameWindow) return (GameWindow) f;
        return null;
    }

    void resizeInternalFrames() {
        SwingUtilities.invokeLater(() -> {
            int w = desktopPane.getWidth(), h = desktopPane.getHeight();
            if (w <= 0 || h <= 0 || oldWidth <= 0 || oldHeight <= 0) {
                oldWidth = w;
                oldHeight = h;
                return;
            }
            for (JInternalFrame f : desktopPane.getAllFrames()) {
                double wr = (double) f.getWidth() / oldWidth;
                double hr = (double) f.getHeight() / oldHeight;
                double xr = (double) f.getX() / oldWidth;
                double yr = (double) f.getY() / oldHeight;
                int nw = (int) round(w * wr), nh = (int) round(h * hr);
                int nx = (int) round(w * xr), ny = (int) round(h * yr);
                nw = Math.min(nw, w - nx);
                nh = Math.min(nh, h - ny);
                f.setBounds(nx, ny, nw, nh);
                f.revalidate();
                f.repaint();
            }
            oldWidth = w;
            oldHeight = h;
        });
    }

    /**
     * Собираем профиль: оба флага хранить независимо.
     */
    public Profile createProfile(String profileName) {
        Profile p = new Profile(
                profileName,
                LocalizationManager.getInstance().getCurrentLanguage().getLocale().getLanguage()
        );
        int z = 0;
        for (JInternalFrame f : desktopPane.getAllFrames()) {
            String key = f.getClass().getSimpleName();
            Rectangle bounds;

            // Get normal bounds (the bounds to restore to when un-maximizing)
            if (f.isIcon() || f.isMaximum()) {
                bounds = f.getNormalBounds();
                if (bounds == null) {
                    // Fallback if getNormalBounds() returns null
                    bounds = new Rectangle(10, 10, 400, 300);
                }
            } else {
                bounds = f.getBounds();
            }

            p.setFrameState(key, new Profile.FrameState(
                    bounds, f.isIcon(), f.isMaximum(), f.isVisible(), z++
            ));
        }
        return p;
    }

    public void applyProfile(Profile profile) {
        this.currentProfile = profile;

        // Process in multiple phases to ensure correct state restoration
        SwingUtilities.invokeLater(() -> {
            // Phase 1: Reset all frames to normal state
            for (JInternalFrame f : desktopPane.getAllFrames()) {
                try {
                    if (f.isIcon()) f.setIcon(false);
                    if (f.isMaximum()) f.setMaximum(false);
                } catch (Exception ignored) {}
            }

            // Process intermediate changes
            desktopPane.validate();
            desktopPane.repaint();

            // Phase 2: Set bounds and visibility
            for (JInternalFrame f : desktopPane.getAllFrames()) {
                String key = f.getClass().getSimpleName();
                Profile.FrameState state = profile.getFrameState(key);

                if (state == null) {
                    f.setVisible(false);
                    continue;
                }

                // Set normal bounds first
                if (state.bounds != null) {
                    f.setBounds(state.bounds);
                }
                f.setVisible(state.isVisible);
            }

            // Process intermediate changes
            desktopPane.validate();
            desktopPane.repaint();

            // Add a small delay before applying special states
            Timer timer = new Timer(100, e -> {
                // Phase 3: Set z-order
                profile.getFrameStates().entrySet().stream()
                        .sorted(Comparator.comparingInt(entry -> entry.getValue().zOrder))
                        .forEach(entry -> {
                            for (JInternalFrame f : desktopPane.getAllFrames()) {
                                if (f.getClass().getSimpleName().equals(entry.getKey()) &&
                                        f.isVisible()) {
                                    desktopPane.moveToFront(f);
                                    break;
                                }
                            }
                        });

                // Phase 4: Apply special states (maximized/iconified)
                for (JInternalFrame f : desktopPane.getAllFrames()) {
                    try {
                        String key = f.getClass().getSimpleName();
                        Profile.FrameState state = profile.getFrameState(key);

                        if (state != null && state.isVisible) {
                            // Apply maximized state first
                            if (state.isMaximum) {
                                f.setMaximum(true);
                            }

                            // Then apply iconified state if needed
                            if (state.isIcon) {
                                f.setIcon(true);
                            }
                        }
                    } catch (Exception ignored) {}
                }

                desktopPane.validate();
                desktopPane.repaint();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    public void saveProfileOnExit() {
        ProfileManager mgr = new ProfileManager();
        String cur = currentProfile != null
                ? currentProfile.getProfileName()
                : null;

        String opt1 = LocalizationManager.getInstance().getString("profile.save.current");
        String opt2 = LocalizationManager.getInstance().getString("profile.save.new");
        String opt3 = LocalizationManager.getInstance().getString("profile.exit.without.saving");

        Object[] opts = cur != null
                ? new Object[]{String.format(opt1, cur), opt2, opt3}
                : new Object[]{opt2, opt3};

        int choice = JOptionPane.showOptionDialog(
                this,
                LocalizationManager.getInstance().getString("profile.save.prompt"),
                LocalizationManager.getInstance().getString("profile.save.title"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opts, opts[0]
        );

        if (cur != null && choice == 0) {
            mgr.saveProfile(createProfile(cur));
        } else if ((cur != null && choice == 1) || (cur == null && choice == 0)) {
            String name = JOptionPane.showInputDialog(
                    this,
                    LocalizationManager.getInstance().getString("profile.new.name"),
                    "profile1"
            );
            if (name != null && !name.trim().isEmpty()) {
                mgr.saveProfile(createProfile(name.trim()));
            }
        }
    }
}
