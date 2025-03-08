package model.ui;

import model.object.abstractions.Entity;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private List<Entity> entities = new ArrayList<>();

    public void addEntity(Entity entity) {
        if (entity != null) {
            entities.add(entity);
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    public boolean hasEntityType(Class<? extends Entity> type) {
        return entities.stream().anyMatch(type::isInstance);
    }
}