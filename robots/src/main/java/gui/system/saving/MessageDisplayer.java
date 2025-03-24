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

        // Create panel with centered text
        JPanel panel = new JPanel(new BorderLayout(0, 20)); // Add gap between components
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setPreferredSize(new Dimension(400, 100));
        panel.add(label, BorderLayout.CENTER);

        // Create OK button
        JButton okButton = new JButton(LocalizationManager.getInstance().getString("system.yes"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add padding
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Create dialog with custom content
        final JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setContentPane(panel);
        dialog.pack();

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Get dialog size
        Dimension dialogSize = dialog.getPreferredSize();

        // Calculate coordinates for centering
        int x = (int) ((screenSize.width - dialogSize.width) / 2);
        int y = (int) ((screenSize.height - dialogSize.height) / 2);

        // Set dialog position
        dialog.setLocation(x, y);

        // Create auto-close timer (12 seconds)
        Timer timer = new Timer(12000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false); // Only trigger once
        timer.start();

        // Set OK button action
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop(); // Stop the timer
                dialog.dispose(); // Close dialog
            }
        });

        // Show dialog
        dialog.setVisible(true);
    }
}