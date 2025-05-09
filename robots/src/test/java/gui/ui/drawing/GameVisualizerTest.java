package gui.ui.drawing;

import game.model.GameObject;
import game.model.ObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameVisualizerTest {

    private GameVisualizer gameVisualizer;

    @BeforeEach
    void setUp() {
        gameVisualizer = new GameVisualizer();
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        GameObject box1 = new GameObject(5, 5, null, "Box", "box");
        box1.addProperty(ObjectProperty.PUSHABLE);
        box1.addProperty(ObjectProperty.STOP);
        gameObjects.add(box1);


        GameObject player = new GameObject(10, 10, null, "Player", "player");
        player.addProperty(ObjectProperty.PLAYER);
        gameObjects.add(player);

        GameObject wall = new GameObject(16, 16, null, "Wall", "wall");
        wall.addProperty(ObjectProperty.STOP);


        gameObjects.add(wall);
        gameVisualizer.rewriteGameObjects(gameObjects);
    }

    @Test
    void testInitialization() {
        // Check if the game starts with initial objects
        ArrayList<GameObject> objects = gameVisualizer.getGameObjects();

        // Should have 3 objects: player, box, and wall
        assertEquals(3, objects.size());

        // Verify each object type exists
        boolean hasPlayer = false;
        boolean hasBox = false;
        boolean hasWall = false;

        for (GameObject obj : objects) {
            if ("player".equals(obj.getType()) && obj.hasProperty(ObjectProperty.PLAYER)) {
                hasPlayer = true;
            } else if ("box".equals(obj.getType()) && obj.hasProperty(ObjectProperty.PUSHABLE)) {
                hasBox = true;
            } else if ("wall".equals(obj.getType()) && obj.hasProperty(ObjectProperty.STOP)) {
                hasWall = true;
            }
        }

        assertTrue(hasPlayer, "Player should be initialized");
        assertTrue(hasBox, "Box should be initialized");
        assertTrue(hasWall, "Wall should be initialized");
    }

    @Test
    void testGetMovableObjects() {
        ArrayList<GameObject> movableObjects = gameVisualizer.getMovableObjects();

        // Only the box should be movable (has PUSHABLE property)
        assertEquals(1, movableObjects.size());
        assertEquals("box", movableObjects.get(0).getType());
        assertTrue(movableObjects.get(0).hasProperty(ObjectProperty.PUSHABLE));
    }

    @Test
    void testMovePlayer() {
        // Get the player's initial position
        GameObject player = findPlayerObject();
        int initialX = player.getPosition()[0];
        int initialY = player.getPosition()[1];

        // Move right
        gameVisualizer.movePlayerInCells(1, 0);
        assertEquals(initialX + 1, player.getPosition()[0]);
        assertEquals(initialY, player.getPosition()[1]);

        // Move down
        gameVisualizer.movePlayerInCells(0, 1);
        assertEquals(initialX + 1, player.getPosition()[0]);
        assertEquals(initialY + 1, player.getPosition()[1]);
    }


    @Test
    void testRewriteGameObjects() {
        // Create new objects to update with
        ArrayList<GameObject> newObjects = new ArrayList<>();
        GameObject newPlayer = new GameObject(3, 3, "test.png", "New Player", "player");
        newPlayer.addProperty(ObjectProperty.PLAYER);
        newObjects.add(newPlayer);

        // Update game objects
        gameVisualizer.rewriteGameObjects(newObjects);

        // Check if objects were updated
        ArrayList<GameObject> updatedObjects = gameVisualizer.getGameObjects();
        assertEquals(1, updatedObjects.size());
        assertEquals("player", updatedObjects.get(0).getType());
        assertEquals(3, updatedObjects.get(0).getPosition()[0]);
        assertEquals(3, updatedObjects.get(0).getPosition()[1]);
    }

    // Helper method to find the player object
    private GameObject findPlayerObject() {
        for (GameObject obj : gameVisualizer.getGameObjects()) {
            if (obj.hasProperty(ObjectProperty.PLAYER)) {
                return obj;
            }
        }
        fail("Player object not found");
        return null;
    }

    @Test
    void testPushBox() {
        // Get player and box objects
        GameObject player = findPlayerObject();
        GameObject box = findBoxObject();

        // Position player next to box (assuming the box is at 5,5)
        while (player.getPosition()[0] != box.getPosition()[0] - 1 || player.getPosition()[1] != box.getPosition()[1]) {
            if (player.getPosition()[0] < box.getPosition()[0] - 1) gameVisualizer.movePlayerInCells(1, 0);
            if (player.getPosition()[0] > box.getPosition()[0] - 1) gameVisualizer.movePlayerInCells(-1, 0);
            if (player.getPosition()[1] < box.getPosition()[1]) gameVisualizer.movePlayerInCells(0, 1);
            if (player.getPosition()[1] > box.getPosition()[1]) gameVisualizer.movePlayerInCells(0, -1);
        }

        // Get box position before push
        int boxInitialX = box.getPosition()[0];
        int boxInitialY = box.getPosition()[1];

        // Push box right
        gameVisualizer.movePlayerInCells(1, 0);

        // Check if box moved
        assertEquals(boxInitialX + 1, box.getPosition()[0]);
        assertEquals(boxInitialY, box.getPosition()[1]);
    }

    // Helper method to find the box object
    private GameObject findBoxObject() {
        for (GameObject obj : gameVisualizer.getGameObjects()) {
            if (obj.hasProperty(ObjectProperty.PUSHABLE)) {
                return obj;
            }
        }
        fail("Box object not found");
        return null;
    }
}