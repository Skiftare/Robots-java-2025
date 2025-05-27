package game.mods;

import log.WindowLogger;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages the storage and retrieval of mod files
 */
public class ModFileManager {
    static final String MODS_DIR = "mods";

    /**
     * Ensures the mods directory exists
     */
    public static Path getModsDirectory() {
        Path modsPath = Paths.get(MODS_DIR);
        try {
            if (!Files.exists(modsPath)) {
                Files.createDirectories(modsPath);
            }
            return modsPath;
        } catch (IOException e) {
            WindowLogger.error("Failed to create mods directory: " + e.getMessage());
            return modsPath;
        }
    }


    public static String importMod(Path sourcePath) {
        if (sourcePath == null || !Files.exists(sourcePath)) {
            WindowLogger.error("Invalid mod source path");
            return null;
        }

        try {
            String fileName = sourcePath.getFileName().toString();
            Path targetPath = getModsDirectory().resolve(fileName);

            if (Files.exists(targetPath) && Files.isSameFile(sourcePath, targetPath)) {
                return targetPath.toString();
            }

            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            WindowLogger.debug("Imported mod to: " + targetPath);
            return targetPath.toString();
        } catch (IOException e) {
            WindowLogger.error("Failed to copy mod file: " + e.getMessage());
            return null;
        }
    }
}