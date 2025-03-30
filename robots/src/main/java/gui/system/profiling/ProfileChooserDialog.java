package gui.system.profiling;

import javax.swing.*;
import gui.system.localization.LocalizationManager;
import java.awt.*;
import java.util.List;

public class ProfileChooserDialog extends JDialog {
    private Profile selectedProfile = null;

    public ProfileChooserDialog(Frame owner, List<Profile> profiles, String message, String title) {
        super(owner, title, true);
        setLayout(new BorderLayout());

        JLabel label = new JLabel(message);
        add(label, BorderLayout.NORTH);

        DefaultListModel<Profile> listModel = new DefaultListModel<>();
        for (Profile p : profiles) {
            listModel.addElement(p);
        }
        JList<Profile> profileList = new JList<>(listModel);
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Profile profile = (Profile) value;
                return super.getListCellRendererComponent(list, profile.getProfileName(), index, isSelected, cellHasFocus);
            }
        });
        add(new JScrollPane(profileList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnYes = new JButton(LocalizationManager.getInstance().getString("system.yes"));
        JButton btnNo = new JButton(LocalizationManager.getInstance().getString("system.no"));
        btnYes.addActionListener(e -> {
            selectedProfile = profileList.getSelectedValue();
            setVisible(false);
        });
        btnNo.addActionListener(e -> {
            selectedProfile = null;
            setVisible(false);
        });
        buttonPanel.add(btnYes);
        buttonPanel.add(btnNo);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }
}
