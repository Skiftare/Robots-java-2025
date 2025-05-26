package game.mods;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Responsible for loading mod JARs
 */
public class ModLoader {
    // In ModLoader class where you load mods
    public IMod loadMod(Path jarPath) throws IOException {
        URL url = jarPath.toUri().toURL();
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{url},
                getClass().getClassLoader()  // Use parent classloader
        );

        try {
            // Look for mod.properties or similar to identify the main mod class
            JarFile jarFile = new JarFile(jarPath.toFile());
            Enumeration<JarEntry> entries = jarFile.entries();

            // Find main class that implements IMod
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");

                    try {
                        Class<?> loadedClass = classLoader.loadClass(className);
                        if (IMod.class.isAssignableFrom(loadedClass) &&
                                !Modifier.isAbstract(loadedClass.getModifiers()) &&
                                !loadedClass.isInterface()) {

                            // Found a concrete implementation of IMod
                            Object instance = loadedClass.getDeclaredConstructor().newInstance();
                            return (IMod) instance;
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException |
                             IllegalAccessException | InstantiationException |
                             InvocationTargetException e) {
                        // Log and continue - this class might not be the mod class
                        continue;
                    }
                }
            }
            jarFile.close();
        } catch (Exception e) {
            throw new IOException("Failed to load mod: " + e.getMessage(), e);
        }

        throw new IOException("No valid mod class found in JAR");
    }
}