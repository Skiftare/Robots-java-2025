package game.mechanic;

import game.model.GameObject;
import game.model.ObjectProperty;
import gui.ui.CoordinateGrid;

import java.util.ArrayList;
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

    public GameObject getPlayerObject() {
        for (GameObject obj : gameObjects) {
            if (obj.hasProperty(ObjectProperty.PLAYER)) {
                return obj;
            }
        }
        return null;
    }

    public boolean movePlayer(int dx, int dy) {
        GameObject player = getPlayerObject();
        if (player == null || (dx == 0 && dy == 0)) {
            return false;
        }

        int[] playerPosition = player.getPosition();
        int playerX = playerPosition[0];
        int playerY = playerPosition[1];
        int newPlayerX = playerX + dx;
        int newPlayerY = playerY + dy;

        // Проверка границ
        if (newPlayerX < 0 || newPlayerX >= grid.getColumns() ||
                newPlayerY < 0 || newPlayerY >= grid.getRows()) {
            return false;
        }

        // Проверка пути и толкание объектов если нужно
        if (!isPathClearOrPushable(playerX, playerY, dx, dy)) {
            return false; // Путь заблокирован
        }

        // Перемещение игрока
        player.setPosition(newPlayerX, newPlayerY);

        // Проверка на специальные взаимодействия (например, WIN или KILL)
        checkInteractions(player);

        return true;
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
}