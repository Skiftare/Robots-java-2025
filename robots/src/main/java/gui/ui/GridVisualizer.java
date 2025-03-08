package gui.ui;

import gui.system.processing.GameManager;
import lombok.Getter;
import lombok.Setter;
import model.object.abstractions.Entity;
import model.ui.Cell;
import model.ui.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

public class GridVisualizer extends JPanel {
    @Getter
    private Grid grid;
    @Setter
    private GameManager gameManager;

    public GridVisualizer(int gridWidth, int gridHeight, int cellSize) {
        grid = new Grid(gridWidth, gridHeight, cellSize);

        // Enable keyboard focus
        setFocusable(true);
        requestFocusInWindow();

        // Mouse click listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int gridX = e.getX() / grid.getCellSize();
                int gridY = e.getY() / grid.getCellSize();

                if (gridX >= 0 && gridX < grid.getWidth() &&
                        gridY >= 0 && gridY < grid.getHeight()) {
                    handleCellClick(gridX, gridY);
                }

                // Request focus to ensure keyboard events are captured
                requestFocusInWindow();
            }
        });

        // Add keyboard listener for arrow keys
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
            }
        });

        setPreferredSize(new Dimension(
                grid.getWidth() * grid.getCellSize(),
                grid.getHeight() * grid.getCellSize()));
    }

    private void movePlayer(int keyCode) {
        if (gameManager == null) return;

        int dx = 0, dy = 0;

        switch(keyCode) {
            case KeyEvent.VK_UP: dy = -1; break;
            case KeyEvent.VK_DOWN: dy = 1; break;
            case KeyEvent.VK_LEFT: dx = -1; break;
            case KeyEvent.VK_RIGHT: dx = 1; break;

            case KeyEvent.VK_W: dy = -1; break;
            case KeyEvent.VK_S: dy = 1; break;
            case KeyEvent.VK_A: dx = 1; break;
            case KeyEvent.VK_D: dx = -1; break;



            default: return; // Not an arrow key
        }
        Logger.getAnonymousLogger().info("Moving player: " + dx + ", " + dy);

        gameManager.movePlayer(dx, dy);
        repaint();
    }

    private void handleCellClick(int x, int y) {
        // Handle user interaction with the grid
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw grid
        g2d.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= grid.getWidth(); x++) {
            g2d.drawLine(
                    x * grid.getCellSize(), 0,
                    x * grid.getCellSize(), grid.getHeight() * grid.getCellSize()
            );
        }

        for (int y = 0; y <= grid.getHeight(); y++) {
            g2d.drawLine(
                    0, y * grid.getCellSize(),
                    grid.getWidth() * grid.getCellSize(), y * grid.getCellSize()
            );
        }

        // Draw entities
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                if (cell != null) {
                    for (Entity entity : cell.getEntities()) {
                        if (entity != null) {
                            drawEntity(g2d, entity, x, y);
                        }
                    }
                }
            }
        }
    }

    private void drawEntity(Graphics2D g2d, Entity entity, int x, int y) {
        if (entity == null) return;

        int cellSize = grid.getCellSize();
        int padding = 4;

        // Draw entity background
        g2d.setColor(entity.getColor());
        g2d.fillRect(
                x * cellSize + padding,
                y * cellSize + padding,
                cellSize - padding * 2,
                cellSize - padding * 2
        );

        // Draw border, with highlight for active player entity
        if (gameManager != null && entity.equals(gameManager.getActivePlayerEntity())) {
            // Highlighted border for active entity
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
        }

        g2d.drawRect(
                x * cellSize + padding,
                y * cellSize + padding,
                cellSize - padding * 2,
                cellSize - padding * 2
        );
        g2d.setStroke(new BasicStroke(1)); // Reset stroke

        // Draw entity name
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String text = entity.getName();
        int textWidth = fm.stringWidth(text);

        g2d.setColor(Color.BLACK);
        g2d.drawString(
                text,
                x * cellSize + (cellSize - textWidth) / 2,
                y * cellSize + cellSize / 2 + fm.getAscent() / 2
        );
    }


}