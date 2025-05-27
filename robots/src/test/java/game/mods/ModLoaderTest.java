package game.mods;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

class ModLoaderTest {

    private ModLoader modLoader;

    @BeforeEach
    void setUp() {
        modLoader = new ModLoader();
    }

    @Test
    void shouldFailWhenJarDoesNotExist() {
        Path nonExistentPath = Path.of("non-existent-mod.jar");

        IOException exception = assertThrows(IOException.class, () -> {
            modLoader.loadMod(nonExistentPath);
        });

        assertTrue(exception.getMessage().contains("Failed to load mod") ||
                exception.getMessage().contains("No valid mod class found"));
    }

    @Test
    void shouldLoadModFromGeneratedJar(@TempDir Path tempDir) throws IOException {
        String modSource = "package testmod;\n" +
                "import game.mods.IMod;\n" +
                "import game.mods.ModRegistry;\n" +
                "public class SimpleMod implements IMod {\n" +
                "    @Override public String getName() { return \"SimpleMod\"; }\n" +
                "    @Override public String getAuthor() { return \"Author\"; }\n" +
                "    @Override public String getVersion() { return \"1.0\"; }\n" +
                "    @Override public String getDescription() { return \"A simple test mod.\"; }\n" +
                "    @Override public void initialize(ModRegistry registry) {}\n" +
                "    public void startup() {}\n" +
                "    @Override public void shutdown() {}\n" +
                "}";

        Path srcDir = tempDir.resolve("src");
        Files.createDirectories(srcDir);
        Path modJavaFile = srcDir.resolve("SimpleMod.java");
        Files.writeString(modJavaFile, modSource);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler);

        Path classesDir = tempDir.resolve("classes");
        Files.createDirectories(classesDir);
        int compilationResult = compiler.run(null, null, null, "-d", classesDir.toString(), modJavaFile.toString());
        assertEquals(0, compilationResult, "Compilation failed");
        Path targetClassFile = classesDir.resolve("testmod").resolve("SimpleMod.class");
        assertTrue(Files.exists(targetClassFile), "Compiled class file not found in expected location.");
        Path jarFile = tempDir.resolve("TestMod.jar");
        try (FileOutputStream fos = new FileOutputStream(jarFile.toFile());
             JarOutputStream jos = new JarOutputStream(fos)) {
            ZipEntry ze = new ZipEntry("testmod/SimpleMod.class");
            jos.putNextEntry(ze);
            try (InputStream fis = Files.newInputStream(targetClassFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    jos.write(buffer, 0, len);
                }
            }
            jos.closeEntry();
        }

        IMod loadedMod = modLoader.loadMod(jarFile);

        assertNotNull(loadedMod, "Loaded mod should not be null");
        assertEquals("SimpleMod", loadedMod.getName());
        assertEquals("1.0", loadedMod.getVersion());
        assertEquals("A simple test mod.", loadedMod.getDescription());
    }

    @Test
    void shouldThrowIOExceptionIfJarContainsNoIMod(@TempDir Path tempDir) throws IOException {
        String nonModSource = "package testnomod;\n" +
                "public class NotAMod {\n" +
                "    public String getInfo() { return \"I am not a mod\"; }\n" +
                "}";

        Path srcDir = tempDir.resolve("src_nomod");
        Files.createDirectories(srcDir);
        Path nonModJavaFile = srcDir.resolve("NotAMod.java");
        Files.writeString(nonModJavaFile, nonModSource);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler);
        Path classesDir = tempDir.resolve("classes_nomod");
        Files.createDirectories(classesDir);

        int compilationResult = compiler.run(null, null, null, "-d", classesDir.toString(), nonModJavaFile.toString());
        assertEquals(0, compilationResult, "Compilation failed");

        Path targetClassFile = classesDir.resolve("testnomod").resolve("NotAMod.class");
        assertTrue(Files.exists(targetClassFile), "Compiled class file not found.");


        Path jarFile = tempDir.resolve("NoMod.jar");
        try (FileOutputStream fos = new FileOutputStream(jarFile.toFile());
             JarOutputStream jos = new JarOutputStream(fos)) {
            ZipEntry ze = new ZipEntry("testnomod/NotAMod.class");
            jos.putNextEntry(ze);
            try (InputStream fis = Files.newInputStream(targetClassFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    jos.write(buffer, 0, len);
                }
            }
            jos.closeEntry();
        }

        // assert IOException
        IOException exception = assertThrows(IOException.class, () -> {
            modLoader.loadMod(jarFile);
        });
        assertEquals("No valid mod class found in JAR", exception.getMessage());
    }
}