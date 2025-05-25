package gui.ui;

import game.mods.IMod;
import game.mods.ModManager;
import gui.system.localization.LocalizationManager;
import gui.ui.drawing.GameVisualizer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;

public class ModManagementFrame extends JInternalFrame {
    private final ModManager modManager;
    private final JList<String> modList;
    private final DefaultListModel<String> modListModel;

    public ModManagementFrame(ModManager modManager) {
        super(LocalizationManager.getInstance().getString("mods.window.title"),
                true, true, true, true);

        this.modManager = modManager;

        setSize(400, 300);
        setLocation(50, 50);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create mod list
        modListModel = new DefaultListModel<>();
        updateModList();

        modList = new JList<>(modListModel);
        JScrollPane scrollPane = new JScrollPane(modList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel();
        JButton loadButton = new JButton(LocalizationManager.getInstance().getString("mods.load"));
        JButton unloadButton = new JButton(LocalizationManager.getInstance().getString("mods.unload"));

        loadButton.addActionListener(e -> loadMod());
        unloadButton.addActionListener(e -> unloadSelectedMod());

        buttonsPanel.add(loadButton);
        buttonsPanel.add(unloadButton);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void updateModList() {
        modListModel.clear();
        for (IMod mod : modManager.getLoadedMods()) {
            modListModel.addElement(mod.getName() + " v" + mod.getVersion() + " by " + mod.getAuthor());
        }
    }

    private void loadMod() {
        // Get the parent frame
        Frame parentFrame = JOptionPane.getFrameForComponent(this);

        // Create a custom dialog with path input option
        JDialog pathDialog = new JDialog(parentFrame,
                LocalizationManager.getInstance().getString("mods.load"), true);
        pathDialog.setLayout(new BorderLayout());
        pathDialog.setSize(500, 150);
        pathDialog.setLocationRelativeTo(this);

        // Create path input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Start in the current working directory
        JTextField pathField = new JTextField(System.getProperty("user.dir"));
        JButton browseButton = new JButton(LocalizationManager.getInstance().getString("browse"));

        inputPanel.add(new JLabel(LocalizationManager.getInstance().getString("mods.path")), BorderLayout.NORTH);
        inputPanel.add(pathField, BorderLayout.CENTER);
        inputPanel.add(browseButton, BorderLayout.EAST);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loadButton = new JButton(LocalizationManager.getInstance().getString("mods.load"));
        JButton cancelButton = new JButton(LocalizationManager.getInstance().getString("cancel"));
        buttonPanel.add(loadButton);
        buttonPanel.add(cancelButton);

        pathDialog.add(inputPanel, BorderLayout.CENTER);
        pathDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Browse button action
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(pathField.getText());
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
                }

                public String getDescription() {
                    return "JAR Files (*.jar)";
                }
            });

            if (fileChooser.showOpenDialog(pathDialog) == JFileChooser.APPROVE_OPTION) {
                pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> pathDialog.dispose());

        // Load button action
        loadButton.addActionListener(e -> {
            String filePath = pathField.getText().trim();
            File file = new File(filePath);

            if (!file.exists()) {
                JOptionPane.showMessageDialog(pathDialog,
                        LocalizationManager.getInstance().getString("mods.file.not.found"),
                        LocalizationManager.getInstance().getString("error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!file.getName().toLowerCase().endsWith(".jar")) {
                JOptionPane.showMessageDialog(pathDialog,
                        LocalizationManager.getInstance().getString("mods.file.not.jar"),
                        LocalizationManager.getInstance().getString("error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            modManager.loadMod(file.toPath());
            updateModList();
            pathDialog.dispose();
        });

        // Allow Enter key to submit
        pathField.addActionListener(e -> loadButton.doClick());

        pathDialog.setVisible(true);
    }


    private void unloadSelectedMod() {
        int selectedIndex = modList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String modName = modManager.getLoadedMods().get(selectedIndex).getName();
            modManager.unloadMod(modName);
            updateModList();
        }
    }
}