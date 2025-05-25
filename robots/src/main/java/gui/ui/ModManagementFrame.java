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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
            }

            public String getDescription() {
                return "JAR Files (*.jar)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Path jarPath = fileChooser.getSelectedFile().toPath();
            modManager.loadMod(jarPath);
            updateModList();
        }
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