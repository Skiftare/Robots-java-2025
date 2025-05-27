package game.mods;

import game.mods.extensions_points.IBackgroundProvider;
import game.mods.extensions_points.IControlAdapter;
import game.mods.extensions_points.IGameMechanic;
import game.mods.extensions_points.IRenderAdapter;

import java.util.List;


public class ModRegistry {
    private final ModManager manager;

    ModRegistry(ModManager manager) {
        this.manager = manager;
    }

    public void registerBackgroundProvider(IBackgroundProvider provider) {
        manager.registerBackgroundProvider(provider);
    }

    public void registerControlAdapter(IControlAdapter adapter) {
        manager.registerControlAdapter(adapter);
    }

    public void registerRenderAdapter(IRenderAdapter adapter) {
        manager.registerRenderAdapter(adapter);
    }

    public void registerGameMechanic(IGameMechanic mechanic) {
        manager.registerGameMechanic(mechanic);
    }
}