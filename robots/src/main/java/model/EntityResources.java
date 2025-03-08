package model;

import model.object.abstractions.Entity;
import java.awt.Color;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EntityResources {
    private static final String RESOURCE_FILE = "/entities.properties";
    private static final Map<String, EntityDefinition> definitions = new HashMap<>();
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) return;

        try (InputStream stream = EntityResources.class.getResourceAsStream(RESOURCE_FILE)) {
            Properties props = new Properties();
            if (stream != null) {
                props.load(stream);
                loadDefinitionsFromProperties(props);
            } else {
                System.err.println("Warning: Resource file not found. Using default definitions.");
                loadDefaultDefinitions();
            }
        } catch (Exception e) {
            System.err.println("Error loading entity resources: " + e.getMessage());
            loadDefaultDefinitions();
        }
        initialized = true;
    }

    private static void loadDefinitionsFromProperties(Properties props) {
        for (String key : props.stringPropertyNames()) {
            if (key.contains(".")) {
                String[] parts = key.split("\\.");
                String entityName = parts[0];
                String property = parts[1];

                EntityDefinition def = definitions.computeIfAbsent(entityName, k ->
                        new EntityDefinition(entityName));

                switch (property) {
                    case "type" -> def.type = props.getProperty(key);
                    case "color" -> def.color = parseColor(props.getProperty(key));
                    case "displayName" -> def.displayName = props.getProperty(key);
                }
            }
        }
    }

    private static void loadDefaultDefinitions() {
        definitions.put("STUDENT", new EntityDefinition("STUDENT", "NOUN", Color.BLUE));
        definitions.put("PERS", new EntityDefinition("PERS", "NOUN", Color.PINK));
        definitions.put("40", new EntityDefinition("40", "NOUN", Color.RED));
        definitions.put("60", new EntityDefinition("60", "NOUN", Color.GREEN));
        definitions.put("80", new EntityDefinition("80", "NOUN", Color.YELLOW));
        definitions.put("WIN", new EntityDefinition("WIN", "PROPERTY", Color.YELLOW));
        definitions.put("YOU", new EntityDefinition("YOU", "PROPERTY", Color.CYAN));
        definitions.put("IS", new EntityDefinition("IS", "OPERATOR", Color.WHITE));
    }

    private static Color parseColor(String colorStr) {
        try {
            if (colorStr.startsWith("#")) {
                return Color.decode(colorStr);
            } else {
                return (Color) Color.class.getField(colorStr.toUpperCase()).get(null);
            }
        } catch (Exception e) {
            return Color.GRAY;
        }
    }

    public static EntityDefinition getDefinition(String name) {
        if (!initialized) initialize();
        return definitions.get(name);
    }

    public static class EntityDefinition {
        public final String name;
        public String type;
        public Color color;
        public String displayName;

        public EntityDefinition(String name) {
            this.name = name;
            this.color = Color.GRAY;
            this.displayName = name; // Default to name if not specified
        }

        public EntityDefinition(String name, String type, Color color) {
            this.name = name;
            this.type = type;
            this.color = color;
            this.displayName = name; // Default to name if not specified
        }
    }
}