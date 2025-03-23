package gui.system.saving;

import model.Robot;
import gui.ui.MovableObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameSaver {
    public static void saveGameState(Robot robot, MovableObject movableObject) {
        try {
            // Получаем текущие координаты робота и объекта
            int[] robotPosition = robot.getPositionInCell();
            int[] objectPosition = movableObject.getPosition();

            // Преобразуем координаты в строку
            String saveData = robotPosition[0] + "," + robotPosition[1] + "," +
                    objectPosition[0] + "," + objectPosition[1];

            // Создаем файл для сохранения
            File saveFile = new File("savegame.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                writer.write(saveData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
