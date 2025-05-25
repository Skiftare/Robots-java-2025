package game.mods;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarFile;

/**
 * Responsible for loading mod JARs
 */
public class ModLoader {
    public IMod loadMod(Path jarPath) throws IOException {
        URL jarUrl = jarPath.toUri().toURL();

        // Create isolated class loader for this mod
        try (URLClassLoader classLoader = new URLClassLoader(
                new URL[] { jarUrl },
                getClass().getClassLoader())) {

            // Use ServiceLoader to find implementations of IMod
            ServiceLoader<IMod> serviceLoader = ServiceLoader.load(IMod.class, classLoader);

            for (IMod mod : serviceLoader) {
                return mod; // Return the first mod found
            }

            throw new IOException("No IMod implementation found in JAR: " + jarPath);
        }
    }
}