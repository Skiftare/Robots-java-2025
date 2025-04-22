package gui.system.profiling;

import game.model.GameState;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Profile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, GameState> frameStates = new HashMap<>();
    private final String language;
    private final String profileName;

    public Profile(String profileName, String language) {
        this.profileName = profileName;
        this.language = language;
    }

    public void setFrameState(String frameId, GameState state) {
        frameStates.put(frameId, state);
    }

    public GameState getFrameState(String frameId) {
        return frameStates.get(frameId);
    }

    // Utility method to apply profile to windows
    public static void applyProfileToWindows(Profile profile, Map<String, JFrame> frames) {
        if (profile == null) return;

        for (Map.Entry<String, JFrame> entry : frames.entrySet()) {
            String frameId = entry.getKey();
            JFrame frame = entry.getValue();

            GameState frameState = profile.getFrameState(frameId);
            if (frameState != null) {
                // First reset to normal
                frame.setExtendedState(Frame.NORMAL);

                // Set size and position
                frame.setBounds(
                        frameState.getWindowX(),
                        frameState.getWindowY(),
                        frameState.getWindowWidth(),
                        frameState.getWindowHeight()
                );
                frame.setVisible(frameState.isVisible());

                // Apply maximized state first if needed
                if (frameState.isMaximized()) {
                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                }

                // Apply iconified state in a separate invokeLater if needed
                if (frameState.isIconified()) {
                    SwingUtilities.invokeLater(() -> {
                        int currentState = frame.getExtendedState();
                        frame.setExtendedState(currentState | Frame.ICONIFIED);
                    });
                }
            }
        }
    }

    @Override
    public String toString() {
        return profileName;
    }
}