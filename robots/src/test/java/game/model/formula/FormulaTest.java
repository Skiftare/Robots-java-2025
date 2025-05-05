package game.model.formula;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import game.model.ObjectProperty;
import game.model.formula.Formula;
import game.model.formula.FormulaElement;
import game.model.formula.FormulaElementFactory;

class FormulaTest {

    @Test
    void testValidFormula() {
        FormulaElement subject = FormulaElementFactory.createNoun(1, 1, "box");
        FormulaElement verb = FormulaElementFactory.createVerb(2, 1);
        FormulaElement property = FormulaElementFactory.createProperty(3, 1, ObjectProperty.STOP);

        Formula formula = new Formula(subject, verb, property);

        assertTrue(formula.isValid());
        assertEquals("box", formula.getSubjectValue());
        assertEquals(ObjectProperty.STOP, formula.getPropertyValue());
    }

    @Test
    void testInvalidFormula() {
        // Wrong order: verb, noun, property
        FormulaElement verb = FormulaElementFactory.createVerb(1, 1);
        FormulaElement subject = FormulaElementFactory.createNoun(2, 1, "box");
        FormulaElement property = FormulaElementFactory.createProperty(3, 1, ObjectProperty.STOP);

        Formula formula = new Formula(verb, subject, property);

        assertFalse(formula.isValid());
    }

    @Test
    void testNullElements() {
        FormulaElement subject = FormulaElementFactory.createNoun(1, 1, "box");
        FormulaElement verb = FormulaElementFactory.createVerb(2, 1);

        Formula formula = new Formula(subject, verb, null);

        assertFalse(formula.isValid());
    }
}