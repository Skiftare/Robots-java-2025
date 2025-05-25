package game.model.formula;

import gui.system.localization.LocalizationManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import game.model.ObjectProperty;
import game.model.formula.FormulaElement;
import game.model.formula.FormulaElementFactory;

class FormulaElementFactoryTest {

    @Test
    void testCreateNoun() {
        FormulaElement noun = FormulaElementFactory.createNoun(3, 4, "wall");

        assertEquals(3, noun.getPosition()[0]);
        assertEquals(4, noun.getPosition()[1]);
        assertEquals(FormulaElement.ElementType.NOUN, noun.getElementType());
        assertEquals("wall", noun.getValue());
        assertNull(noun.getCorrespondingProperty());
    }

    @Test
    void testCreateVerb() {
        FormulaElement verb = FormulaElementFactory.createVerb(5, 6);

        assertEquals(5, verb.getPosition()[0]);
        assertEquals(6, verb.getPosition()[1]);
        assertEquals(FormulaElement.ElementType.VERB, verb.getElementType());
        assertEquals(LocalizationManager.getInstance().getString("game.formula.is"), verb.getValue());
    }

    @Test
    void testCreateProperty() {
        FormulaElement prop = FormulaElementFactory.createProperty(7, 8, ObjectProperty.WIN);

        assertEquals(7, prop.getPosition()[0]);
        assertEquals(8, prop.getPosition()[1]);
        assertEquals(FormulaElement.ElementType.PROPERTY, prop.getElementType());
        assertEquals(LocalizationManager.getInstance().getString("game.formula.win"), prop.getValue());
        assertEquals(ObjectProperty.WIN, prop.getCorrespondingProperty());
    }

    @Test
    void testPropertyTextMapping() {
        assertEquals(ObjectProperty.PLAYER, FormulaElementFactory.getPropertyFromText(LocalizationManager.getInstance().getString("game.formula.you")));
        assertEquals(ObjectProperty.PUSHABLE, FormulaElementFactory.getPropertyFromText(LocalizationManager.getInstance().getString("game.formula.push")));
        assertEquals(ObjectProperty.STOP, FormulaElementFactory.getPropertyFromText(LocalizationManager.getInstance().getString("game.formula.stop")));
        assertEquals(ObjectProperty.WIN, FormulaElementFactory.getPropertyFromText(LocalizationManager.getInstance().getString("game.formula.win")));
        assertEquals(ObjectProperty.KILL, FormulaElementFactory.getPropertyFromText(LocalizationManager.getInstance().getString("game.formula.kill")));

        // Case insensitivity
        assertEquals(ObjectProperty.WIN, FormulaElementFactory.getPropertyFromText(LocalizationManager.getInstance().getString("game.formula.win")));

        // Invalid text
        assertNull(FormulaElementFactory.getPropertyFromText("INVALID"));
    }
}