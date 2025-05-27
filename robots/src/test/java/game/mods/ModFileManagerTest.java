package game.mods;

import log.WindowLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class ModFileManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void getModsDirectory_shouldCreateDirectoryIfNotExists() throws IOException {
        Path projectRootModsDir = ModFileManager.getModsDirectory(); // This will use the static "mods"

        assertTrue(Files.exists(projectRootModsDir), "Mods directory should be created.");
        assertTrue(Files.isDirectory(projectRootModsDir), "Mods directory should be a directory.");

        if (Files.exists(projectRootModsDir) && projectRootModsDir.startsWith(Paths.get("").toAbsolutePath()) && projectRootModsDir.endsWith("mods")) {
            Files.walk(projectRootModsDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }


    @Test
    void importMod_shouldCopyFileToModsDirectory() throws IOException {
        Path sourceFile = Files.createFile(tempDir.resolve("myMod.jar"));
        Files.writeString(sourceFile, "mod content");
        Path projectRootModsDir = Paths.get(ModFileManager.MODS_DIR);
        if (Files.exists(projectRootModsDir)) {
            Files.walk(projectRootModsDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
        assertFalse(Files.exists(projectRootModsDir), "Project root mods directory should be clean before importMod test.");


        String importedPathString = ModFileManager.importMod(sourceFile);
        assertNotNull(importedPathString, "Imported path should not be null.");

        Path importedPath = Paths.get(importedPathString);
        assertTrue(Files.exists(importedPath), "Imported mod file should exist in mods directory.");
        assertEquals(sourceFile.getFileName().toString(), importedPath.getFileName().toString(), "File name should be the same.");
        assertEquals(Files.readString(sourceFile), Files.readString(importedPath), "File content should be the same.");
        assertTrue(importedPath.startsWith(projectRootModsDir), "Imported file should be in the project's mods directory.");

        if (Files.exists(projectRootModsDir)) {
            Files.walk(projectRootModsDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }

    @Test
    void importMod_shouldReturnPathIfFileAlreadyInModsDirectoryAndIdentical() throws IOException {
        Path modsDir = ModFileManager.getModsDirectory();
        Path existingModFile = modsDir.resolve("alreadyExists.jar");
        Files.createFile(existingModFile);
        Files.writeString(existingModFile, "existing content");

        String importedPathString = ModFileManager.importMod(existingModFile);
        assertNotNull(importedPathString);
        assertEquals(existingModFile.toString(), importedPathString, "Should return the same path if file is already in mods dir.");

        if (Files.exists(modsDir)) {
            Files.walk(modsDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }

    @Test
    void importMod_shouldReplaceIfFileInModsDirectoryIsDifferent() throws IOException {
        Path modsDir = ModFileManager.getModsDirectory();
        Path modFileInTemp = Files.createFile(tempDir.resolve("myModToImport.jar"));
        Files.writeString(modFileInTemp, "new content");

        Path targetModFileInModsDir = modsDir.resolve("myModToImport.jar");
        Files.createFile(targetModFileInModsDir);
        Files.writeString(targetModFileInModsDir, "old content"); // Different content

        String importedPathString = ModFileManager.importMod(modFileInTemp);
        assertNotNull(importedPathString);
        Path importedPath = Paths.get(importedPathString);

        assertTrue(Files.exists(importedPath), "Imported mod file should exist.");
        assertEquals("new content", Files.readString(importedPath), "File content should be updated to new content.");

        if (Files.exists(modsDir)) {
            Files.walk(modsDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }


    @Test
    void importMod_shouldReturnNullForNonExistentSourceFile() {
        Path nonExistentSourceFile = tempDir.resolve("nonExistent.jar");
        // Mock WindowLogger.error to verify it's called
        try (var mockedStatic = mockStatic(WindowLogger.class)) {
            String importedPathString = ModFileManager.importMod(nonExistentSourceFile);
            assertNull(importedPathString, "Imported path should be null for non-existent source.");
            mockedStatic.verify(() -> WindowLogger.error("Invalid mod source path"), times(1));
        }
    }

    @Test
    void importMod_shouldReturnNullForNullSourcePath() {
        try (var mockedStatic = mockStatic(WindowLogger.class)) {
            String importedPathString = ModFileManager.importMod(null);
            assertNull(importedPathString, "Imported path should be null for null source path.");
            mockedStatic.verify(() -> WindowLogger.error("Invalid mod source path"), times(1));
        }
    }
}