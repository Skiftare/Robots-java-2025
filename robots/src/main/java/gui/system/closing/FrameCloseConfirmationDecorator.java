package gui.system.closing;


import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Декоратор для добавления подтверждения закрытия к JInternalFrame
 */
public class FrameCloseConfirmationDecorator {
    /**
     * Добавляет диалог подтверждения закрытия к JInternalFrame
     *
     * @param frame    Окно для добавления подтверждения
     * @param strategy Стратегия для подтверждения
     */
    public static void addCloseConfirmation(JInternalFrame frame, FrameClosingStrategy strategy) {
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (strategy.confirmClosing(frame)) {
                    frame.dispose();
                }
            }
        });
    }
    public static void addCloseConfirmation(JFrame frame, FrameClosingStrategy strategy) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (strategy.confirmClosing(frame)) {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Добавляет стандартный диалог подтверждения закрытия
     *
     * @param frame Окно для добавления подтверждения
     */
    public static void addCloseConfirmation(JInternalFrame frame) {
        addCloseConfirmation(frame, new DefaultFrameClosingStrategy());
    }
    public static void addCloseConfirmation(JFrame frame, String message, String title) {
        addCloseConfirmation(frame, new DefaultFrameClosingStrategy(message, title));
    }
}