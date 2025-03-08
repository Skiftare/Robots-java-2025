package gui.system.processing;

import model.ui.Grid;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

public class PlayerController {
    private final GameManager gameManager;
    private final JComponent component;

    public PlayerController(GameManager gameManager, JComponent component, Grid grid) {
        this.gameManager = gameManager;
        this.component = component;

        // Keyboard controls for movement
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        // Mouse controls for selecting which entity to control
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int gridX = e.getX() / grid.getCellSize();
                int gridY = e.getY() / grid.getCellSize();

                if (gridX >= 0 && gridX < grid.getWidth() &&
                        gridY >= 0 && gridY < grid.getHeight()) {
                    gameManager.selectPlayer(gridX, gridY);
                }

                // Ensure focus for keyboard controls
                component.requestFocusInWindow();
            }
        });

        component.setFocusable(true);
        component.requestFocusInWindow();
    }

    private void handleKeyPress(int keyCode) {
        int dx = 0, dy = 0;

        switch(keyCode) {
            // Arrow keys
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: dy = -1; break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S: dy = 1; break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A: dx = -1; break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: dx = 1; break;

            default: return; // Not a movement key
        }

        gameManager.movePlayer(dx, dy);
        component.repaint();
    }

    public void requestFocus() {
        component.requestFocusInWindow();
    }
}