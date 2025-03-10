package gui.system.localization;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LocalizationManagerTest {

    private LocalizationManager manager;

    @Before
    public void setUp() {
        // Получаем экземпляр менеджера.
        manager = LocalizationManager.getInstance();
    }

    @Test
    public void testSingleton() {
        LocalizationManager instance1 = LocalizationManager.getInstance();
        LocalizationManager instance2 = LocalizationManager.getInstance();
        assertSame("Экземпляры LocalizationManager должны совпадать (singleton)", instance1, instance2);
    }

    @Test
    public void testGetStringMissingKey() {
        // Если ресурсный bundle не содержит перевод для ключа, метод должен вернуть "!key!".
        String missingKey = "nonexistent.key";
        String result = manager.getString(missingKey);
        assertEquals("!nonexistent.key!", result);
    }

    @Test
    public void testLocaleChangeListenerNotification() {
        // Создаём тестового слушателя, который устанавливает флаг при вызове localeChanged().
        TestLocaleChangeListener listener = new TestLocaleChangeListener();
        manager.addListener(listener);

        // Пытаемся сменить язык на другой. Выбираем первый язык, отличный от текущего.
        Language newLanguage = null;
        for (Language lang : Language.values()) {
            if (lang != manager.getCurrentLanguage()) {
                newLanguage = lang;
                break;
            }
        }
        // Если найден альтернативный язык, выполняем смену.
        if (newLanguage != null) {
            manager.setLanguage(newLanguage);
            assertTrue("Слушатель должен быть уведомлён о смене локали", listener.isNotified());
        } else {
            // Если альтернативного языка нет, тест пропускаем.
            System.out.println("Альтернативный язык не найден, тест пропущен.");
        }
    }

    @Test
    public void testRemoveListener() {
        TestLocaleChangeListener listener = new TestLocaleChangeListener();
        manager.addListener(listener);
        manager.removeListener(listener);

        // Пытаемся сменить язык, чтобы убедиться, что удалённый слушатель не уведомляется.
        Language newLanguage = null;
        for (Language lang : Language.values()) {
            if (lang != manager.getCurrentLanguage()) {
                newLanguage = lang;
                break;
            }
        }
        if (newLanguage != null) {
            manager.setLanguage(newLanguage);
            assertFalse("После удаления слушателя уведомление не должно приходить", listener.isNotified());
        }
    }

    @Test
    public void testSetSameLanguageDoesNotNotify() {
        TestLocaleChangeListener listener = new TestLocaleChangeListener();
        manager.addListener(listener);

        // При установке того же языка уведомление не должно происходить.
        Language current = manager.getCurrentLanguage();
        manager.setLanguage(current);
        assertFalse("При установке того же языка слушатель не должен быть уведомлён", listener.isNotified());
    }

    /**
     * Простейшая реализация интерфейса LocaleChangeListener для целей тестирования.
     */
    private static class TestLocaleChangeListener implements LocaleChangeListener {
        private boolean notified = false;

        @Override
        public void localeChanged() {
            notified = true;
        }

        public boolean isNotified() {
            return notified;
        }
    }
}
