package game.mechanic;

import game.model.GameObject;
import game.model.ObjectProperty;
import game.model.formula.FormulaElement;
import game.model.formula.FormulaElementFactory;
import gui.ui.CoordinateGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaHandlerTest {

    private MovementHandler movementHandler;
    private FormulaHandler formulaHandler;
    private CoordinateGrid grid;

    @BeforeEach
    void setUp() {
        grid = new CoordinateGrid(20, 20);
        movementHandler = new MovementHandler(grid);
        formulaHandler = movementHandler.getFormulaHandler();
    }

    @Test
    void testBasicFormulaCreation() {
        // Create object
        GameObject box = new GameObject(5, 5, null, "Box", "box");
        movementHandler.addGameObject(box);

        // Create formula "BOX IS STOP"
        addFormula(2, 2, "box", ObjectProperty.STOP, true);

        // Process formulas
        boolean changed = formulaHandler.processFormulas();

        // Verify
        assertTrue(changed, "Formula processing should report changes");
        assertTrue(box.hasProperty(ObjectProperty.STOP), "Box should have STOP property");
    }

    @Test
    void testFormulaDestruction() {
        // Create object
        GameObject box = new GameObject(5, 5, null, "Box", "box");
        movementHandler.addGameObject(box);

        // Create formula elements
        FormulaElement noun = addNoun(2, 2, "box");
        FormulaElement verb = addVerb(3, 2);
        FormulaElement property = addProperty(4, 2, ObjectProperty.STOP);

        // Process formulas
        formulaHandler.processFormulas();
        assertTrue(box.hasProperty(ObjectProperty.STOP), "Box should have STOP property");

        // Break formula by moving one element away
        noun.setPosition(10, 10);

        // Process formulas again
        boolean changed = formulaHandler.processFormulas();

        // Verify
        assertTrue(changed, "Formula processing should report changes");
        assertFalse(box.hasProperty(ObjectProperty.STOP), "Box should not have STOP property after formula is broken");
    }

    @Test
    void testVerticalFormula() {
        // Create object
        GameObject wall = new GameObject(8, 8, null, "Wall", "wall");
        movementHandler.addGameObject(wall);

        // Create vertical formula "WALL IS WIN"
        addFormula(10, 5, "wall", ObjectProperty.WIN, false);

        // Process formulas
        formulaHandler.processFormulas();

        // Verify
        assertTrue(wall.hasProperty(ObjectProperty.WIN), "Wall should have WIN property from vertical formula");
    }

    @Test
    void testBranchingFormulas() {
        // Create object
        GameObject player = new GameObject(10, 10, null, "Player", "player");
        movementHandler.addGameObject(player);

        // Create horizontal formula "PLAYER IS STOP"
        addFormula(2, 2, "player", ObjectProperty.STOP, true);

        // Create vertical formula "PLAYER IS PUSHABLE"
        addFormula(5, 5, "player", ObjectProperty.PUSHABLE, false);

        // Process formulas
        formulaHandler.processFormulas();

        // Verify player has both properties
        assertTrue(player.hasProperty(ObjectProperty.STOP), "Player should have STOP property");
        assertTrue(player.hasProperty(ObjectProperty.PUSHABLE), "Player should have PUSHABLE property");
    }

    @Test
    void testPlayerPropertyIsRequiredForWin() {

        // Create object
        GameObject object = new GameObject(5, 5, null, "Object", "object");
        movementHandler.addGameObject(object);
        //PLAYER property
        addFormula(3, 3, "object", ObjectProperty.PLAYER, true);
        formulaHandler.processFormulas();

        //WIN property
        addFormula(4, 4, "object", ObjectProperty.WIN, true);
        formulaHandler.processFormulas();
        movementHandler.movePlayers(0, 0); // Trigger game state check

        // Verify game is won when an object has both PLAYER and WIN properties
        assertTrue(movementHandler.isGameWon(), "Game should be won when an object has both PLAYER and WIN properties");
    }

    @Test
    void testPlayerWithKillProperty() {

        // Create player
        GameObject player = new GameObject(5, 5, null, "Player", "player");
        player.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player);

        // Create formula "PLAYER IS KILL"
        addFormula(2, 2, "player", ObjectProperty.KILL, true);
        addFormula(3,3, "player", ObjectProperty.PLAYER, true);

        // Process formulas and check game state
        formulaHandler.processFormulas();
        movementHandler.movePlayers(0, 0); // Trigger game state check without moving

        assertTrue(movementHandler.isGameOver(), "Game should be over when all players are killed");
    }

    @Test
    void testSharedNounMultipleProperties() {
        // Create arrangement like:
        // box - is - push
        //  |
        // is
        //  |
        // win

        // Create object
        GameObject box = new GameObject(5, 5, null, "Box", "box");
        movementHandler.addGameObject(box);

        // Add horizontal formula elements
        FormulaElement noun = addNoun(3, 3, "box");
        addVerb(4, 3);
        addProperty(5, 3, ObjectProperty.PUSHABLE);

        // Add vertical formula elements (reusing the noun)
        addVerb(3, 4);
        addProperty(3, 5, ObjectProperty.WIN);

        // Process formulas
        formulaHandler.processFormulas();

        // Verify box has both properties
        assertTrue(box.hasProperty(ObjectProperty.PUSHABLE), "Box should have PUSHABLE property");
        assertTrue(box.hasProperty(ObjectProperty.WIN), "Box should have WIN property");
    }

    // Helper methods for creating formula elements
    private FormulaElement addNoun(int x, int y, String type) {
        FormulaElement noun = FormulaElementFactory.createNoun(x, y, type);
        movementHandler.addGameObject(noun);
        return noun;
    }

    private FormulaElement addVerb(int x, int y) {
        FormulaElement verb = FormulaElementFactory.createVerb(x, y);
        movementHandler.addGameObject(verb);
        return verb;
    }

    private FormulaElement addProperty(int x, int y, ObjectProperty property) {
        FormulaElement prop = FormulaElementFactory.createProperty(x, y, property);
        movementHandler.addGameObject(prop);
        return prop;
    }

    private void addFormula(int startX, int startY, String type, ObjectProperty property, boolean horizontal) {
        FormulaElement noun = addNoun(startX, startY, type);

        int verbX = horizontal ? startX + 1 : startX;
        int verbY = horizontal ? startY : startY + 1;
        FormulaElement verb = addVerb(verbX, verbY);

        int propX = horizontal ? startX + 2 : startX;
        int propY = horizontal ? startY : startY + 2;
        FormulaElement prop = addProperty(propX, propY, property);
    }
}