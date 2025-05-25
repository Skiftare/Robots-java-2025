package game.mods;

import game.mechanic.MovementHandler;
import game.model.GameObject;
import game.model.formula.Formula;
import game.mods.extensions_points.IBackgroundProvider;
import game.mods.extensions_points.IControlAdapter;
import game.mods.extensions_points.IGameMechanic;
import game.mods.extensions_points.IRenderAdapter;
import log.WindowLogger;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central coordinator for all mod functionality
 */
public class ModManager {
    private final Map<String, IMod> loadedMods = new HashMap<>();
    @Getter
    private final List<IBackgroundProvider> backgroundProviders = new ArrayList<>();
    private final List<IControlAdapter> controlAdapters = new ArrayList<>();
    private final List<IRenderAdapter> renderAdapters = new ArrayList<>();
    private final List<IGameMechanic> gameMechanics = new ArrayList<>();

    private final ModLoader loader = new ModLoader();

    public void loadMod(Path jarPath) {
        try {
            IMod mod = loader.loadMod(jarPath);

            if (loadedMods.containsKey(mod.getName())) {
                WindowLogger.error("Mod already loaded: " + mod.getName());
                return;
            }

            // Register the mod
            ModRegistry registry = new ModRegistry(this);
            mod.initialize(registry);

            loadedMods.put(mod.getName(), mod);
            WindowLogger.debug("Loaded mod: " + mod.getName() + " v" + mod.getVersion());

        } catch (IOException e) {
            WindowLogger.error("Failed to load mod: " + e.getMessage());
        }
    }

    public void unloadMod(String modName) {
        IMod mod = loadedMods.remove(modName);
        if (mod != null) {
            mod.shutdown();

            // Remove all registered components from this mod
            // This requires adding a reference to the source mod in each component
            // which we'd need to implement

            WindowLogger.debug("Unloaded mod: " + modName);
        }
    }

    public List<IMod> getLoadedMods() {
        return new ArrayList<>(loadedMods.values());
    }

    // Registration methods used by ModRegistry
    public void registerBackgroundProvider(IBackgroundProvider provider) {
        backgroundProviders.add(provider);
    }

    public void registerControlAdapter(IControlAdapter adapter) {
        controlAdapters.add(adapter);
    }

    public void registerRenderAdapter(IRenderAdapter adapter) {
        renderAdapters.add(adapter);
    }

    public void registerGameMechanic(IGameMechanic mechanic) {
        gameMechanics.add(mechanic);
    }

    // Public accessor methods for the game to use

    public void drawCustomBackgrounds(Graphics2D g, int width, int height, long timestamp) {
        for (IBackgroundProvider provider : backgroundProviders) {
            provider.drawBackground(g, width, height, timestamp);
        }
    }

    public void setupCustomControls(JComponent component) {
        for (IControlAdapter adapter : controlAdapters) {
            adapter.setupAdditionalControls(component);
        }
    }

    public boolean tryCustomizeDrawing(GameObject object, Graphics2D g, int cellSize, Point start) {
        for (IRenderAdapter adapter : renderAdapters) {
            if (adapter.customizeDraw(object, g, cellSize, start)) {
                return true;
            }
        }
        return false;
    }

    public void notifyObjectDestroyed(GameObject object, MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            mechanic.onObjectDestroyed(object, handler);
        }
    }

    public void notifyBeforeMovement(MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            mechanic.beforeMovement(handler);
        }
    }

    public void notifyAfterMovement(MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            mechanic.afterMovement(handler);
        }
    }

    public void notifyFormulaProcessed(Formula formula, MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            mechanic.onFormulaProcessed(formula, handler);
        }
    }

    public boolean interceptKeyEvent(KeyEvent e) {
        for (IControlAdapter adapter : controlAdapters) {
            if (adapter.interceptKeyEvent(e)) {
                return true;
            }
        }
        return false;
    }
}