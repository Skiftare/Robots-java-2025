package game.mechanic;

import game.model.GameObject;
import game.model.ObjectProperty;
import gui.ui.CoordinateGrid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovementHandler {
    private final CoordinateGrid grid;
    private final List<GameObject> gameObjects = new ArrayList<>();

    public MovementHandler(CoordinateGrid grid) {
        this.grid = grid;
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
        List<GameObject> players = getPlayerObjects();
        if (players.isEmpty() || (dx == 0 && dy == 0)) {
            return false;
        }

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
            if (!isPathClearOrPushable(playerX, playerY, dx, dy)) {
                continue; // Path blocked for this player
            }

            // Move this player
            player.setPosition(newPlayerX, newPlayerY);
            anyPlayerMoved = true;

            // Check for special interactions
            checkInteractions(player);
        }

        return anyPlayerMoved;
    }

    private void checkInteractions(GameObject player) {
        int[] playerPos = player.getPosition();
        for (GameObject obj : gameObjects) {
            if (obj == player) continue;

            int[] objPos = obj.getPosition();
            if (objPos[0] == playerPos[0] && objPos[1] == playerPos[1]) {
                // Здесь будет логика для WIN, KILL и других взаимодействий
                // на основе свойств объектов
            }
        }
    }

    /**
     * Рекурсивно проверяет, свободен ли путь или можно ли толкать объекты
     */
    private boolean isPathClearOrPushable(int startX, int startY, int dx, int dy) {
        int nextX = startX + dx;
        int nextY = startY + dy;

        // Проверка границ
        if (nextX < 0 || nextX >= grid.getColumns() ||
                nextY < 0 || nextY >= grid.getRows()) {
            return false;
        }

        // Проверка на объекты
        GameObject objectAtNext = getObjectAt(nextX, nextY);
        if (objectAtNext == null) {
            return true; // Путь свободен
        }

        // Если объект имеет свойство STOP и не PUSHABLE, путь заблокирован
        if (objectAtNext.hasProperty(ObjectProperty.STOP) &&
                !objectAtNext.hasProperty(ObjectProperty.PUSHABLE)) {
            return false;
        }

        // Если объект PUSHABLE, проверяем рекурсивно дальше
        if (objectAtNext.hasProperty(ObjectProperty.PUSHABLE)) {
            if (isPathClearOrPushable(nextX, nextY, dx, dy)) {
                // Толкаем объект
                objectAtNext.setPosition(nextX + dx, nextY + dy);
                return true;
            }
        }

        return false; // Не можем толкать объект
    }

    private GameObject getObjectAt(int x, int y) {
        for (GameObject obj : gameObjects) {
            int[] pos = obj.getPosition();
            if (pos[0] == x && pos[1] == y) {
                return obj;
            }
        }
        return null;
    }

    //Удаляет объект из игрового мира
    public void removeGameObject(GameObject object) {
        gameObjects.remove(object);
    }

    //Удаляет все объекты из игрового мира
    public void clearGameObjects() {
        gameObjects.clear();
    }
}