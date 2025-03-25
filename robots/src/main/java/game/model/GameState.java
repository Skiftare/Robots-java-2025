package game.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ArrayList<GameObject> gameObjects;
    @Getter
    private final String saveName;

    public GameState(ArrayList<GameObject> gameObjects, String saveName) {
        this.gameObjects = new ArrayList<>(gameObjects);
        this.saveName = saveName;
    }

    public ArrayList<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }


}