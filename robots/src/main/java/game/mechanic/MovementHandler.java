package game.mechanic;

import game.model.GameObject;
import game.model.ObjectProperty;
import gui.ui.CoordinateGrid;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovementHandler {
    private final CoordinateGrid grid;
    private final List<GameObject> gameObjects = new ArrayList<>();
    @Getter
    private final FormulaHandler formulaHandler;
    @Getter
    private boolean gameWon = false;
    @Getter
    private boolean gameOver = false;

    public MovementHandler(CoordinateGrid grid) {
        this.grid = grid;
        this.formulaHandler = new FormulaHandler(this);
    }

    public void addGameObject(GameObject object) {
        gameObjects.add(object);
    }

    public List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    public ArrayList<GameObject> getPlayerObjects() {
        ArrayList<GameObject> players = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (obj.hasProperty(ObjectProperty.PLAYER)) {
                players.add(obj);
            }
        }
        return players;
    }

    public boolean movePlayers(int dx, int dy) {
        boolean moved = movePlayersInternal(dx, dy);

        // Process formulas after movement
        boolean formulaChanged = formulaHandler.processFormulas();

        // Check game state after formula changes (without moving)
        if (moved || formulaChanged) {
            checkGameState();
        }

        return moved || formulaChanged;
    }

    private boolean movePlayersInternal(int dx, int dy) {
        List<GameObject> players = getPlayerObjects();
        if (players.isEmpty() || (dx == 0 && dy == 0)) {
            return false;
        }

        // Sort players by position based on movement direction for correct pushing behavior
        if (dx > 0) {
            players.sort((p1, p2) -> p2.getPosition()[0] - p1.getPosition()[0]);
        } else if (dx < 0) {
            players.sort(Comparator.comparingInt(p -> p.getPosition()[0]));
        } else if (dy > 0) {
            players.sort((p1, p2) -> p2.getPosition()[1] - p1.getPosition()[1]);
        } else {
            players.sort(Comparator.comparingInt(p -> p.getPosition()[1]));
        }

        boolean anyPlayerMoved = false;

        // Process each player independently
        for (GameObject player : players) {
            int[] playerPosition = player.getPosition();
            int playerX = playerPosition[0];
            int playerY = playerPosition[1];
            int newPlayerX = playerX + dx;
            int newPlayerY = playerY + dy;

            // Check boundaries
            if (newPlayerX < 0 || newPlayerX >= grid.getColumns() ||
                    newPlayerY < 0 || newPlayerY >= grid.getRows()) {
                continue; // Skip this player
            }

            // Check path and push objects if needed
            if (!canMoveToPosition(playerX, playerY, dx, dy)) {
                continue; // Path blocked for this player
            }

            // Move this player
            player.setPosition(newPlayerX, newPlayerY);
            anyPlayerMoved = true;

            checkGameState();
        }

        return anyPlayerMoved;
    }


    private void checkGameState() {
        List<GameObject> players = getPlayerObjects();
        gameOver = players.isEmpty();
        if (gameOver) {
            return;
        }

        for (GameObject player : players) {
            int[] playerPos = player.getPosition();
            List<GameObject> objectsAtPos = getObjectsAt(playerPos[0], playerPos[1]);

            for (GameObject obj : objectsAtPos) {
                if (obj.hasProperty(ObjectProperty.KILL)) {
                    gameObjects.remove(player);
                    break;
                }
                if (obj.hasProperty(ObjectProperty.WIN)) {
                    gameWon = true;
                    break;
                }
            }

            if (gameWon) break;
        }
    }


    /**
     * Checks if a move to the target position is possible
     */
    private boolean canMoveToPosition(int startX, int startY, int dx, int dy) {
        int nextX = startX + dx;
        int nextY = startY + dy;

        // Check boundaries
        if (nextX < 0 || nextX >= grid.getColumns() ||
                nextY < 0 || nextY >= grid.getRows()) {
            return false;
        }

        // Check all objects at the target position
        List<GameObject> objectsAtNext = getObjectsAt(nextX, nextY);

        // If no objects, path is clear
        if (objectsAtNext.isEmpty()) {
            return true;
        }

        boolean hasStop = false;
        boolean needsPush = false;

        // Check all objects at the position
        for (GameObject obj : objectsAtNext) {
            if (obj.hasProperty(ObjectProperty.STOP)) {
                hasStop = true;
            }
            if (obj.hasProperty(ObjectProperty.PUSHABLE)) {
                needsPush = true;
            }
        }

        // If any object has STOP and is not pushable, we can't move
        if (hasStop) {
            return false;
        }

        // If we have objects to push, check if we can push them
        if (needsPush) {
            // Try to push all pushable objects
            boolean canPushAll = true;
            for (GameObject obj : objectsAtNext) {
                if (obj.hasProperty(ObjectProperty.PUSHABLE)) {
                    // Recursively check if we can push this object
                    if (!canMoveToPosition(nextX, nextY, dx, dy)) {
                        canPushAll = false;
                        break;
                    }

                    // If we can push, update its position
                    obj.setPosition(nextX + dx, nextY + dy);
                }
            }
            return canPushAll;
        }

        // If no STOP or PUSHABLE objects, we can move freely (overlap)
        return true;
    }

    /**
     * Returns all objects at the specified position
     */
    private List<GameObject> getObjectsAt(int x, int y) {
        List<GameObject> result = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            int[] pos = obj.getPosition();
            if (pos[0] == x && pos[1] == y) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * Returns a single object at the position (for backward compatibility)
     */
    private GameObject getObjectAt(int x, int y) {
        List<GameObject> objects = getObjectsAt(x, y);
        return objects.isEmpty() ? null : objects.get(0);
    }

    // Removes an object from the game world
    public void removeGameObject(GameObject object) {
        gameObjects.remove(object);
    }

    // Removes all objects from the game world
    public void clearGameObjects() {
        gameObjects.clear();
    }

    // Add this method to MovementHandler class
    public void resetGameState() {
        this.gameWon = false;
        this.gameOver = false;
    }
}