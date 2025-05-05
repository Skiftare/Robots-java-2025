package gui.system.closing;

import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class DefaultFrameClosingStrategy implements FrameClosingStrategy, LocaleChangeListener {
    private String message;
    private String title;
    private String yesButtonText;
    private String noButtonText;

    public DefaultFrameClosingStrategy() {
        LocalizationManager.getInstance().addListener(this);
        updateStrings();
    }

    public DefaultFrameClosingStrategy(String incomeMessage, String incomeTitle) {
        LocalizationManager.getInstance().addListener(this);
        this.message = incomeMessage;
        this.title = incomeTitle;
        this.yesButtonText = LocalizationManager.getInstance().getString("system.yes");
        this.noButtonText = LocalizationManager.getInstance().getString("system.no");

    }

    private void fetchLocalizationButtons() {
        this.yesButtonText = LocalizationManager.getInstance().getString("system.yes");
        this.noButtonText = LocalizationManager.getInstance().getString("system.no");

    }

    @Override
    public boolean confirmClosing(JInternalFrame frame) {
        return confirmClosingForAnyComponent(frame);
    }

    @Override
    public boolean confirmClosing(JFrame frame) {
        return confirmClosingForAnyComponent(frame);
    }

    private boolean confirmClosingForAnyComponent(Component frame) {
        if (yesButtonText == null || noButtonText == null) {
            fetchLocalizationButtons();
        }
        String yes = (yesButtonText != null) ? yesButtonText : "Yes";
        String no = (noButtonText != null) ? noButtonText : "No";

        Object[] options = {yes, no};

        int result = JOptionPane.showOptionDialog(
                frame,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        return result == 0;
    }


    @Override
    public void localeChanged() {
        updateStrings();
    }

    private void updateStrings() {
        this.message = LocalizationManager.getInstance().getString("close.confirm");
        this.title = LocalizationManager.getInstance().getString("close.confirm.title");
        this.yesButtonText = LocalizationManager.getInstance().getString("system.yes");
        this.noButtonText = LocalizationManager.getInstance().getString("system.no");
    }
}