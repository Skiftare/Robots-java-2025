package gui.system.processing;

import model.Position;
import model.object.GameObject;
import model.object.NounWord;
import model.object.OperatorWord;
import model.object.PropertyWord;
import model.object.abstractions.Entity;
import model.object.abstractions.Word;
import model.ui.Cell;
import model.ui.Grid;

import java.awt.Color;
import java.util.*;

public class GameManager {
    private Grid grid;
    private Map<String, Entity> wordTypes = new HashMap<>();
    private List<Formula> activeFormulas = new ArrayList<>();
    private Position activePlayerPosition = null;
    private GameObject activePlayerObject = null;
    private FormulaEvaluator formulaEvaluator;

    public GameManager(Grid grid) {
        this.grid = grid;
        initializeWordTypes();
    }

    private void initializeWordTypes() {
        // Initialize word types (formula components)
        wordTypes.put("STUDENT", new NounWord("STUDENT", Color.BLUE));
        wordTypes.put("PERS", new NounWord("PERS", Color.PINK));
        wordTypes.put("40", new NounWord("40", Color.RED));
        wordTypes.put("60", new NounWord("60", Color.GREEN));
        wordTypes.put("80", new NounWord("80", Color.YELLOW));
        wordTypes.put("WIN", new PropertyWord("WIN", Color.YELLOW));
        wordTypes.put("YOU", new PropertyWord("YOU", Color.CYAN));
        wordTypes.put("IS", new OperatorWord("IS", Color.WHITE));
        wordTypes.put("NORM", new PropertyWord("NORM", Color.ORANGE));
        wordTypes.put("GOOD", new PropertyWord("GOOD", Color.GREEN.darker()));
    }

    public void setupLevel() {
        // Place formula words
        grid.getCell(7, 3).addEntity(createWord("STUDENT"));
        grid.getCell(7, 4).addEntity(createWord("IS"));
        grid.getCell(7, 5).addEntity(createWord("YOU"));

        grid.getCell(10, 8).addEntity(createWord("IS"));
        grid.getCell(10, 6).addEntity(createWord("NORM"));
        grid.getCell(10, 10).addEntity(createWord("40"));

        // Place actual game objects
        grid.getCell(3, 3).addEntity(createGameObject("PERS"));
        grid.getCell(12, 12).addEntity(createGameObject("60"));
        grid.getCell(8, 8).addEntity(createGameObject("80"));

        // Evaluate rules after setup
        evaluateFormulas();
        findInitialPlayerObject();
    }

    private Word createWord(String type) {
        Entity template = wordTypes.get(type);
        if (template instanceof NounWord) {
            return new NounWord(template.getName(), template.getColor());
        } else if (template instanceof PropertyWord) {
            return new PropertyWord(template.getName(), template.getColor());
        } else if (template instanceof OperatorWord) {
            return new OperatorWord(template.getName(), template.getColor());
        }
        return null;
    }

    private GameObject createGameObject(String type) {
        Entity template = wordTypes.get(type);
        if (template != null) {
            return new GameObject(type, template.getColor());
        }
        return null;
    }

    private void findInitialPlayerObject() {
        activePlayerPosition = null;
        activePlayerObject = null;

        // Find the first object with "YOU" property
        for (int x = 0; x < grid.getWidth() && activePlayerPosition == null; x++) {
            for (int y = 0; y < grid.getHeight() && activePlayerPosition == null; y++) {
                Cell cell = grid.getCell(x, y);
                for (Entity entity : cell.getEntities()) {
                    if (entity instanceof GameObject gameObject && hasProperty(gameObject, "YOU")) {
                        activePlayerPosition = new Position(x, y);
                        activePlayerObject = gameObject;
                        return;
                    }
                }
            }
        }
    }

    public void movePlayer(int dx, int dy) {
        if (activePlayerPosition == null || activePlayerObject == null) {
            findInitialPlayerObject();
            if (activePlayerPosition == null) return;
        }

        // Calculate new position
        Position newPos = new Position(
                activePlayerPosition.x() + dx,
                activePlayerPosition.y() + dy
        );

        // Check if new position is within bounds
        if (newPos.x() >= 0 && newPos.x() < grid.getWidth() &&
                newPos.y() >= 0 && newPos.y() < grid.getHeight()) {

            Cell sourceCell = grid.getCell(activePlayerPosition.x(), activePlayerPosition.y());
            Cell targetCell = grid.getCell(newPos.x(), newPos.y());

            // Move only the active player object
            if (sourceCell.getEntities().contains(activePlayerObject)) {
                sourceCell.removeEntity(activePlayerObject);
                targetCell.addEntity(activePlayerObject);
                activePlayerPosition = newPos;
            }
        }

        // After moving, re-evaluate formulas
        evaluateFormulas();

        // Check if active object still has "YOU" property
        if (activePlayerObject != null && !hasProperty(activePlayerObject, "YOU")) {
            findInitialPlayerObject();
        }
    }

    private boolean hasProperty(GameObject gameObject, String property) {
        return gameObject.hasProperty(property);
    }

    private void evaluateFormulas() {
        // Clear existing formulas
        activeFormulas.clear();

        // Clear existing properties from all game objects
        clearAllGameObjectProperties();

        // Find all horizontal formulas
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth() - 2; x++) {
                findFormulaAt(x, y, 1, 0);
            }
        }

        // Find all vertical formulas
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight() - 2; y++) {
                findFormulaAt(x, y, 0, 1);
            }
        }

        // Apply properties to game objects based on formulas
        applyFormulasToGameObjects();
    }

    private void clearAllGameObjectProperties() {
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Cell cell = grid.getCell(x, y);
                for (Entity entity : cell.getEntities()) {
                    if (entity instanceof GameObject gameObject) {
                        gameObject.clearProperties();
                    }
                }
            }
        }
    }

    private void applyFormulasToGameObjects() {
        for (Formula formula : activeFormulas) {
            for (int x = 0; x < grid.getWidth(); x++) {
                for (int y = 0; y < grid.getHeight(); y++) {
                    Cell cell = grid.getCell(x, y);
                    for (Entity entity : cell.getEntities()) {
                        if (entity instanceof GameObject gameObject &&
                                gameObject.getType().equals(formula.subject)) {
                            gameObject.addProperty(formula.property);
                        }
                    }
                }
            }
        }
    }

    private void findFormulaAt(int x, int y, int dx, int dy) {
        Cell cell1 = grid.getCell(x, y);
        Cell cell2 = grid.getCell(x + dx, y + dy);
        Cell cell3 = grid.getCell(x + 2*dx, y + 2*dy);

        if (cell1 == null || cell2 == null || cell3 == null) return;

        for (Entity e1 : cell1.getEntities()) {
            if (e1 instanceof NounWord) {
                for (Entity e2 : cell2.getEntities()) {
                    if (e2 instanceof OperatorWord && "IS".equals(e2.getName())) {
                        for (Entity e3 : cell3.getEntities()) {
                            if (e3 instanceof PropertyWord) {
                                activeFormulas.add(new Formula(e1.getName(), e3.getName()));
                            }
                        }
                    }
                }
            }
        }
    }

    public void selectPlayer(int x, int y) {
        Cell cell = grid.getCell(x, y);
        if (cell != null) {
            for (Entity entity : cell.getEntities()) {
                if (entity instanceof GameObject gameObject && hasProperty(gameObject, "YOU")) {
                    activePlayerPosition = new Position(x, y);
                    activePlayerObject = gameObject;
                    return;
                }
            }
        }
    }

    public Entity getActivePlayerEntity() {
        return activePlayerObject;
    }

    private static class Formula {
        String subject;
        String property;

        public Formula(String subject, String property) {
            this.subject = subject;
            this.property = property;
        }
    }
}