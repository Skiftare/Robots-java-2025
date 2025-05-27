package game.mods;

import game.mods.extensions_points.IBackgroundProvider;
import game.mods.extensions_points.IControlAdapter;
import game.mods.extensions_points.IGameMechanic;
import game.mods.extensions_points.IRenderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModManagerTest {

    private ModManager modManager;
    private IMod mockMod;

    @BeforeEach
    void setUp() {
        modManager = new ModManager();
        mockMod = mock(IMod.class);

        when(mockMod.getName()).thenReturn("TestMod");
        when(mockMod.getVersion()).thenReturn("1.0");
    }

    @Test
    void shouldRegisterBackgroundProvider() {
        IBackgroundProvider provider = mock(IBackgroundProvider.class);
        try {
            java.lang.reflect.Method method = ModManager.class.getDeclaredMethod(
                    "registerBackgroundProvider", IBackgroundProvider.class);
            method.setAccessible(true);
            method.invoke(modManager, provider);
            List<IBackgroundProvider> providers = modManager.getBackgroundProviders();
            assertTrue(providers.contains(provider));
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    @Test
    void shouldUnloadModAndRemoveComponents() {
        IBackgroundProvider mockProvider = mock(IBackgroundProvider.class);

        try {
            java.lang.reflect.Field modsField = ModManager.class.getDeclaredField("loadedMods");
            modsField.setAccessible(true);
            ((java.util.Map<String, IMod>)modsField.get(modManager)).put("TestMod", mockMod);

            java.lang.reflect.Method method = ModManager.class.getDeclaredMethod(
                    "registerBackgroundProvider", IBackgroundProvider.class);
            method.setAccessible(true);
            method.invoke(modManager, mockProvider);

            java.lang.reflect.Field sourcesField = ModManager.class.getDeclaredField("backgroundProviderSources");
            sourcesField.setAccessible(true);
            ((java.util.Map<IBackgroundProvider, String>)sourcesField.get(modManager)).put(mockProvider, "TestMod");

            modManager.unloadMod("TestMod");

            assertFalse(modManager.getLoadedMods().contains(mockMod));

            assertFalse(modManager.getBackgroundProviders().contains(mockProvider));

            verify(mockMod).shutdown();
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }
}