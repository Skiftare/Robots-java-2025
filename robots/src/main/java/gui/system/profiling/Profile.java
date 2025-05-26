package gui.system.profiling;

import lombok.Getter;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class Profile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, FrameState> frameStates = new HashMap<>();
    private final String language;
    private final String profileName;
    // Add to robots/src/main/java/gui/system/profiling/Profile.java
    private Set<String> modPaths = new HashSet<>();  // Changed from final for serialization/deserialization

    public Set<String> getModPaths() {
        if (modPaths == null) {
            modPaths = new HashSet<>();
        }
        return new HashSet<>(modPaths);
    }

    public void addModPath(String path) {
        if (modPaths == null) {
            modPaths = new HashSet<>();
        }
        modPaths.add(path);
    }

    public void removeModPath(String path) {
        if (modPaths == null) {
            return;
        }
        modPaths.remove(path);
    }

    public void clearModPaths() {
        if (modPaths == null) {
            modPaths = new HashSet<>();
            return;
        }
        modPaths.clear();
    }

    public Profile(String profileName, String language) {
        this.profileName = profileName;
        this.language = language;
    }

    @Getter
    private int highestLevelCompleted = 0;  // последний пройденный уровень

    public void setHighestLevelCompleted(int highestLevelCompleted) {
        if (highestLevelCompleted > this.highestLevelCompleted) {
            this.highestLevelCompleted = highestLevelCompleted;
        }
    }

    public void setFrameState(String frameId, FrameState state) {
        frameStates.put(frameId, state);
    }

    public FrameState getFrameState(String frameId) {
        return frameStates.get(frameId);
    }

    public static class FrameState implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        public Rectangle bounds;
        public boolean isIcon;
        public boolean isMaximum;
        public boolean isVisible;
        public int zOrder;


        public FrameState(Rectangle bounds, boolean isIcon, boolean isMaximum, boolean isVisible, int zOrder) {
            this.bounds = bounds;
            this.isIcon = isIcon;
            this.isMaximum = isMaximum;
            this.isVisible = isVisible;
            this.zOrder = zOrder;
        }
    }

    @Override
    public String toString() {
        return profileName;
    }

    // Update the readObject method in Profile.java
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Ensure modPaths is initialized after deserialization
        if (modPaths == null) {
            modPaths = new HashSet<>();
        }
    }
}
