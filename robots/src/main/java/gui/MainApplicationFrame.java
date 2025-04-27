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
import log.WindowLogger;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
                () -> { saveProfileOnExit(); System.exit(0); }
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
            if (w<=0 || h<=0 || oldWidth<=0 || oldHeight<=0) {
                oldWidth = w; oldHeight = h;
                return;
            }
            for (JInternalFrame f : desktopPane.getAllFrames()) {
                double wr = (double)f.getWidth()/oldWidth;
                double hr = (double)f.getHeight()/oldHeight;
                double xr = (double)f.getX()/oldWidth;
                double yr = (double)f.getY()/oldHeight;
                int nw = (int)round(w*wr), nh = (int)round(h*hr);
                int nx = (int)round(w*xr), ny = (int)round(h*yr);
                nw = Math.min(nw, w-nx);
                nh = Math.min(nh, h-ny);
                f.setBounds(nx, ny, nw, nh);
                f.revalidate(); f.repaint();
            }
            oldWidth = w; oldHeight = h;
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
            Rectangle norm;
            try {
                if (f.isIcon() || f.isMaximum()) {
                    norm = f.getNormalBounds();
                    if (norm==null) norm = f.getBounds();
                } else {
                    norm = f.getBounds();
                }
            } catch (Exception e) {
                norm = f.getBounds();
            }
            boolean icon = false, max = false;
            try { icon = f.isIcon(); max = f.isMaximum(); } catch (Exception ignored) {}
            p.setFrameState(key, new Profile.FrameState(
                    norm, icon, max, f.isVisible(), z++
            ));
        }
        return p;
    }

    /**
     * Восстанавливаем профиль:
     * – сбрасываем icon/max;
     * – всегда восстанавливаем bounds (нужно для правильной памяти нормальных размеров);
     * – если wasIcon – только iconify (Swing на де-иконификацию сам вернёт max- или оконный режим);
     * – иначе, если wasMax – делаем maximize;
     * – иначе – просто показываем/скрываем.
     */
    public void applyProfile(Profile profile) {
        this.currentProfile = profile;

        for (JInternalFrame f : desktopPane.getAllFrames()) {
            String key = f.getClass().getSimpleName();
            Profile.FrameState st = profile.getFrameState(key);

            if (st==null) {
                f.setVisible(false);
                continue;
            }

            // 1) сброс любых старых флагов
            try { f.setIcon(false); } catch (Exception ignored) {}
            try { f.setMaximum(false); } catch (Exception ignored) {}

            // 2) восстанавливаем bounds
            f.setBounds(st.bounds);

            if (st.isIcon) {
                // 3) если иконом — только иконить
                f.setVisible(true);
                try { f.setIcon(true); } catch (Exception ignored) {}

            } else {
                // 4) иначе окно должно быть видно или скрыто
                f.setVisible(st.isVisible);
                if (st.isMaximum) {
                    // 5) и, если нужно, максимизировать
                    try { f.setMaximum(true); } catch (Exception ignored) {}
                }
            }
        }

        // порядок Z-слоёв
        profile.getFrameStates().entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue().zOrder, a.getValue().zOrder))
                .forEach(entry -> {
                    for (JInternalFrame f : desktopPane.getAllFrames()) {
                        if (f.getClass().getSimpleName().equals(entry.getKey())
                                && f.isVisible())
                        {
                            desktopPane.moveToFront(f);
                            break;
                        }
                    }
                });

        desktopPane.revalidate();
        desktopPane.repaint();
    }

    /**
     * При выходе — спрашиваем, сохранять ли профиль.
     */
    public void saveProfileOnExit() {
        ProfileManager mgr = new ProfileManager();
        String cur = currentProfile != null
                ? currentProfile.getProfileName()
                : null;

        String opt1 = LocalizationManager.getInstance().getString("profile.save.current");
        String opt2 = LocalizationManager.getInstance().getString("profile.save.new");
        String opt3 = LocalizationManager.getInstance().getString("profile.exit.without.saving");

        Object[] opts = cur != null
                ? new Object[]{ String.format(opt1, cur), opt2, opt3 }
                : new Object[]{ opt2, opt3 };

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
        } else if ((cur != null && choice == 1) || (cur==null && choice==0)) {
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
