package gui.system;

import gui.MainApplicationFrame;
import gui.system.localization.Language;
import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import log.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ApplicationMenu extends JMenuBar implements LocaleChangeListener {
    private final MainApplicationFrame mainFrame;
    private JMenu lookAndFeelMenu;
    private JMenuItem systemLookAndFeelMenuItem;
    private JMenuItem crossPlatformLookAndFeelMenuItem;
    private JMenu testMenu;
    private JMenuItem logMessageMenuItem;
    private JMenu languageMenu;

    public ApplicationMenu(MainApplicationFrame mainFrame) {
        this.mainFrame = mainFrame;
        LocalizationManager.getInstance().addListener(this);
        buildMenu();
    }

    private void buildMenu() {
        add(createLookAndFeelMenu());
        add(createTestMenu());
        add(createLanguageMenu());
    }

    private JMenu createLookAndFeelMenu() {
        lookAndFeelMenu = new JMenu(LocalizationManager.getInstance().getString("menu.view"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance().getString("menu.view.desc")
        );

        systemLookAndFeelMenuItem = createSystemLookAndFeelMenuItem();
        crossPlatformLookAndFeelMenuItem = createCrossPlatformLookAndFeelMenuItem();

        lookAndFeelMenu.add(systemLookAndFeelMenuItem);
        lookAndFeelMenu.add(crossPlatformLookAndFeelMenuItem);
        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookAndFeelMenuItem() {
        JMenuItem systemLookAndFeel = new JMenuItem(LocalizationManager.getInstance().getString("menu.view.system"), KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            mainFrame.invalidate();
        });
        return systemLookAndFeel;
    }

    private JMenuItem createCrossPlatformLookAndFeelMenuItem() {
        JMenuItem crossplatformLookAndFeel = new JMenuItem(LocalizationManager.getInstance().getString("menu.view.cross-platform"), KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            mainFrame.invalidate();
        });
        return crossplatformLookAndFeel;
    }

    private JMenu createTestMenu() {
        testMenu = new JMenu(LocalizationManager.getInstance().getString("menu.test"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance().getString("menu.test.desc"));

        logMessageMenuItem = createLogMessageMenuItem();
        testMenu.add(logMessageMenuItem);
        return testMenu;
    }

    private JMenuItem createLogMessageMenuItem() {
        JMenuItem addLogMessageItem = new JMenuItem(LocalizationManager.getInstance().getString("log.test.message"), KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(LocalizationManager.getInstance().getString("log.test.message.text"));
        });
        return addLogMessageItem;
    }

    private JMenu createLanguageMenu() {
        languageMenu = new JMenu(LocalizationManager.getInstance().getString("menu.language"));
        languageMenu.setMnemonic(KeyEvent.VK_L);
        languageMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance().getString("menu.language.desc"));

        for (Language language : Language.values()) {
            JMenuItem item = new JMenuItem(language.getDisplayName());
            item.addActionListener(e -> {
                LocalizationManager.getInstance().setLanguage(language);
            });
            languageMenu.add(item);
        }

        return languageMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(mainFrame);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.error(LocalizationManager.getInstance().getString("theme.system.exception.while.loading") + e.getMessage());
        }
    }



    @Override
    public void localeChanged() {
        if (lookAndFeelMenu != null) {
            lookAndFeelMenu.setText(LocalizationManager.getInstance().getString("menu.view"));
            lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                    LocalizationManager.getInstance().getString("menu.view.desc"));

            if (systemLookAndFeelMenuItem != null) {
                systemLookAndFeelMenuItem.setText(LocalizationManager.getInstance().getString("menu.view.system"));
            }

            if (crossPlatformLookAndFeelMenuItem != null) {
                crossPlatformLookAndFeelMenuItem.setText(LocalizationManager.getInstance().getString("menu.view.cross-platform"));
            }
        }

        // Update Test menu
        if (testMenu != null) {
            testMenu.setText(LocalizationManager.getInstance().getString("menu.test"));
            testMenu.getAccessibleContext().setAccessibleDescription(
                    LocalizationManager.getInstance().getString("menu.test.desc"));

            if (logMessageMenuItem != null) {
                logMessageMenuItem.setText(LocalizationManager.getInstance().getString("log.test.message"));
            }
        }

        // Update Language menu
        if (languageMenu != null) {
            languageMenu.setText(LocalizationManager.getInstance().getString("menu.language"));
            languageMenu.getAccessibleContext().setAccessibleDescription(
                    LocalizationManager.getInstance().getString("menu.language.desc"));
        }
    }
}