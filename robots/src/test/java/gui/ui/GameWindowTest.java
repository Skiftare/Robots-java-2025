package gui.ui;

import gui.system.localization.LocalizationManager;
import gui.ui.drawing.GameVisualizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class GameWindowTest {

    private GameWindow gameWindow;
    private LocalizationManager localizationManager;

    @Before
    public void setUp() {
        gameWindow = new GameWindow();
        localizationManager = LocalizationManager.getInstance();
    }

    @After
    public void tearDown() {
        if (gameWindow != null) {
            gameWindow.dispose();
        }
    }

    @Test
    public void testTitleInitialization() {
        String expectedTitle = localizationManager.getString("game.window.title");
        assertEquals("Заголовок окна должен инициализироваться строкой из LocalizationManager",
                expectedTitle, gameWindow.getTitle());
    }

    @Test
    public void testLocaleChangedUpdatesTitle() {
        // Устанавливаем произвольное значение, чтобы проверить обновление
        gameWindow.setTitle("dummy");
        gameWindow.localeChanged();
        String expectedTitle = localizationManager.getString("game.window.title");
        assertEquals("После вызова localeChanged заголовок должен обновиться",
                expectedTitle, gameWindow.getTitle());
    }

    @Test
    public void testContainsGameVisualizer() {
        Container contentPane = gameWindow.getContentPane();
        boolean foundGameVisualizer = false;
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component child : panel.getComponents()) {
                    if (child instanceof GameVisualizer) {
                        foundGameVisualizer = true;
                        break;
                    }
                }
            }
        }
        assertTrue("GameWindow должен содержать компонент GameVisualizer", foundGameVisualizer);
    }

    @Test
    public void testDisposeRemovesListener() throws Exception {
        // Используем рефлексию для доступа к приватному полю "listeners" LocalizationManager
        Field listenersField = LocalizationManager.class.getDeclaredField("listeners");
        listenersField.setAccessible(true);
        List<?> listeners = (List<?>) listenersField.get(localizationManager);

        // Перед вызовом dispose() окно должно быть зарегистрировано как слушатель
        assertTrue("GameWindow должен быть зарегистрирован как слушатель", listeners.contains(gameWindow));

        gameWindow.dispose();

        // После вызова dispose() GameWindow должен быть удалён из списка слушателей
        assertFalse("После dispose() GameWindow не должен присутствовать в списке слушателей",
                listeners.contains(gameWindow));
    }
}
