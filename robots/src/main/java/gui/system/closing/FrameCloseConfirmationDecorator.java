package gui.system.closing;


import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

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

    /**
     * Добавляет стандартный диалог подтверждения закрытия
     *
     * @param frame Окно для добавления подтверждения
     */
    public static void addCloseConfirmation(JInternalFrame frame) {
        addCloseConfirmation(frame, new DefaultFrameClosingStrategy());
    }
}