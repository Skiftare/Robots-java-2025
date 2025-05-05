package gui.system.profiling;

import lombok.Getter;

import java.awt.Rectangle;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Profile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, FrameState> frameStates = new HashMap<>();
    private final String language;
    private final String profileName;

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
}
