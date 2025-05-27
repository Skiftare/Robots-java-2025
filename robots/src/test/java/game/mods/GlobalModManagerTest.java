package game.mods;

import gui.MainApplicationFrame;
import gui.system.profiling.Profile;
import gui.ui.drawing.GameVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir; // Import @TempDir
import org.mockito.ArgumentCaptor;

import java.io.IOException; // Import IOException
import java.nio.file.Files; // Import Files
import java.nio.file.Path;
// import java.nio.file.Paths; // Not strictly needed for this change
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalModManagerTest {

    private GlobalModManager globalModManagerInstance;
    private ModManager mockInternalModManager;
    private MainApplicationFrame mockMainFrame;
    private Profile mockProfile;

    @BeforeEach
    void setUp() throws Exception {
        globalModManagerInstance = GlobalModManager.getInstance();

        mockInternalModManager = mock(ModManager.class);
        java.lang.reflect.Field field = GlobalModManager.class.getDeclaredField("modManager");
        field.setAccessible(true);
        field.set(globalModManagerInstance, mockInternalModManager);

        mockMainFrame = mock(MainApplicationFrame.class);
        mockProfile = mock(Profile.class);
        globalModManagerInstance.setMainApplicationFrame(mockMainFrame);
    }

    @Test
    void shouldLoadModsFromProfile(@TempDir Path tempDir) throws IOException {
        // Given
        Path mod1File = tempDir.resolve("mod1.jar");
        Path mod2File = tempDir.resolve("mod2.jar");
        Files.createFile(mod1File);
        Files.createFile(mod2File);

        Set<String> modPaths = new HashSet<>();
        modPaths.add(mod1File.toString());
        modPaths.add(mod2File.toString());
        when(mockProfile.getModPaths()).thenReturn(modPaths);

        // When
        globalModManagerInstance.loadModsFromProfile(mockProfile);
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(mockInternalModManager, times(2)).loadModAndStore(pathCaptor.capture());

        List<Path> capturedPaths = pathCaptor.getAllValues();
        assertTrue(capturedPaths.contains(mod1File.toAbsolutePath()), "Captured paths should contain mod1File");
        assertTrue(capturedPaths.contains(mod2File.toAbsolutePath()), "Captured paths should contain mod2File");
    }

    @Test
    void shouldSaveModsToProfile() {
        // Given
        String modName1 = "Mod1";
        String modName2 = "Mod2";
        IMod mockMod1 = mock(IMod.class);
        IMod mockMod2 = mock(IMod.class);
        when(mockMod1.getName()).thenReturn(modName1);
        when(mockMod2.getName()).thenReturn(modName2);

        List<IMod> mods = List.of(mockMod1, mockMod2);
        when(mockInternalModManager.getLoadedMods()).thenReturn(mods);
        when(mockInternalModManager.getSourcePathForMod(modName1)).thenReturn("/path/to/mod1.jar");
        when(mockInternalModManager.getSourcePathForMod(modName2)).thenReturn("/path/to/mod2.jar");

        globalModManagerInstance.saveModsToProfile(mockProfile);

        verify(mockProfile).clearModPaths();
        verify(mockProfile).addModPath("/path/to/mod1.jar");
        verify(mockProfile).addModPath("/path/to/mod2.jar");
    }

    @Test
    void shouldApplyToGameVisualizer() {
        GameVisualizer mockVisualizer = mock(GameVisualizer.class);
        globalModManagerInstance.applyToGameVisualizer(mockVisualizer);
        verify(mockVisualizer).setModManager(mockInternalModManager);
    }

    @Test
    void loadModsFromProfileShouldHandleNullProfile() {
        globalModManagerInstance.loadModsFromProfile(null);
        verify(mockInternalModManager, never()).loadModAndStore(any(Path.class));
    }

    @Test
    void loadModsFromProfileShouldHandleNullModPaths() {
        when(mockProfile.getModPaths()).thenReturn(null);
        globalModManagerInstance.loadModsFromProfile(mockProfile);
        verify(mockInternalModManager, never()).loadModAndStore(any(Path.class));
    }

    @Test
    void loadModsFromProfileShouldSkipNonExistentFiles(@TempDir Path tempDir) {
        String nonExistentModPath = tempDir.resolve("non-existent-mod.jar").toString();
        Set<String> modPaths = new HashSet<>();
        modPaths.add(nonExistentModPath);
        when(mockProfile.getModPaths()).thenReturn(modPaths);
        globalModManagerInstance.loadModsFromProfile(mockProfile);
        verify(mockInternalModManager, never()).loadModAndStore(any(Path.class));
    }
}