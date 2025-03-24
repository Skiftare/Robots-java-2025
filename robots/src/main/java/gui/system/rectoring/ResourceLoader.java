package gui.system.rectoring;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
    private static ResourceLoader instance;
    private final Map<String, Image> imageCache = new HashMap<>();

    private ResourceLoader() {}

    public static ResourceLoader getInstance() {
        if (instance == null) {
            instance = new ResourceLoader();
        }
        return instance;
    }

    public Image loadImage(String path) {
        if (path == null) return null;

        // Return from cache if available
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            Image image = ImageIO.read(new File(path));
            imageCache.put(path, image);
            return image;
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            return null;
        }
    }
}