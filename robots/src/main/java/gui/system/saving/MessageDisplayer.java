package gui.system.saving;

import gui.system.localization.LocalizationManager;

import javax.swing.*;
import java.awt.*;

public class MessageDisplayer {

    public static void showCenteredMessage(JFrame parentFrame, String messageKey) {
        // Получаем локализованный текст для сообщения и заголовка
        String message = LocalizationManager.getInstance().getString(messageKey);
        String title = LocalizationManager.getInstance().getString("message.info");

        // Создаём панель с выравниванием текста по центру
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message, JLabel.CENTER);  // Выравнивание по центру
        label.setPreferredSize(new Dimension(400, 100));  // Задаем размер для улучшения отображения
        panel.add(label, BorderLayout.CENTER);

        // Создаём диалоговое окно с кастомным содержимым
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setContentPane(panel);
        dialog.pack();

        // Получаем размер экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Получаем размеры окна
        Dimension dialogSize = dialog.getPreferredSize();

        // Вычисляем координаты для центрирования
        int x = (int) ((screenSize.width - dialogSize.width) / 2);
        int y = (int) ((screenSize.height - dialogSize.height) / 2);

        // Устанавливаем позицию диалога
        dialog.setLocation(x, y);

        // Показываем диалог
        dialog.setVisible(true);
    }
}
