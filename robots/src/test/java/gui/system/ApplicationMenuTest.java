package gui.system;

import gui.MainApplicationFrame;
import gui.system.localization.Language;
import gui.system.localization.LocalizationManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationMenuTest {

    private MainApplicationFrame mainFrameMock;
    private ApplicationMenu applicationMenu;
    private LocalizationManager localizationManager;

    @Before
    public void setUp() {
        // Создаём mock для основного окна приложения
        mainFrameMock = Mockito.mock(MainApplicationFrame.class);
        // Создаём экземпляр ApplicationMenu с mock-окном
        applicationMenu = new ApplicationMenu(mainFrameMock);
        localizationManager = LocalizationManager.getInstance();
    }

    @Test
    public void testSystemLookAndFeelAction() {
        // Получаем меню "Look & Feel"
        JMenu lookAndFeelMenu = applicationMenu.getMenu(0);
        assertNotNull("Меню 'Look & Feel' не найдено", lookAndFeelMenu);

        // Ищем пункт меню для системного стиля
        JMenuItem systemMenuItem = null;
        for (int i = 0; i < lookAndFeelMenu.getItemCount(); i++) {
            JMenuItem item = lookAndFeelMenu.getItem(i);
            if (item != null && item.getText().equals(localizationManager.getString("menu.view.system"))) {
                systemMenuItem = item;
                break;
            }
        }
        assertNotNull("Пункт меню для системного стиля не найден", systemMenuItem);

        // Имитируем нажатие на пункт меню
        systemMenuItem.doClick();

        // Проверяем, что после смены темы вызывается метод invalidate() у основного окна
        verify(mainFrameMock, atLeastOnce()).invalidate();
    }

    @Test
    public void testLanguageMenuAction() {
        // Текущий язык
        Language initialLanguage = localizationManager.getCurrentLanguage();
        Language targetLanguage = null;
        // Ищем альтернативный язык
        for (Language lang : Language.values()) {
            if (!lang.equals(initialLanguage)) {
                targetLanguage = lang;
                break;
            }
        }
        if (targetLanguage == null) {
            // Если альтернативного языка нет, тест считается пройденным
            return;
        }

        // Получаем меню выбора языка (оно третий пункт в строке меню)
        JMenu languageMenu = applicationMenu.getMenu(2);
        assertNotNull("Меню выбора языка не найдено", languageMenu);

        // Ищем пункт меню, соответствующий целевому языку
        JMenuItem languageMenuItem = null;
        for (int i = 0; i < languageMenu.getItemCount(); i++) {
            JMenuItem item = languageMenu.getItem(i);
            if (item != null && item.getText().equals(targetLanguage.getDisplayName())) {
                languageMenuItem = item;
                break;
            }
        }
        assertNotNull("Пункт меню для выбранного языка не найден", languageMenuItem);

        // Имитируем нажатие на пункт меню для смены языка
        languageMenuItem.doClick();

        // Проверяем, что текущий язык в LocalizationManager изменился
        assertEquals("Язык не изменился после выбора пункта меню", targetLanguage, localizationManager.getCurrentLanguage());
    }

    @Test
    public void testLocaleChangedUpdatesMenuTexts() {
        // Вызываем localeChanged(), который должен обновить тексты всех меню
        applicationMenu.localeChanged();

        JMenu lookAndFeelMenu = applicationMenu.getMenu(0);
        JMenu testMenu = applicationMenu.getMenu(1);
        JMenu languageMenu = applicationMenu.getMenu(2);

        assertEquals("Текст меню 'Look & Feel' не обновлён",
                localizationManager.getString("menu.view"), lookAndFeelMenu.getText());
        assertEquals("Текст тестового меню не обновлён",
                localizationManager.getString("menu.test"), testMenu.getText());
        assertEquals("Текст меню языка не обновлён",
                localizationManager.getString("menu.language"), languageMenu.getText());
    }
}
