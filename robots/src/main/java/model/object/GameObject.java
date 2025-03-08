package model.object;

import model.object.abstractions.Entity;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class GameObject extends Entity {
    private String type; // The noun this object represents (e.g., "PERS", "STUDENT")
    private final Set<String> properties = new HashSet<>();

    public GameObject(String type, Color color) {
        super(type, color);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void addProperty(String property) {
        properties.add(property);
    }

    public void clearProperties() {
        properties.clear();
    }

    public boolean hasProperty(String property) {
        return properties.contains(property);
    }
}