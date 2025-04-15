package game.model.formula;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import game.model.ObjectProperty;
import game.model.formula.FormulaElement;

class FormulaElementTest {

    @Test
    void testConstructorAndGetters() {
        FormulaElement noun = new FormulaElement(5, 7, null,
                FormulaElement.ElementType.NOUN, "box");

        assertEquals(5, noun.getPosition()[0]);
        assertEquals(7, noun.getPosition()[1]);
        assertEquals(FormulaElement.ElementType.NOUN, noun.getElementType());
        assertEquals("box", noun.getValue());
        assertNull(noun.getCorrespondingProperty());
        assertTrue(noun.hasProperty(ObjectProperty.PUSHABLE));
    }

    @Test
    void testPropertyElement() {
        FormulaElement property = new FormulaElement(5, 7, null,
                FormulaElement.ElementType.PROPERTY, "STOP", ObjectProperty.STOP);

        assertEquals(FormulaElement.ElementType.PROPERTY, property.getElementType());
        assertEquals("STOP", property.getValue());
        assertEquals(ObjectProperty.STOP, property.getCorrespondingProperty());
    }

    @Test
    void testPropertyManagement() {
        FormulaElement verb = new FormulaElement(5, 7, null,
                FormulaElement.ElementType.VERB, "IS");

        // Initial property check
        assertTrue(verb.hasProperty(ObjectProperty.PUSHABLE));
        assertFalse(verb.hasProperty(ObjectProperty.STOP));

        // Add property
        verb.addProperty(ObjectProperty.STOP);
        assertTrue(verb.hasProperty(ObjectProperty.STOP));

        // Remove property
        verb.removeProperty(ObjectProperty.STOP);
        assertFalse(verb.hasProperty(ObjectProperty.STOP));

        // Clear properties
        verb.clearProperties();
        assertFalse(verb.hasProperty(ObjectProperty.PUSHABLE));
    }
}