package gui.ui;

import game.model.GameObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


import game.model.GameObject;
import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ArrayList<GameObject> gameObjects;
    private final String saveName;

    public GameState(ArrayList<GameObject> gameObjects, String saveName) {
        this.gameObjects = new ArrayList<>(gameObjects);
        this.saveName = saveName;
    }

    public ArrayList<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    public String getSaveName() {
        return saveName;
    }
}