package game.mods.extensions_points;

import javax.swing.*;
import java.awt.event.KeyEvent;

public interface IControlAdapter {
    void setupAdditionalControls(JComponent component);
    boolean interceptKeyEvent(KeyEvent e); // Return true if handled
}