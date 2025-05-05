package gui.system.saving;

import gui.system.localization.LocalizationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDisplayer {

    public static void showCenteredMessage(JFrame parentFrame, String messageKey) {
        // Get localized text for message and title
        String message = LocalizationManager.getInstance().getString(messageKey);
        String title = LocalizationManager.getInstance().getString("save.message.info");

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setPreferredSize(new Dimension(400, 100));
        panel.add(label, BorderLayout.CENTER);

        JButton okButton = new JButton(LocalizationManager.getInstance().getString("system.yes"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        final JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setContentPane(panel);
        dialog.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Dimension dialogSize = dialog.getPreferredSize();

        int x = (int) ((screenSize.width - dialogSize.width) / 2);
        int y = (int) ((screenSize.height - dialogSize.height) / 2);

        dialog.setLocation(x, y);

        Timer timer = new Timer(12000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }
}