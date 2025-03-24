package gui.system.saving;
import model.Robot;
import game.model.GameObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameLoader {
    public static void loadGameState(Robot robot, GameObject gameObject) {
        try {
            // Читаем строку из файла
            File saveFile = new File("savegame.txt");
            BufferedReader reader = new BufferedReader(new FileReader(saveFile));
            String saveData = reader.readLine();
            reader.close();

            if (saveData != null) {
                String[] coordinates = saveData.split(",");

                int robotX = Integer.parseInt(coordinates[0]);
                int robotY = Integer.parseInt(coordinates[1]);
                int objectX = Integer.parseInt(coordinates[2]);
                int objectY = Integer.parseInt(coordinates[3]);

                robot.setPositionInCell(robotX, robotY);
                gameObject.setPosition(objectX, objectY);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
