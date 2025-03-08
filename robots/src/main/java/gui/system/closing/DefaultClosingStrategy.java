package gui.system.closing;


import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

/**
 * Стандартная стратегия с диалоговым окном подтверждения
 */
public class DefaultClosingStrategy implements ClosingStrategy {
    private final String message;
    private final String title;

    public DefaultClosingStrategy(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public DefaultClosingStrategy() {
        this("Вы уверены, что хотите закрыть это окно?", "Подтверждение закрытия");
    }

    @Override
    public boolean confirmClosing(JInternalFrame frame) {
        int result = JOptionPane.showConfirmDialog(
                frame,
                message,
                title,
                JOptionPane.YES_NO_OPTION
        );
        return result == JOptionPane.YES_OPTION;
    }
}