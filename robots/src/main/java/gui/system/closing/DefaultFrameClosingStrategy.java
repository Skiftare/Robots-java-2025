package gui.system.closing;

import gui.system.localization.LocalizationManager;
import lombok.Getter;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

@Getter
public class DefaultFrameClosingStrategy implements FrameClosingStrategy, LocaleChangeListener {
    private String message;
    private String title;

    public DefaultFrameClosingStrategy() {
        LocalizationManager.getInstance().addListener(this);
        updateStrings();
    }

    public DefaultFrameClosingStrategy(String messageKey, String titleKey) {
        LocalizationManager.getInstance().addListener(this);
        this.message = LocalizationManager.getInstance().getString(messageKey);
        this.title = LocalizationManager.getInstance().getString(titleKey);
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

    @Override
    public void localeChanged() {
        updateStrings();
    }

    private void updateStrings() {
        this.message = LocalizationManager.getInstance().getString("close.confirm");
        this.title = LocalizationManager.getInstance().getString("close.confirm.title");
    }
}