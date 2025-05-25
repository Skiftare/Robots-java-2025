package game.factory;

import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.ObjectProperty;
import game.model.formula.FormulaElement;
import game.model.formula.FormulaElementFactory;
import gui.system.localization.LocalizationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class to create and initialize game objects and formulas
 */
public class GameObjectFactory {

    /**
     * Creates a basic level with player, boxes, and walls
     */

    private static LocalizationManager localization = LocalizationManager.getInstance();
    
    private static String getLocalizedObjectName(String key) {
        return localization.getString("game.object." + key);
    }

    public static void createBasicLevel(MovementHandler movementHandler) {
        // Create player
        GameObject player = new GameObject(10, 10,
                "robots/src/main/resources/robot.png", getLocalizedObjectName("player"), "player");
        player.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player);

        // Create boxes
        GameObject box1 = new GameObject(5, 5,
                "robots/src/main/resources/object.png", getLocalizedObjectName("box"), "box");
        box1.addProperty(ObjectProperty.PUSHABLE);
        box1.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box1);

        GameObject box2 = new GameObject(6, 5,
                "robots/src/main/resources/object.png", getLocalizedObjectName("box"), "box");
        box2.addProperty(ObjectProperty.PUSHABLE);
        box2.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box2);

        // Create wall
        GameObject wall = new GameObject(8, 8,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall);

        // Create flag (win condition)
        GameObject flag = new GameObject(15, 15,
                "robots/src/main/resources/flag.png", getLocalizedObjectName("flag"), "flag");
        flag.addProperty(ObjectProperty.WIN);
        movementHandler.addGameObject(flag);

        // Create trap
        GameObject trap = new GameObject(12, 12,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap);
    }

    public static void createBasicFormulas(MovementHandler movementHandler) {
        // Player is You formula (horizontal)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(2, 2, getLocalizedObjectName("player")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(3, 2));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(4, 2, ObjectProperty.PLAYER));

        // Wall is Stop formula (vertical)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(12, 2, getLocalizedObjectName("wall")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(12, 3));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(12, 4, ObjectProperty.STOP));

        // Box is Push formula
        movementHandler.addGameObject(FormulaElementFactory.createNoun(2, 6, getLocalizedObjectName("box")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(3, 6));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(4, 6, ObjectProperty.PUSHABLE));

        // Flag is Win formula
        movementHandler.addGameObject(FormulaElementFactory.createNoun(8, 15, getLocalizedObjectName("flag")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(9, 15));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(10, 15, ObjectProperty.WIN));

        // Trap is Kill formula
        movementHandler.addGameObject(FormulaElementFactory.createNoun(6, 10, getLocalizedObjectName("trap")));
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
        //createBasicLevel(movementHandler);
        createCustomLevel(movementHandler);
        //createBasicFormulas(movementHandler);
        createCustomFormulas(movementHandler);


        // Process formulas once to establish initial rules
        movementHandler.getFormulaHandler().processFormulas();
    }

    /**
     * Create a custom level with specified objects and formulas
     */
    public static void createCustomLevel(MovementHandler movementHandler) {
        // Create player
        GameObject player = new GameObject(18, 9,
                "robots/src/main/resources/robot.png", getLocalizedObjectName("player"), "player");
        player.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player);

        // Create boxes
        GameObject box = new GameObject(6, 8,
                "robots/src/main/resources/object.png", getLocalizedObjectName("box"), "box");
        box.addProperty(ObjectProperty.PUSHABLE);
        box.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box);

        // Create walls
        GameObject wall1 = new GameObject(8, 10,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall1.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall1);

        GameObject wall2 = new GameObject(9, 10,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall2.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall2);

        GameObject wall3 = new GameObject(10, 10,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall3.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall3);

        GameObject wall4 = new GameObject(11, 10,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall4.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall4);

        GameObject wall5 = new GameObject(12, 10,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall5.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall5);

        GameObject wall6 = new GameObject(13, 10,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall6.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall6);

        GameObject wall7 = new GameObject(14, 10,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall7.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall7);

        GameObject wall8 = new GameObject(14, 11,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall8.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall8);

        GameObject wall9 = new GameObject(14, 12,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall9.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall9);

        GameObject wall10 = new GameObject(14, 13,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall10.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall10);

        GameObject wall11 = new GameObject(14, 14,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall11.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall11);

        GameObject wall12 = new GameObject(14, 15,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall12.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall12);

        GameObject wall13 = new GameObject(14, 16,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall13.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall13);

        GameObject wall14 = new GameObject(13, 16,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall14.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall14);

        GameObject wall15 = new GameObject(12, 16,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall15.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall15);

        GameObject wall16 = new GameObject(11, 16,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall16.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall16);

        GameObject wall17 = new GameObject(10, 16,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall17.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall17);

        GameObject wall18 = new GameObject(9, 16,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall18.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall18);

        GameObject wall19 = new GameObject(8, 16,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall19.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall19);

        GameObject wall20 = new GameObject(8, 15,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall20.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall20);

        GameObject wall21 = new GameObject(8, 14,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall21.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall21);

        GameObject wall22 = new GameObject(8, 13,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall22.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall22);

        GameObject wall23 = new GameObject(8, 12,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall23.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall23);

        GameObject wall24 = new GameObject(8, 11,
                "robots/src/main/resources/wall.png", getLocalizedObjectName("wall"), "wall");
        wall24.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall24);

        // Create flag (win condition)
        GameObject flag = new GameObject(11, 13,
                "robots/src/main/resources/flag.png", getLocalizedObjectName("flag"), "flag");
        flag.addProperty(ObjectProperty.WIN);
        movementHandler.addGameObject(flag);

        // Create traps
        GameObject trap1 = new GameObject(10, 12,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap1.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap1);

        GameObject trap2 = new GameObject(10, 13,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap2.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap2);

        GameObject trap3 = new GameObject(10, 14,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap3.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap3);

        GameObject trap4 = new GameObject(11, 14,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap4.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap4);

        GameObject trap5 = new GameObject(12, 14,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap5.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap5);

        GameObject trap6 = new GameObject(12, 13,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap6.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap6);

        GameObject trap7 = new GameObject(12, 12,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap7.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap7);

        GameObject trap8 = new GameObject(11, 12,
                "robots/src/main/resources/trap.png", getLocalizedObjectName("trap"), "trap");
        trap8.addProperty(ObjectProperty.KILL);
        movementHandler.addGameObject(trap8);
    }

    public static void createCustomFormulas(MovementHandler movementHandler) {
        // Box is You formula (horizontal)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(4, 2, getLocalizedObjectName("box")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(5, 2));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(6, 2, ObjectProperty.PLAYER));

        // Wall is Stop formula (horizontal)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(4, 3, getLocalizedObjectName("wall")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(5, 3));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(6, 3, ObjectProperty.STOP));

        // Trap is Kill formula (horizontal)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(4, 4, getLocalizedObjectName("trap")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(5, 4));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(6, 4, ObjectProperty.KILL));

        // Flag is Stop formula (horizontal)
        movementHandler.addGameObject(FormulaElementFactory.createNoun(4, 5, getLocalizedObjectName("flag")));
        movementHandler.addGameObject(FormulaElementFactory.createVerb(5, 5));
        movementHandler.addGameObject(FormulaElementFactory.createProperty(6, 5, ObjectProperty.STOP));

        // Push
        movementHandler.addGameObject(FormulaElementFactory.createProperty(14, 4, ObjectProperty.PUSHABLE));

        // Win
        movementHandler.addGameObject(FormulaElementFactory.createProperty(18, 18, ObjectProperty.WIN));

        // Player
        movementHandler.addGameObject(FormulaElementFactory.createNoun(4, 17, getLocalizedObjectName("player")));
    }

    public static void createLevel1(MovementHandler movementHandler) {
        movementHandler.clearGameObjects();

        // Пример: текущий кастомный уровень
        createCustomLevel(movementHandler);
        createCustomFormulas(movementHandler);

        movementHandler.getFormulaHandler().processFormulas();
    }

    public static void createLevel2(MovementHandler movementHandler) {
        movementHandler.clearGameObjects();

        // TODO: реализуйте второй уровень (другой набор объектов)
        createBasicLevel(movementHandler);
        createBasicFormulas(movementHandler);

        movementHandler.getFormulaHandler().processFormulas();
    }

    public static void createLevel3(MovementHandler movementHandler) {
        movementHandler.clearGameObjects();

        // TODO: реализуйте третий уровень
        createCustomLevel(movementHandler);
        createCustomFormulas(movementHandler);

        movementHandler.getFormulaHandler().processFormulas();
    }

}