package gui.system.profiling;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, FrameState> frameStates = new HashMap<>();
    private String language;
    private String profileName;

    public Profile(String profileName, String language) {
        this.profileName = profileName;
        this.language = language;
    }

    public void setFrameState(String frameId, FrameState state) {
        frameStates.put(frameId, state);
    }

    public FrameState getFrameState(String frameId) {
        return frameStates.get(frameId);
    }

    public Map<String, FrameState> getFrameStates() {
        return frameStates;
    }

    public String getLanguage() {
        return language;
    }

    public String getProfileName() {
        return profileName;
    }

    public static class FrameState implements Serializable {
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
}
