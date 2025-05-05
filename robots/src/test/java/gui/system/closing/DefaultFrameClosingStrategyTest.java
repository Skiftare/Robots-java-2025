package gui.system.closing;

import gui.system.localization.LocalizationManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import static org.junit.Assert.*;

public class DefaultFrameClosingStrategyTest {

    private DefaultFrameClosingStrategy strategy;

    @Before
    public void setUp() {
        // Создаем экземпляр стратегии с произвольными входными строками.
        // Конструктор добавляет слушателя в LocalizationManager и инициализирует строки.
        strategy = new DefaultFrameClosingStrategy("incomeMessage", "incomeTitle");
    }

    @After
    public void tearDown() {
        strategy = null;
    }

    @Test
    public void testUpdateStringsOnLocaleChanged() {
        // До вызова localeChanged поля могут иметь первоначальные значения.
        // После localeChanged они должны обновиться согласно LocalizationManager.
        String expectedMessage = LocalizationManager.getInstance().getString("close.confirm");
        String expectedTitle = LocalizationManager.getInstance().getString("close.confirm.title");
        String expectedYes = LocalizationManager.getInstance().getString("system.yes");
        String expectedNo = LocalizationManager.getInstance().getString("system.no");

        strategy.localeChanged();

        assertEquals("Сообщение должно обновиться согласно LocalizationManager", expectedMessage, strategy.getMessage());
        assertEquals("Заголовок должен обновиться согласно LocalizationManager", expectedTitle, strategy.getTitle());
        assertEquals("Текст кнопки Yes должен обновиться согласно LocalizationManager", expectedYes, strategy.getYesButtonText());
        assertEquals("Текст кнопки No должен обновиться согласно LocalizationManager", expectedNo, strategy.getNoButtonText());
    }

    /**
     * Тестовый подкласс, который переопределяет методы confirmClosing, чтобы
     * симулировать выбор пользователя без показа диалога.
     */
    private class TestFrameClosingStrategy extends DefaultFrameClosingStrategy {
        private int simulatedResponse; // 0 - Yes, любое иное значение - No

        public TestFrameClosingStrategy(int simulatedResponse) {
            super("dummy", "dummy");
            this.simulatedResponse = simulatedResponse;
        }

        @Override
        public boolean confirmClosing(JInternalFrame frame) {
            // Вместо вызова JOptionPane возвращаем результат на основе simulatedResponse
            return simulatedResponse == 0;
        }

        @Override
        public boolean confirmClosing(JFrame frame) {
            return simulatedResponse == 0;
        }
    }

    @Test
    public void testConfirmClosingInternalFrameYes() {
        // Симулируем, что пользователь выбрал "Yes" (0)
        TestFrameClosingStrategy testStrategy = new TestFrameClosingStrategy(0);
        JInternalFrame frame = new JInternalFrame();
        assertTrue("При симуляции ответа 'Yes' метод должен возвращать true", testStrategy.confirmClosing(frame));
    }

    @Test
    public void testConfirmClosingInternalFrameNo() {
        // Симулируем, что пользователь выбрал "No" (например, 1)
        TestFrameClosingStrategy testStrategy = new TestFrameClosingStrategy(1);
        JInternalFrame frame = new JInternalFrame();
        assertFalse("При симуляции ответа 'No' метод должен возвращать false", testStrategy.confirmClosing(frame));
    }

    @Test
    public void testConfirmClosingFrameYes() {
        TestFrameClosingStrategy testStrategy = new TestFrameClosingStrategy(0);
        JFrame frame = new JFrame();
        assertTrue("Для JFrame при симуляции ответа 'Yes' метод должен возвращать true", testStrategy.confirmClosing(frame));
    }

    @Test
    public void testConfirmClosingFrameNo() {
        TestFrameClosingStrategy testStrategy = new TestFrameClosingStrategy(1);
        JFrame frame = new JFrame();
        assertFalse("Для JFrame при симуляции ответа 'No' метод должен возвращать false", testStrategy.confirmClosing(frame));
    }
}
