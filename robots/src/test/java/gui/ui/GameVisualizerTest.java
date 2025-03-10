package gui.ui;

import model.Robot;
import model.Target;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class GameVisualizerTest {

    private GameVisualizer gameVisualizer;

    @Before
    public void setUp() {
        // Создаем экземпляр визуализатора игры
        gameVisualizer = new GameVisualizer();
    }

    @Test
    public void testSetTargetPosition() throws Exception {
        // Задаем новую позицию для цели
        Point newTarget = new Point(250, 300);
        gameVisualizer.setTargetPosition(newTarget);

        // С помощью рефлексии получаем приватное поле target
        Field targetField = GameVisualizer.class.getDeclaredField("target");
        targetField.setAccessible(true);
        Target target = (Target) targetField.get(gameVisualizer);

        assertEquals("X координата цели должна обновиться", newTarget.x, target.getX(), 0.001);
        assertEquals("Y координата цели должна обновиться", newTarget.y, target.getY(), 0.001);
    }

    @Test
    public void testOnModelUpdateEventMovesRobot() throws Exception {
        // Чтобы onModelUpdateEvent не завершался досрочно, имитируем инициализацию панели:
        // устанавливаем размер и вызываем paint с тестовой графикой.
        gameVisualizer.setSize(800, 600);
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        gameVisualizer.paint(g); // в методе paint устанавливаются panelWidth, panelHeight и флаг isPanelInitialized

        // Получаем приватное поле robot через рефлексию
        Field robotField = GameVisualizer.class.getDeclaredField("robot");
        robotField.setAccessible(true);
        Robot robot = (Robot) robotField.get(gameVisualizer);

        // Запоминаем исходное положение робота
        double initialX = robot.getPositionX();
        double initialY = robot.getPositionY();

        // Обновляем цель – чтобы робот начал движение (например, вправо)
        gameVisualizer.setTargetPosition(new Point(200, 100));

        // Вызываем обновление модели
        gameVisualizer.onModelUpdateEvent();

        // Получаем обновленное положение робота
        double updatedX = robot.getPositionX();
        double updatedY = robot.getPositionY();

        // При движении к цели, находящейся правее, X должен увеличиться.
        assertTrue("Позиция робота по X должна увеличиться после обновления модели", updatedX > initialX);
    }

    @Test
    public void testMouseClickUpdatesTarget() throws Exception {
        // Симулируем событие клика мышью по панели.
        Point clickPoint = new Point(300, 350);
        MouseListener[] listeners = gameVisualizer.getMouseListeners();
        assertTrue("GameVisualizer должен иметь хотя бы один MouseListener", listeners.length > 0);

        MouseEvent clickEvent = new MouseEvent(
                gameVisualizer,
                MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(),
                0,
                clickPoint.x,
                clickPoint.y,
                1,
                false
        );

        // Вызываем обработчик клика для каждого зарегистрированного слушателя
        for (MouseListener listener : listeners) {
            listener.mouseClicked(clickEvent);
        }

        // Проверяем, что положение цели обновилось
        Field targetField = GameVisualizer.class.getDeclaredField("target");
        targetField.setAccessible(true);
        Target target = (Target) targetField.get(gameVisualizer);

        assertEquals("При клике мышью X координата цели должна обновиться", clickPoint.x, target.getX(), 0.001);
        assertEquals("При клике мышью Y координата цели должна обновиться", clickPoint.y, target.getY(), 0.001);
    }

    @Test
    public void testOnRedrawEventCallsRepaint() {
        // Создаем подкласс GameVisualizer, переопределяющий repaint, чтобы отследить его вызов.
        class TestGameVisualizer extends GameVisualizer {
            boolean repaintCalled = false;

            @Override
            public void repaint() {
                repaintCalled = true;
                super.repaint();
            }
        }

        TestGameVisualizer testVisualizer = new TestGameVisualizer();
        testVisualizer.onRedrawEvent();

        // Ждем немного, чтобы EventQueue.invokeLater выполнился
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue("Метод repaint должен быть вызван после onRedrawEvent", testVisualizer.repaintCalled);
    }
}
