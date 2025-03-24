package gui.ui.drawing;

import static org.junit.jupiter.api.Assertions.*;

import game.model.GameObject;
import model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

class GameVisualizerTest {

    private GameVisualizer gameVisualizer;
    private Robot robot;

    @BeforeEach
    void setUp() {
        gameVisualizer = new GameVisualizer();
        robot = gameVisualizer.getRobot();  // Получаем объект робота из GameVisualizer
    }

    @Test
    void testRobotInitialPosition() {
        // Проверка, что робот изначально находится в центре экрана
        int[] position = robot.getPositionInCell();
        assertEquals(10, position[0], "Робот должен быть в центре по оси X");
        assertEquals(10, position[1], "Робот должен быть в центре по оси Y");
    }

    @Test
    void testMoveRobotUp() {
        // Перемещаем робота вверх
        gameVisualizer.moveRobotInCells(0, -1);
        int[] position = robot.getPositionInCell();
        assertEquals(10, position[0], "Робот должен оставаться по оси X");
        assertEquals(9, position[1], "Робот должен переместиться на одну клетку вверх");
    }

    @Test
    void testMoveRobotDown() {
        // Перемещаем робота вниз
        gameVisualizer.moveRobotInCells(0, 1);
        int[] position = robot.getPositionInCell();
        assertEquals(10, position[0], "Робот должен оставаться по оси X");
        assertEquals(11, position[1], "Робот должен переместиться на одну клетку вниз");
    }

    @Test
    void testMoveRobotLeft() {
        // Перемещаем робота влево
        gameVisualizer.moveRobotInCells(-1, 0);
        int[] position = robot.getPositionInCell();
        assertEquals(9, position[0], "Робот должен переместиться на одну клетку влево");
        assertEquals(10, position[1], "Робот должен оставаться по оси Y");
    }

    @Test
    void testMoveRobotRight() {
        // Перемещаем робота вправо
        gameVisualizer.moveRobotInCells(1, 0);
        int[] position = robot.getPositionInCell();
        assertEquals(11, position[0], "Робот должен переместиться на одну клетку вправо");
        assertEquals(10, position[1], "Робот должен оставаться по оси Y");
    }

    @Test
    void testMoveRobotAndPushObject() {
        // Перемещаем объект в клетку рядом с роботом
        GameObject gameObject = gameVisualizer.getMovableObject();
        gameObject.setPosition(10, 11);  // Размещаем объект в клетке (10, 11)

        // Робот толкает объект
        gameVisualizer.moveRobotInCells(0, 1);  // Перемещаем робота вниз на одну клетку

        // Проверяем, что объект был перемещён
        int[] objectPosition = gameObject.getPosition();
        assertEquals(10, objectPosition[0], "Объект должен остаться в той же колонке");
        assertEquals(12, objectPosition[1], "Объект должен быть перемещен вниз");
    }

    @Test
    void testObjectIsNotPushedWhenRobotIsNotNextToIt() {
        // Проверяем, что объект не перемещается, если робот не рядом с ним
        GameObject gameObject = gameVisualizer.getMovableObject();
        gameObject.setPosition(10, 12);  // Размещаем объект в клетке (10, 12)

        // Робот не двигается рядом с объектом, так что объект не должен двигаться
        gameVisualizer.moveRobotInCells(1, 0);  // Перемещаем робота вправо

        // Проверяем, что объект не был перемещен
        int[] objectPosition = gameObject.getPosition();
        assertEquals(10, objectPosition[0], "Объект должен оставаться на месте");
        assertEquals(12, objectPosition[1], "Объект должен оставаться на месте");
    }

    @Test
    void testRenderingRobotAndObject() {
        // Загружаем моки для Graphics
        Graphics g = Mockito.mock(Graphics.class);
        gameVisualizer.paint(g);  // Рисуем панель

        // Проверяем, что отрисовка робота и объекта прошла без ошибок
        Mockito.verify(g, Mockito.atLeastOnce()).drawImage(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
    }
}
