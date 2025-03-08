package model.object.abstractions;

import lombok.Data;
import java.awt.Color;

@Data
public abstract class Entity {
    private String name;
    private Color color;

    public Entity(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}

