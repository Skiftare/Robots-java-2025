package gui;

import gui.system.closing.CloseConfirmationDecorator;
import gui.ui.GameWindow;
import gui.ui.LogWindow;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import static java.lang.Math.round;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private int oldWidth = -1;
    private int oldHeight = -1;


    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();
        Dimension screenSize = new Dimension(screenBounds.width, screenBounds.height);
        setBounds(inset, inset,
                screenSize.width,
                screenSize.height);

        setContentPane(desktopPane);


        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(screenSize.width, screenSize.height);
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

    void resizeInternalFrames() {
        SwingUtilities.invokeLater(() -> {
            int width = desktopPane.getWidth();
            int height = desktopPane.getHeight();

            if (width == 0 || height == 0 || oldWidth <= 0 || oldHeight <= 0) {
                Logger.debug("Ошибка: размер desktopPane некорректен или ещё не инициализирован!");
                oldWidth = width;
                oldHeight = height;
                return;
            }

            Logger.debug("Изменяем размеры окон: " + width + "x" + height);

            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                double widthRatio = (double) frame.getWidth() / oldWidth;
                double heightRatio = (double) frame.getHeight() / oldHeight;
                double xRatio = (double) frame.getX() / oldWidth;
                double yRatio = (double) frame.getY() / oldHeight;

                int newWidth = (int) round(width * widthRatio);
                int newHeight = (int) round(height * heightRatio);
                int newX = (int) round(width * xRatio);
                int newY = (int) round(height * yRatio);

                // Ограничения, чтобы окна не выходили за границы
                newWidth = Math.min(newWidth, width - newX);
                newHeight = Math.min(newHeight, height - newY);

                frame.setBounds(newX, newY, newWidth, newHeight);
                frame.revalidate();
                frame.repaint();
            }

            // Сохраняем новые размеры
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
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        CloseConfirmationDecorator.addCloseConfirmation(frame);
        desktopPane.add(frame);
        frame.setVisible(true);
    }


    JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

        lookAndFeelMenu.add(createSystemLookAndFeelMenuItem());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelMenuItem());
        return lookAndFeelMenu;
    }

    JMenuItem createSystemLookAndFeelMenuItem() {
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
    }

    JMenuItem createCrossPlatformLookAndFeelMenuItem() {
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndFeel;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        testMenu.add(createLogMessageMenuItem());
        return testMenu;
    }

    JMenuItem createLogMessageMenuItem() {
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return addLogMessageItem;
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

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
}
