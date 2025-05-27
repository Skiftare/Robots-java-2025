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
import java.util.logging.Logger;

/**
 * Responsible for loading mod JARs
 */
public class ModLoader {
    // In ModLoader class where you load mods
    public IMod loadMod(Path jarPath) throws IOException {
        URL url = jarPath.toUri().toURL();
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{url},
                getClass().getClassLoader()
        );

        try {
            JarFile jarFile = new JarFile(jarPath.toFile());
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");

                    try {
                        Class<?> loadedClass = classLoader.loadClass(className);
                        if (IMod.class.isAssignableFrom(loadedClass) &&
                                !Modifier.isAbstract(loadedClass.getModifiers()) &&
                                !loadedClass.isInterface()) {
                            Object instance = loadedClass.getDeclaredConstructor().newInstance();
                            return (IMod) instance;
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException |
                             IllegalAccessException | InstantiationException |
                             InvocationTargetException e) {
                        Logger.getAnonymousLogger().info(e.getMessage());
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