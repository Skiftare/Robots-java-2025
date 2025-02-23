package gui;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;

import log.Logger;

import static java.lang.Math.min;
import static java.lang.Math.round;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final int inset = 50;

    public MainApplicationFrame() {

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();
        Dimension screenSize = new Dimension(screenBounds.width, screenBounds.height);

        Logger.debug("Размер экрана: " + screenSize.width + "x" + screenSize.height);
        setBounds(inset, inset,
                screenSize.width,
                screenSize.height);

        setContentPane(desktopPane);


        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(screenSize.width - inset, screenSize.height - inset);
        addWindow(gameWindow);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeInternalFrames();
            }
        });

    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private void resizeInternalFrames() {
        SwingUtilities.invokeLater(() -> {
            int width = desktopPane.getWidth();
            int height = desktopPane.getHeight();

            if (width == 0 || height == 0) {
                Logger.debug("Ошибка: размер desktopPane некорректен!");
                return;
            }

            Logger.debug("Изменяем размеры окон: " + width + "x" + height);

            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                double widthRatio = (double) frame.getWidth() / desktopPane.getWidth();
                double heightRatio = (double) frame.getHeight() / desktopPane.getHeight();
                double xRatio = (double) frame.getX() / desktopPane.getWidth();
                double yRatio = (double) frame.getY() / desktopPane.getHeight();
                int newWidth = (int) round(width / widthRatio);
                int newHeight = (int) round(height / heightRatio);
                int newX = (int) round(width * xRatio);
                int newY = (int) round(height * yRatio);

                if (newHeight > desktopPane.getHeight() - newY) {
                    newHeight = desktopPane.getHeight() - newY;
                }
                if (newWidth > desktopPane.getWidth() - newX) {
                    newWidth = desktopPane.getWidth() - newX;
                }

                frame.setBounds(newX, newY, newWidth, newHeight);
                frame.revalidate();
                frame.repaint();
            }
            desktopPane.revalidate();
            desktopPane.repaint();
        });
    }


    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.error("Ошибка при установке схемы оформления: " + e.getMessage());
        }
    }
}
