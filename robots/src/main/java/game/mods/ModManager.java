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
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

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

    // Maps to track which mod registered which component
    private final Map<IBackgroundProvider, String> backgroundProviderSources = new HashMap<>();
    private final Map<IControlAdapter, String> controlAdapterSources = new HashMap<>();
    private final Map<IRenderAdapter, String> renderAdapterSources = new HashMap<>();
    private final Map<IGameMechanic, String> gameMechanicSources = new HashMap<>();

    private final ModLoader loader = new ModLoader();

    private final Map<String, String> modSourcePaths = new HashMap<>();

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
            // Store the source path
            modSourcePaths.put(mod.getName(), jarPath.toAbsolutePath().toString());

            WindowLogger.debug("Loaded mod: " + mod.getName() + " v" + mod.getVersion());

        } catch (IOException e) {
            WindowLogger.error("Failed to load mod: " + e.getMessage());
        } catch (Exception e) {
            // Catch any exceptions during mod initialization to prevent crashes
            WindowLogger.error("Error initializing mod: " + e.getMessage());
        }
    }
    // Add this method to ModManager.java after the loadMod method
    public void loadModAndStore(Path jarPath) {
        // First import the mod to our mods directory
        String storedPath = ModFileManager.importMod(jarPath);
        if (storedPath == null) {
            WindowLogger.error("Failed to store mod file: " + jarPath);
            return;
        }

        // Then load it from the stored location
        loadMod(Paths.get(storedPath));
    }

    public void unloadMod(String modName) {
        IMod mod = loadedMods.remove(modName);
        if (mod != null) {
            // Call the mod's shutdown method inside a try-catch to prevent issues
            try {
                mod.shutdown();
            } catch (Exception e) {
                WindowLogger.error("Error during mod shutdown: " + e.getMessage());
            }

            // Remove source path
            modSourcePaths.remove(modName);

            // Remove all registered components from this mod
            removeModComponents(modName);

            WindowLogger.debug("Unloaded mod: " + modName);
        }
    }

    public String getSourcePathForMod(String modName) {
        return modSourcePaths.get(modName);
    }

    private void removeModComponents(String modName) {
        // Remove background providers
        backgroundProviders.removeIf(provider -> {
            boolean shouldRemove = modName.equals(backgroundProviderSources.get(provider));
            if (shouldRemove) {
                backgroundProviderSources.remove(provider);
            }
            return shouldRemove;
        });

        // Remove control adapters
        controlAdapters.removeIf(adapter -> {
            boolean shouldRemove = modName.equals(controlAdapterSources.get(adapter));
            if (shouldRemove) {
                controlAdapterSources.remove(adapter);
            }
            return shouldRemove;
        });

        // Remove render adapters
        renderAdapters.removeIf(adapter -> {
            boolean shouldRemove = modName.equals(renderAdapterSources.get(adapter));
            if (shouldRemove) {
                renderAdapterSources.remove(adapter);
            }
            return shouldRemove;
        });

        // Remove game mechanics
        gameMechanics.removeIf(mechanic -> {
            boolean shouldRemove = modName.equals(gameMechanicSources.get(mechanic));
            if (shouldRemove) {
                gameMechanicSources.remove(mechanic);
            }
            return shouldRemove;
        });
    }

    public List<IMod> getLoadedMods() {
        return new ArrayList<>(loadedMods.values());
    }

    // Registration methods used by ModRegistry
    void registerBackgroundProvider(IBackgroundProvider provider) {
        String currentModName = getCurrentModName();
        backgroundProviders.add(provider);
        backgroundProviderSources.put(provider, currentModName);
    }

    void registerControlAdapter(IControlAdapter adapter) {
        String currentModName = getCurrentModName();
        controlAdapters.add(adapter);
        controlAdapterSources.put(adapter, currentModName);
    }

    void registerRenderAdapter(IRenderAdapter adapter) {
        String currentModName = getCurrentModName();
        renderAdapters.add(adapter);
        renderAdapterSources.put(adapter, currentModName);
    }

    void registerGameMechanic(IGameMechanic mechanic) {
        String currentModName = getCurrentModName();
        gameMechanics.add(mechanic);
        gameMechanicSources.put(mechanic, currentModName);
    }

    // Helper method to get the name of the mod currently being registered
    private String getCurrentModName() {
        // This gets the calling class through the stack trace
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (className.equals(ModRegistry.class.getName())) {
                continue;
            }

            // Find the mod that's registering the component
            for (IMod mod : loadedMods.values()) {
                try {
                    Class<?> modClass = mod.getClass();
                    if (className.startsWith(modClass.getPackage().getName())) {
                        return mod.getName();
                    }
                } catch (Exception e) {
                    // Skip if we can't determine the package
                }
            }
            break;
        }
        return "unknown";
    }

    // Public accessor methods for the game to use

    public void drawCustomBackgrounds(Graphics2D g, int width, int height, long timestamp) {
        for (IBackgroundProvider provider : backgroundProviders) {
            try {
                provider.drawBackground(g, width, height, timestamp);
            } catch (Exception e) {
                WindowLogger.error("Error in background provider: " + e.getMessage());
            }
        }
    }

    public void setupCustomControls(JComponent component) {
        for (IControlAdapter adapter : controlAdapters) {
            try {
                adapter.setupAdditionalControls(component);
            } catch (Exception e) {
                WindowLogger.error("Error in control adapter: " + e.getMessage());
            }
        }
    }

    public boolean tryCustomizeDrawing(GameObject object, Graphics2D g, int cellSize, Point start) {
        for (IRenderAdapter adapter : renderAdapters) {
            try {
                if (adapter.customizeDraw(object, g, cellSize, start)) {
                    return true;
                }
            } catch (Exception e) {
                WindowLogger.error("Error in render adapter: " + e.getMessage());
            }
        }
        return false;
    }

    public void notifyObjectDestroyed(GameObject object, MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            try {
                mechanic.onObjectDestroyed(object, handler);
            } catch (Exception e) {
                WindowLogger.error("Error in game mechanic: " + e.getMessage());
            }
        }
    }

    public void notifyBeforeMovement(MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            try {
                mechanic.beforeMovement(handler);
            } catch (Exception e) {
                WindowLogger.error("Error in game mechanic: " + e.getMessage());
            }
        }
    }

    public void notifyAfterMovement(MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            try {
                mechanic.afterMovement(handler);
            } catch (Exception e) {
                WindowLogger.error("Error in game mechanic: " + e.getMessage());
            }
        }
    }

    public void notifyFormulaProcessed(Formula formula, MovementHandler handler) {
        for (IGameMechanic mechanic : gameMechanics) {
            try {
                mechanic.onFormulaProcessed(formula, handler);
            } catch (Exception e) {
                WindowLogger.error("Error in game mechanic: " + e.getMessage());
            }
        }
    }

    public boolean interceptKeyEvent(KeyEvent e) {
        for (IControlAdapter adapter : controlAdapters) {
            try {
                if (adapter.interceptKeyEvent(e)) {
                    return true;
                }
            } catch (Exception e1) {
                WindowLogger.error("Error in control adapter: " + e1.getMessage());
            }
        }
        return false;
    }

}