package game.model.formula;

import game.model.ObjectProperty;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class FormulaElementFactory {
    private static final Map<ObjectProperty, String> propertyToText = new HashMap<>();
    private static final Map<FormulaElement.ElementType, Color> typeColors = new HashMap<>();

    static {
        // Map properties to their text representation
        propertyToText.put(ObjectProperty.PLAYER, "YOU");
        propertyToText.put(ObjectProperty.PUSHABLE, "PUSH");
        propertyToText.put(ObjectProperty.STOP, "STOP");
        propertyToText.put(ObjectProperty.WIN, "WIN");
        propertyToText.put(ObjectProperty.KILL, "KILL");

        // Define colors for different formula element types
        typeColors.put(FormulaElement.ElementType.NOUN, new Color(230, 180, 80));    // Yellow/gold
        typeColors.put(FormulaElement.ElementType.VERB, new Color(150, 200, 250));   // Light blue
        typeColors.put(FormulaElement.ElementType.PROPERTY, new Color(200, 130, 220)); // Purple
    }

    public static FormulaElement createNoun(int x, int y, String name) {
        FormulaElement element = new FormulaElement(x, y, null,
                FormulaElement.ElementType.NOUN, name);
        element.setColor(typeColors.get(FormulaElement.ElementType.NOUN));
        return element;
    }

    public static FormulaElement createVerb(int x, int y) {
        FormulaElement element = new FormulaElement(x, y, null,
                FormulaElement.ElementType.VERB, "IS");
        element.setColor(typeColors.get(FormulaElement.ElementType.VERB));
        return element;
    }

    public static FormulaElement createProperty(int x, int y, ObjectProperty property) {
        String text = propertyToText.get(property);
        FormulaElement element = new FormulaElement(x, y, null,
                FormulaElement.ElementType.PROPERTY, text, property);
        element.setColor(typeColors.get(FormulaElement.ElementType.PROPERTY));
        return element;
    }

    public static ObjectProperty getPropertyFromText(String text) {
        for (Map.Entry<ObjectProperty, String> entry : propertyToText.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(text)) {
                return entry.getKey();
            }
        }
        return null;
    }
}