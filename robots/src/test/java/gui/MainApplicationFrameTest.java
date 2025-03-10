package gui;

import static org.junit.jupiter.api.Assertions.*;

import gui.system.localization.LocalizationManager;
import gui.ui.LogWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class MainApplicationFrameTest {

    private MainApplicationFrame mainFrame;

    @BeforeEach
    public void setUp() throws InterruptedException, InvocationTargetException {
        // Создаем MainApplicationFrame на EDT
        SwingUtilities.invokeAndWait(() -> mainFrame = new MainApplicationFrame());
    }

    @Test
    public void testFrameInitialization() {
        // Проверяем, что объект mainFrame создан и десктоп-панель не равна null
        assertNotNull(mainFrame, "MainApplicationFrame должен быть создан");
        assertNotNull(mainFrame.getDesktopPane(), "DesktopPane не должен быть null");
        // Проверяем, что десктоп-панель установлена как content pane
        assertEquals(mainFrame.getContentPane(), mainFrame.getDesktopPane(), "DesktopPane должен быть установлен как content pane");

        // Проверяем, что размеры окна заданы (хотя в тестовой среде они могут быть не полностью реальными)
        Rectangle bounds = mainFrame.getBounds();
        assertTrue(bounds.width > 0, "Ширина MainApplicationFrame должна быть больше 0");
        assertTrue(bounds.height > 0, "Высота MainApplicationFrame должна быть больше 0");

        // Проверяем, что операция закрытия установлена как DO_NOTHING_ON_CLOSE
        assertEquals(JFrame.DO_NOTHING_ON_CLOSE, mainFrame.getDefaultCloseOperation(),
                "Операция закрытия должна быть DO_NOTHING_ON_CLOSE");
    }

    @Test
    public void testTitleUpdate() throws InterruptedException, InvocationTargetException {
        // Обновляем заголовок и проверяем, что он соответствует локализованной строке
        SwingUtilities.invokeAndWait(() -> mainFrame.updateTitle());
        String expectedTitle = LocalizationManager.getInstance().getString("application.title");
        assertEquals(expectedTitle, mainFrame.getTitle(), "Заголовок MainApplicationFrame должен соответствовать локализованной строке");
    }

    @Test
    public void testAddWindow() throws InterruptedException, InvocationTargetException {
        // Создаем новое внутреннее окно
        JInternalFrame testInternalFrame = new JInternalFrame("Test Window", true, true, true, true);
        // Добавляем его с использованием метода addWindow()
        SwingUtilities.invokeAndWait(() -> mainFrame.addWindow(testInternalFrame));

        // Проверяем, что окно добавлено на десктоп-панель и сделано видимым
        boolean found = false;
        for (JInternalFrame frame : mainFrame.getDesktopPane().getAllFrames()) {
            if (frame == testInternalFrame) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Внутреннее окно должно быть добавлено в десктоп-панель");
        assertTrue(testInternalFrame.isVisible(), "Внутреннее окно должно быть видимым");
    }

    @Test
    public void testCreateLogWindow() throws InterruptedException, InvocationTargetException {
        // Создаем лог-окно
        final LogWindow[] holder = new LogWindow[1];
        SwingUtilities.invokeAndWait(() -> holder[0] = mainFrame.createLogWindow());
        LogWindow logWindow = holder[0];
        assertNotNull(logWindow, "LogWindow не должен быть null");

        // Проверяем, что координаты установлены как (10, 10)
        assertEquals(10, logWindow.getX(), "X координата LogWindow должна быть 10");
        assertEquals(10, logWindow.getY(), "Y координата LogWindow должна быть 10");
        // Проверяем, что размеры лог-окна заданы (после pack() они могут определяться содержимым)
        assertTrue(logWindow.getWidth() > 0, "Ширина LogWindow должна быть больше 0");
        assertTrue(logWindow.getHeight() > 0, "Высота LogWindow должна быть больше 0");
    }

    @Test
    public void testResizeInternalFrames() throws InterruptedException, InvocationTargetException {
        // Создаем внутреннее окно с начальными размерами и добавляем его
        JInternalFrame internalFrame = new JInternalFrame("Resizable Window", true, true, true, true);
        SwingUtilities.invokeAndWait(() -> {
            internalFrame.setBounds(0, 0, 200, 200);
            mainFrame.addWindow(internalFrame);
            // Устанавливаем начальный размер десктоп-панели
            mainFrame.getDesktopPane().setSize(800, 600);
        });
        // Первый вызов resizeInternalFrames() для инициализации oldWidth/oldHeight
        SwingUtilities.invokeAndWait(() -> mainFrame.resizeInternalFrames());
        // Симулируем изменение размера десктоп-панели
        SwingUtilities.invokeAndWait(() -> mainFrame.getDesktopPane().setSize(1200, 900));
        // Второй вызов для реального пересчета размеров
        SwingUtilities.invokeAndWait(() -> mainFrame.resizeInternalFrames());
        // Даем время на выполнение invokeLater
        Thread.sleep(100);

        Rectangle newBounds = internalFrame.getBounds();
        // Проверяем, что хотя бы один из размеров изменился (увеличился относительно исходных 200)
        assertTrue(newBounds.width > 200 || newBounds.height > 200,
                "Размеры внутреннего окна должны измениться после изменения размера десктоп-панели");
    }

    @Test
    public void testLocaleChanged() throws InterruptedException, InvocationTargetException {
        // Изначально обновляем заголовок
        SwingUtilities.invokeAndWait(() -> mainFrame.updateTitle());
        // Вызываем localeChanged(), которое должно обновить заголовок
        SwingUtilities.invokeAndWait(() -> mainFrame.localeChanged());
        String updatedTitle = mainFrame.getTitle();

        // Проверяем, что заголовок соответствует локализованной строке
        String expectedTitle = LocalizationManager.getInstance().getString("application.title");
        assertEquals(expectedTitle, updatedTitle, "После изменения локали заголовок должен обновиться");
    }
}
