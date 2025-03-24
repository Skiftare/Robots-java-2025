package gui.system.saving;

import game.model.GameObject;
import game.model.ObjectProperty;
import gui.ui.drawing.GameVisualizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GameLoader {
    public static boolean loadGameState(GameVisualizer gameVisualizer) {
        File saveFile = new File("savegame.txt");
        if (!saveFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            // Очищаем текущие объекты
            ArrayList<GameObject> currentObjects = gameVisualizer.getGameObjects();
            ArrayList<GameObject> newObjects = new ArrayList<>();

            // Читаем количество объектов
            int count = Integer.parseInt(reader.readLine());

            // Читаем каждый объект
            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                String[] parts = line.split(",");

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                String type = parts[2];
                String label = parts[3];
                boolean hasTexture = "1".equals(parts[4]);

                GameObject obj = findObjectByType(currentObjects, type);
                if (obj == null) {
                    String texturePath = getDefaultTextureForType(type);
                    obj = new GameObject(x, y, texturePath, label, type);
                } else {
                    obj.setPosition(x, y);
                }

                // Очищаем свойства и добавляем новые
                obj.clearProperties();
                for (int j = 5; j < parts.length; j++) {
                    try {
                        ObjectProperty property = ObjectProperty.valueOf(parts[j]);
                        obj.addProperty(property);
                    } catch (IllegalArgumentException e) {
                    }
                }

                newObjects.add(obj);
            }

            gameVisualizer.updateGameObjects(newObjects);

            return true;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static GameObject findObjectByType(ArrayList<GameObject> objects, String type) {
        for (GameObject obj : objects) {
            if (type.equals(obj.getType())) {
                return obj;
            }
        }
        return null;
    }

    private static String getDefaultTextureForType(String type) {
        return switch (type) {
            case "player" -> "robots/src/main/resources/robot.png";
            case "box" -> "robots/src/main/resources/object.png";
            case "wall" -> "robots/src/main/resources/wall.png";
            default -> null;
        };
    }
}