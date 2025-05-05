package game.mechanic;

import game.model.GameObject;
import game.model.ObjectProperty;
import gui.ui.CoordinateGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MovementHandlerTest {

    private MovementHandler movementHandler;
    private CoordinateGrid grid;
    private GameObject player;
    private GameObject box;
    private GameObject wall;

    @BeforeEach
    void setUp() {
        grid = new CoordinateGrid(20, 20);
        movementHandler = new MovementHandler(grid);

        // Создаем игрока
        player = new GameObject(10, 10, null, "Игрок", "player");
        player.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player);

        // Создаем толкаемый ящик
        box = new GameObject(12, 10, null, "Ящик", "box");
        box.addProperty(ObjectProperty.PUSHABLE);
        box.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box);

        // Создаем стену (непроходимую)
        wall = new GameObject(8, 10, null, "Стена", "wall");
        wall.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(wall);
    }

    @Test
    void testBasicPlayerMovement() {
        // Перемещение игрока вправо
        boolean moved = movementHandler.movePlayers(1, 0);
        assertTrue(moved, "Игрок должен переместиться вправо");
        assertArrayEquals(new int[]{11, 10}, player.getPosition(), "Позиция игрока должна обновиться");

        // Перемещение игрока влево
        moved = movementHandler.movePlayers(-1, 0);
        assertTrue(moved, "Игрок должен переместиться влево");
        assertArrayEquals(new int[]{10, 10}, player.getPosition(), "Позиция игрока должна вернуться");
    }

    @Test
    void testPushingObject() {
        // Размещаем игрока рядом с коробкой
        player.setPosition(11, 10);
        // Толкаем коробку вправо
        boolean moved = movementHandler.movePlayers(1, 0);
        assertTrue(moved, "Игрок должен толкнуть коробку");
        assertArrayEquals(new int[]{12, 10}, player.getPosition(), "Позиция игрока должна обновиться");
        assertArrayEquals(new int[]{13, 10}, box.getPosition(), "Коробка должна сдвинуться");
    }

    @Test
    void testBlockedByWall() {
        // Размещаем игрока рядом со стеной
        player.setPosition(9, 10);

        // Пытаемся пройти через стену
        boolean moved = movementHandler.movePlayers(-1, 0);
        assertFalse(moved, "Игрок не должен проходить через стену");
        assertArrayEquals(new int[]{9, 10}, player.getPosition(), "Позиция игрока не должна меняться");
        assertArrayEquals(new int[]{8, 10}, wall.getPosition(), "Позиция стены не должна меняться");
    }

    @Test
    void testBoundaryCheck() {
        // Размещаем игрока у края карты
        player.setPosition(0, 0);

        // Пытаемся выйти за пределы карты
        boolean moved = movementHandler.movePlayers(-1, 0);
        assertFalse(moved, "Игрок не должен выходить за границы");
        assertArrayEquals(new int[]{0, 0}, player.getPosition(), "Позиция игрока не должна меняться");
    }

    @Test
    void testChainPushing() {
        // Создаем вторую коробку
        GameObject box2 = new GameObject(13, 10, null, "Коробка2", "box");
        box2.addProperty(ObjectProperty.PUSHABLE);
        box2.addProperty(ObjectProperty.STOP);
        movementHandler.addGameObject(box2);

        // Выстраиваем игрока и коробки в ряд
        player.setPosition(11, 10);
        box.setPosition(12, 10);

        // Толкаем цепочку коробок
        boolean moved = movementHandler.movePlayers(1, 0);
        assertTrue(moved, "Игрок должен толкнуть цепочку коробок");
        assertArrayEquals(new int[]{12, 10}, player.getPosition(), "Позиция игрока должна обновиться");
        assertArrayEquals(new int[]{13, 10}, box.getPosition(), "Первая коробка должна сдвинуться");
        assertArrayEquals(new int[]{14, 10}, box2.getPosition(), "Вторая коробка должна сдвинуться");
    }

    @Test
    void testCannotPushAgainstWall() {
        // Размещаем игрока и коробку перед стеной
        player.setPosition(9, 12);
        box.setPosition(8, 12);
        wall.setPosition(7, 12);

        // Пытаемся толкнуть коробку на стену
        boolean moved = movementHandler.movePlayers(-1, 0);
        assertFalse(moved, "Нельзя толкнуть коробку на стену");
        assertArrayEquals(new int[]{9, 12}, player.getPosition(), "Позиция игрока не должна меняться");
        assertArrayEquals(new int[]{8, 12}, box.getPosition(), "Позиция коробки не должна меняться");
    }

    @Test
    void testGetPlayerObject() {
        ArrayList<GameObject> retrievedPlayer = movementHandler.getPlayerObjects();
        assertSame(player, retrievedPlayer.get(0), "Должен вернуть правильный объект игрока");
    }
    @Test
    void testManyPlayerObjects() {
        player.setPosition(9, 12);
        GameObject player2 = new GameObject(9, 9, null, "Игрок", "player");
        player2.addProperty(ObjectProperty.PLAYER);
        movementHandler.addGameObject(player2);

        box.setPosition(8, 12);
        wall.setPosition(7, 12);

        ArrayList<GameObject> retrievedPlayers = movementHandler.getPlayerObjects();
        assertEquals(2, retrievedPlayers.size(), "Should have exactly two player objects");

        Set<String> expectedPositions = Set.of("9,12", "9,9");
        Set<String> actualPositions = retrievedPlayers.stream()
                .map(obj -> obj.getPosition()[0] + "," + obj.getPosition()[1])
                .collect(Collectors.toSet());
        assertEquals(expectedPositions, actualPositions, "Player positions should match expected coordinates");

        boolean moved = movementHandler.movePlayers(1, 0);
        assertTrue(moved, "At least one player should move");

        retrievedPlayers = movementHandler.getPlayerObjects();
        Set<String> expectedPositionsAfterMove = Set.of("10,12", "10,9");
        Set<String> actualPositionsAfterMove = retrievedPlayers.stream()
                .map(obj -> obj.getPosition()[0] + "," + obj.getPosition()[1])
                .collect(Collectors.toSet());
        assertEquals(expectedPositionsAfterMove, actualPositionsAfterMove, "Both players should have moved right");
    }
}