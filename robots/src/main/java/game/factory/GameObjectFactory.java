package game.factory;

import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.ObjectProperty;
import game.model.formula.FormulaElement;
import game.model.formula.FormulaElementFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class to create and initialize game objects and formulas
 */
public class GameObjectFactory {

    /**
     * Creates a basic level with player, boxes, and walls
     */
    public static void createBasicLevel(MovementHandler movementHandler) {
        // Create player
        GameObject player = new GameObject(10, 10,
                "robots/src/main/resources/robot.png", "Player", "player");
        player.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player);

        // Create boxes
        GameObject box1 = new GameObject(5, 5,
                "robots/src/main/resources/object.png", "Box", "box");
        box1.addProperty(ObjectProperty.PUSHABLE);
        box1.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box1);

        GameObject box2 = new GameObject(6, 5,
                "robots/src/main/resources/object.png", "Box", "box");
        box2.addProperty(ObjectProperty.PUSHABLE);
        box2.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box2);

        // Create wall
        GameObject wall = new GameObject(8, 8,
                "robots/src/main/resources/wall.png", "Wall", "wall");
        wall.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall);

        // Create flag (win condition)
        GameObject flag = new GameObject(15, 15,
                "robots/src/main/resources/flag.png", "Flag", "flag");
        flag.addProperty(ObjectProperty.WIN);
        movementHandler.addGameObject(flag);

        // Create trap
        GameObject trap = new GameObject(12, 12,
                "robots/src/main/resources/trap.png", "Trap", "trap");
        trap.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap);
    }

    public static void createBasicFormulas(MovementHandler movementHandler) {
        // Player is You formula (horizontal)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(2, 2, "player"));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(3, 2));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(4, 2, ObjectProperty.PLAYER));

        // Wall is Stop formula (vertical)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(12, 2, "wall"));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(12, 3));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(12, 4, ObjectProperty.STOP));

        // Box is Push formula
        movementHandler.addGameObject(FormulaElementFactory.createNoun(2, 6, "box"));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(3, 6));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(4, 6, ObjectProperty.PUSHABLE));

        // Flag is Win formula
        movementHandler.addGameObject(FormulaElementFactory.createNoun(8, 15, "flag"));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(9, 15));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(10, 15, ObjectProperty.WIN));

        // Trap is Kill formula
        movementHandler.addGameObject(FormulaElementFactory.createNoun(6, 10, "trap"));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(7, 10));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(8, 10, ObjectProperty.KILL));
    }

    /**
     * Initialize a level with all objects including formulas
     */
    public static void initializeGame(MovementHandler movementHandler) {
        // Clear any existing objects
        movementHandler.clearGameObjects();

        // Add game objects and formulas
        createBasicLevel(movementHandler);
        createBasicFormulas(movementHandler);

        // Process formulas once to establish initial rules
        movementHandler.getFormulaHandler().processFormulas();
    }

    /**
     * Create a custom level with specified objects and formulas
     */
    public static void createCustomLevel(MovementHandler movementHandler,
                                         List<GameObject> gameObjects,
                                         List<FormulaElement> formulaElements) {
        // Clear existing objects
        movementHandler.clearGameObjects();

        // Add custom objects
        for (GameObject obj : gameObjects) {
            movementHandler.addGameObject(obj);
        }

        // Add formula elements
        for (FormulaElement element : formulaElements) {
            movementHandler.addGameObject(element);
        }

        // Process formulas
        movementHandler.getFormulaHandler().processFormulas();
    }
}