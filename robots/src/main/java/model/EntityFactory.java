package model;

import model.object.GameObject;
import model.object.NounWord;
import model.object.OperatorWord;
import model.object.PropertyWord;
import model.object.abstractions.Word;
public class EntityFactory {
    static {
        // Initialize resources when factory is first loaded
        EntityResources.initialize();
    }

    public static Word createWord(String name) {
        EntityResources.EntityDefinition def = EntityResources.getDefinition(name);
        if (def == null) {
            return null;
        }

        return switch (def.type) {
            case "NOUN" -> new NounWord(def.name, def.color);
            case "PROPERTY" -> new PropertyWord(def.name, def.color);
            case "OPERATOR" -> new OperatorWord(def.name, def.color);
            default -> null;
        };
    }

    public static GameObject createGameObject(String type) {
        EntityResources.EntityDefinition def = EntityResources.getDefinition(type);
        if (def == null) {
            return null;
        }
        return new GameObject(type, def.color);
    }
}