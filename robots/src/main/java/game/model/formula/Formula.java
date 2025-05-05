package game.model.formula;

import game.model.ObjectProperty;

public class Formula {
    private FormulaElement subject;  // NOUN
    private FormulaElement verb;     // VERB (usually "is")
    private FormulaElement property; // PROPERTY

    public Formula(FormulaElement subject, FormulaElement verb, FormulaElement property) {
        this.subject = subject;
        this.verb = verb;
        this.property = property;
    }

    public String getSubjectValue() {
        return subject.getValue();
    }

    public ObjectProperty getPropertyValue() {
        return property.getCorrespondingProperty();
    }

    public boolean isValid() {
        return subject != null &&
                verb != null &&
                property != null &&
                subject.getElementType() == FormulaElement.ElementType.NOUN &&
                verb.getElementType() == FormulaElement.ElementType.VERB &&
                property.getElementType() == FormulaElement.ElementType.PROPERTY;
    }
}