package game.model;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<GameObject> gameObjects;
    private final String saveName;

    // Window state fields
    private int windowWidth;
    private int windowHeight;
    private int windowX;
    private int windowY;
    private int windowExtendedState;
    private int zOrder; // Added for compatibility with Profile.FrameState
    private boolean isVisible = true; // Default to visible

    public GameState(List<GameObject> gameObjects, String saveName) {
        this.gameObjects = gameObjects;
        this.saveName = saveName;
    }

    public GameState(List<GameObject> gameObjects, String saveName,
                     int windowWidth, int windowHeight, int windowX, int windowY,
                     int windowExtendedState) {
        this.gameObjects = gameObjects;
        this.saveName = saveName;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowX = windowX;
        this.windowY = windowY;
        this.windowExtendedState = windowExtendedState;
    }

    // New constructor with zOrder
    public GameState(List<GameObject> gameObjects, String saveName,
                     int windowWidth, int windowHeight, int windowX, int windowY,
                     int windowExtendedState, int zOrder, boolean isVisible) {
        this.gameObjects = gameObjects;
        this.saveName = saveName;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowX = windowX;
        this.windowY = windowY;
        this.windowExtendedState = windowExtendedState;
        this.zOrder = zOrder;
        this.isVisible = isVisible;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public String getSaveName() {
        return saveName;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowX() {
        return windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public int getWindowExtendedState() {
        return windowExtendedState;
    }

    public int getZOrder() {
        return zOrder;
    }

    public boolean isVisible() {
        return isVisible;
    }

    // Helper methods like in Profile.FrameState
    public boolean isIconified() {
        return (windowExtendedState & Frame.ICONIFIED) != 0;
    }

    public boolean isMaximized() {
        return (windowExtendedState & Frame.MAXIMIZED_BOTH) != 0;
    }
}