package gui.system;

import gui.MainApplicationFrame;
import gui.ui.GameWindow;
import gui.system.localization.LocalizationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.beans.PropertyVetoException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationMenuTest {

    @Mock
    private MainApplicationFrame mainFrame;
    @Mock
    private GameWindow gameWindow;

    private ApplicationMenu menu;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mainFrame.getGameWindow()).thenReturn(gameWindow);
        when(gameWindow.isClosed()).thenReturn(false);
        doNothing().when(mainFrame).showLevelSelectionMenu(any());

        menu = new ApplicationMenu(mainFrame);
    }

    @Test
    void testExitToMenuPresence() {
        String exitMenuText = LocalizationManager.getInstance().getString("menu.exitToMenu");
        JMenu exitMenu = findMenuByText(exitMenuText);
        assertNotNull(exitMenu, "Должно быть выпадающее меню «Exit to Menu»");
        assertEquals(1, exitMenu.getItemCount(), "В меню должен быть ровно один пункт");
        JMenuItem actionItem = exitMenu.getItem(0);
        String exitActionText = LocalizationManager.getInstance().getString("menu.exitToMenu.action");
        assertEquals(exitActionText, actionItem.getText(), "Текст пункта меню некорректен");
    }

    @Test
    void testExitToMenuAction() throws PropertyVetoException {
        String exitMenuText = LocalizationManager.getInstance().getString("menu.exitToMenu");
        JMenu exitMenu = findMenuByText(exitMenuText);
        JMenuItem actionItem = exitMenu.getItem(0);

        // Симулируем клик
        actionItem.doClick();

        // Проверяем, что close() вызван на mock-окне
        verify(gameWindow).setClosed(true);
        // Проверяем, что показано меню выбора уровня
        verify(mainFrame).showLevelSelectionMenu(any());
    }

    /** Ищет JMenu в JMenuBar по тексту */
    private JMenu findMenuByText(String text) {
        for (int i = 0; i < menu.getMenuCount(); i++) {
            JMenu m = menu.getMenu(i);
            if (text.equals(m.getText())) {
                return m;
            }
        }
        return null;
    }
}
