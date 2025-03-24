package gui.system.saving;

import game.model.GameObject;
import game.model.ObjectProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GameSaver {
    public static void saveGameState(List<GameObject> gameObjects) {
        try {
            File saveFile = new File("savegame.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {

                writer.write(gameObjects.size() + "\n");

                for (GameObject obj : gameObjects) {
                    int[] position = obj.getPosition();
                    StringBuilder sb = new StringBuilder();
                    sb.append(position[0]).append(",")
                            .append(position[1]).append(",")
                            .append(obj.getType()).append(",")
                            .append(obj.getLabel()).append(",")
                            .append(obj.getTexture() != null ? "1" : "0");

                    for (ObjectProperty property : ObjectProperty.values()) {
                        if (obj.hasProperty(property)) {
                            sb.append(",").append(property.name());
                        }
                    }

                    writer.write(sb.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}