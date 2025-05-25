package gui.system;

import gui.MainApplicationFrame;
import gui.system.localization.Language;
import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import gui.system.saving.SaveLoadDialog;
import gui.ui.GameWindow;
import gui.ui.ModManagementFrame;
import gui.ui.drawing.GameVisualizer;
import log.WindowLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

public class ApplicationMenu extends JMenuBar implements LocaleChangeListener {
    private final MainApplicationFrame mainFrame;
    private JMenu lookAndFeelMenu;
    private JMenuItem systemLookAndFeelMenuItem;
    private JMenuItem crossPlatformLookAndFeelMenuItem;
    private JMenu testMenu;
    private JMenuItem logMessageMenuItem;
    private JMenu languageMenu;
    private JMenu fileMenu;
    private JMenuItem exitMenuItem;
    private JMenu saveLoadMenu;
    private JMenuItem saveMenuItem;
    private JMenuItem loadMenuItem;

    // ⇒ Новое выпадающее меню «Exit to Menu»
    private JMenu exitToMenu;
    private JMenuItem exitToMenuAction;

    public ApplicationMenu(MainApplicationFrame mainFrame) {
        super();
        // чтобы пункты меню не растягивались по всей ширине
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.mainFrame = mainFrame;
        LocalizationManager.getInstance().addListener(this);

        buildMenu();
    }

    private void buildMenu() {
        add(createLookAndFeelMenu());
        add(createTestMenu());
        add(createLanguageMenu());
        add(createFileMenu());
        add(createSaveLoadMenu());

        // вместо одного пункта — полноценное выпадающее меню
        add(createExitToMenu());

        add(createModsMenu());
    }

    private JMenu createExitToMenu() {
        exitToMenu = new JMenu(
                LocalizationManager.getInstance().getString("menu.exitToMenu")
        );
        exitToMenu.setMnemonic(KeyEvent.VK_M);

        exitToMenuAction = new JMenuItem(
                LocalizationManager.getInstance().getString("menu.exitToMenu.action"),
                KeyEvent.VK_G
        );
        exitToMenuAction.addActionListener(e -> {
            // закрываем текущее игровое окно
            GameWindow gw = mainFrame.getGameWindow();
            if (gw != null && !gw.isClosed()) {
                try {
                    gw.setClosed(true);
                } catch (PropertyVetoException ex) {
                    ex.printStackTrace();
                }
            }
            // открываем меню выбора уровня
            mainFrame.showLevelSelectionMenu(mainFrame.getProfile());
        });

        exitToMenu.add(exitToMenuAction);
        return exitToMenu;
    }

    private JMenu createSaveLoadMenu() {
        saveLoadMenu = new JMenu(
                LocalizationManager.getInstance().getString("menu.saveLoad")
        );
        saveLoadMenu.setMnemonic(KeyEvent.VK_S);

        saveMenuItem = createSaveMenuItem();
        loadMenuItem = createLoadMenuItem();

        saveLoadMenu.add(saveMenuItem);
        saveLoadMenu.add(loadMenuItem);
        return saveLoadMenu;
    }

    private JMenuItem createSaveMenuItem() {
        JMenuItem saveItem = new JMenuItem(
                LocalizationManager.getInstance().getString("menu.save"),
                KeyEvent.VK_S
        );
        saveItem.addActionListener(event ->
                SaveLoadDialog.showSaveDialog(mainFrame, mainFrame.getGameWindow().getGameVisualizer())
        );
        return saveItem;
    }

    private JMenuItem createLoadMenuItem() {
        JMenuItem loadItem = new JMenuItem(
                LocalizationManager.getInstance().getString("menu.load"),
                KeyEvent.VK_L
        );
        loadItem.addActionListener(event ->
                SaveLoadDialog.showLoadDialog(mainFrame, mainFrame.getGameWindow().getGameVisualizer())
        );
        return loadItem;
    }

    private JMenu createLookAndFeelMenu() {
        lookAndFeelMenu = new JMenu(
                LocalizationManager.getInstance().getString("menu.view")
        );
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
        JMenuItem systemLF = new JMenuItem(
                LocalizationManager.getInstance().getString("menu.view.system"),
                KeyEvent.VK_S
        );
        systemLF.addActionListener(e -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            mainFrame.invalidate();
        });
        return systemLF;
    }

    private JMenuItem createCrossPlatformLookAndFeelMenuItem() {
        JMenuItem crossLF = new JMenuItem(
                LocalizationManager.getInstance().getString("menu.view.cross-platform"),
                KeyEvent.VK_C
        );
        crossLF.addActionListener(e -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            mainFrame.invalidate();
        });
        return crossLF;
    }

    private JMenu createTestMenu() {
        testMenu = new JMenu(
                LocalizationManager.getInstance().getString("menu.test")
        );
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance().getString("menu.test.desc")
        );

        logMessageMenuItem = createLogMessageMenuItem();
        testMenu.add(logMessageMenuItem);
        return testMenu;
    }

    private JMenuItem createLogMessageMenuItem() {
        JMenuItem item = new JMenuItem(
                LocalizationManager.getInstance().getString("log.test.message"),
                KeyEvent.VK_L
        );
        item.addActionListener(e ->
                WindowLogger.debug(LocalizationManager.getInstance().getString("log.test.message.text"))
        );
        return item;
    }

    private JMenu createLanguageMenu() {
        languageMenu = new JMenu(
                LocalizationManager.getInstance().getString("menu.language")
        );
        languageMenu.setMnemonic(KeyEvent.VK_L);
        languageMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance().getString("menu.language.desc")
        );

        for (Language lang : Language.values()) {
            JMenuItem itm = new JMenuItem(lang.getDisplayName());
            itm.addActionListener(e -> LocalizationManager.getInstance().setLanguage(lang));
            languageMenu.add(itm);
        }
        return languageMenu;
    }

    private JMenu createFileMenu() {
        fileMenu = new JMenu(
                LocalizationManager.getInstance().getString("menu.file")
        );
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance().getString("menu.file.desc")
        );

        exitMenuItem = createExitMenuItem();
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem exitItem = new JMenuItem(
                LocalizationManager.getInstance().getString("menu.app.exit"),
                KeyEvent.VK_X
        );
        exitItem.addActionListener(e ->
                mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING))
        );
        return exitItem;
    }

    private JMenu createModsMenu() {
        JMenu modsMenu = new JMenu(
                LocalizationManager.getInstance().getString("menu.mods")
        );
        JMenuItem manageModsItem = new JMenuItem(
                LocalizationManager.getInstance().getString("menu.mods.manage")
        );
        manageModsItem.addActionListener(e -> {
            GameWindow gameWindow = mainFrame.getGameWindow();
            if (gameWindow != null) {
                GameVisualizer viz = gameWindow.getGameVisualizer();
                ModManagementFrame modFrame = new ModManagementFrame(viz.getModManager());
                mainFrame.addWindow(modFrame);
            } else {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        LocalizationManager.getInstance().getString("mods.no.game.open"),
                        LocalizationManager.getInstance().getString("mods.error"),
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        modsMenu.add(manageModsItem);
        return modsMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(mainFrame);
        } catch (Exception e) {
            WindowLogger.error(
                    LocalizationManager.getInstance().getString("theme.system.exception.while.loading")
                            + e.getMessage()
            );
        }
    }

    @Override
    public void localeChanged() {
        lookAndFeelMenu.setText(LocalizationManager.getInstance().getString("menu.view"));
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance().getString("menu.view.desc")
        );
        systemLookAndFeelMenuItem.setText(
                LocalizationManager.getInstance().getString("menu.view.system")
        );
        crossPlatformLookAndFeelMenuItem.setText(
                LocalizationManager.getInstance().getString("menu.view.cross-platform")
        );

        testMenu.setText(LocalizationManager.getInstance().getString("menu.test"));
        logMessageMenuItem.setText(
                LocalizationManager.getInstance().getString("log.test.message")
        );

        languageMenu.setText(LocalizationManager.getInstance().getString("menu.language"));
        fileMenu.setText(LocalizationManager.getInstance().getString("menu.file"));
        exitMenuItem.setText(
                LocalizationManager.getInstance().getString("menu.app.exit")
        );

        saveLoadMenu.setText(
                LocalizationManager.getInstance().getString("menu.saveLoad")
        );
        saveMenuItem.setText(
                LocalizationManager.getInstance().getString("menu.save")
        );
        loadMenuItem.setText(
                LocalizationManager.getInstance().getString("menu.load")
        );

        exitToMenu.setText(
                LocalizationManager.getInstance().getString("menu.exitToMenu")
        );
        exitToMenuAction.setText(
                LocalizationManager.getInstance().getString("menu.exitToMenu.action")
        );
    }
}
