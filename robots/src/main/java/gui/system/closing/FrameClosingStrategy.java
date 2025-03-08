package gui.system.closing;

import javax.swing.*;

/**
 * Интерфейс для стратегии подтверждения закрытия окон
 */
public interface FrameClosingStrategy {
    /**
     * Запрашивает подтверждение закрытия окна
     * @param frame Окно, которое закрывается
     * @return true если окно нужно закрыть, false в противном случае
     */
    boolean confirmClosing(JInternalFrame frame);
    boolean confirmClosing(JFrame frame);
}