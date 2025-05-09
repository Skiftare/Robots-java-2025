package game.mechanic;

import game.model.GameObject;
import game.model.ObjectProperty;
import game.model.formula.Formula;
import game.model.formula.FormulaElement;

import java.util.*;

public class FormulaHandler {
    private final MovementHandler movementHandler;
    private final List<Formula> activeFormulas = new ArrayList<>();

    public FormulaHandler(MovementHandler movementHandler) {
        this.movementHandler = movementHandler;
    }

    public boolean processFormulas() {
        List<Formula> oldFormulas = new ArrayList<>(activeFormulas);
        activeFormulas.clear();

        // Find all formula elements
        List<FormulaElement> elements = findFormulaElements();
        if (elements.isEmpty()) {
            return false;
        }

        // Find valid formulas
        findFormulas(elements);

        // Check if formulas changed
        boolean formulasChanged = !compareFormulas(oldFormulas, activeFormulas);

        // Apply formula properties
        boolean propertiesChanged = applyFormulas();

        return formulasChanged || propertiesChanged;
    }

    private List<FormulaElement> findFormulaElements() {
        List<FormulaElement> elements = new ArrayList<>();
        for (GameObject obj : movementHandler.getGameObjects()) {
            if (obj instanceof FormulaElement) {
                elements.add((FormulaElement) obj);
            }
        }
        return elements;
    }

    private void findFormulas(List<FormulaElement> elements) {
        findDirectionalFormulas(elements, true);  // horizontal
        findDirectionalFormulas(elements, false); // vertical
    }

    private void findDirectionalFormulas(List<FormulaElement> elements, boolean horizontal) {
        // Group elements by row/column
        List<List<FormulaElement>> groupedElements = new ArrayList<>();

        if (horizontal) {
            // Group by Y coordinate (row)
            for (FormulaElement element : elements) {
                int y = element.getPosition()[1];

                // Find or create list for this row
                List<FormulaElement> row = null;
                for (List<FormulaElement> existingRow : groupedElements) {
                    if (!existingRow.isEmpty() && existingRow.get(0).getPosition()[1] == y) {
                        row = existingRow;
                        break;
                    }
                }

                if (row == null) {
                    row = new ArrayList<>();
                    groupedElements.add(row);
                }

                row.add(element);
            }

            // Sort each row by X coordinate
            for (List<FormulaElement> row : groupedElements) {
                row.sort((e1, e2) -> Integer.compare(e1.getPosition()[0], e2.getPosition()[0]));
            }
        } else {
            // Group by X coordinate (column)
            for (FormulaElement element : elements) {
                int x = element.getPosition()[0];

                // Find or create list for this column
                List<FormulaElement> column = null;
                for (List<FormulaElement> existingColumn : groupedElements) {
                    if (!existingColumn.isEmpty() && existingColumn.get(0).getPosition()[0] == x) {
                        column = existingColumn;
                        break;
                    }
                }

                if (column == null) {
                    column = new ArrayList<>();
                    groupedElements.add(column);
                }

                column.add(element);
            }

            // Sort each column by Y coordinate
            for (List<FormulaElement> column : groupedElements) {
                column.sort((e1, e2) -> Integer.compare(e1.getPosition()[1], e2.getPosition()[1]));
            }
        }

        // Check for formulas in each row/column
        for (List<FormulaElement> group : groupedElements) {
            for (int i = 0; i <= group.size() - 3; i++) {
                FormulaElement e1 = group.get(i);
                FormulaElement e2 = group.get(i + 1);
                FormulaElement e3 = group.get(i + 2);

                // Check if they're adjacent
                boolean adjacent;
                if (horizontal) {
                    adjacent = e2.getPosition()[0] == e1.getPosition()[0] + 1 &&
                            e3.getPosition()[0] == e2.getPosition()[0] + 1;
                } else {
                    adjacent = e2.getPosition()[1] == e1.getPosition()[1] + 1 &&
                            e3.getPosition()[1] == e2.getPosition()[1] + 1;
                }

                if (adjacent) {
                    Formula formula = new Formula(e1, e2, e3);
                    if (formula.isValid()) {
                        activeFormulas.add(formula);
                    }
                }
            }
        }
    }

    private boolean compareFormulas(List<Formula> list1, List<Formula> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (Formula f1 : list1) {
            boolean found = false;
            for (Formula f2 : list2) {
                if (f1.getSubjectValue().equals(f2.getSubjectValue()) &&
                        f1.getPropertyValue() == f2.getPropertyValue()) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        return true;
    }

    private boolean applyFormulas() {
        boolean propertiesChanged = false;

        // First, ensure all formula elements only have PUSHABLE property
        for (GameObject obj : movementHandler.getGameObjects()) {
            if (obj instanceof FormulaElement) {
                boolean hadDifferentProps = obj.hasProperty(ObjectProperty.PLAYER) ||
                        obj.hasProperty(ObjectProperty.STOP) ||
                        obj.hasProperty(ObjectProperty.WIN) ||
                        obj.hasProperty(ObjectProperty.KILL);

                obj.clearProperties();
                obj.addProperty(ObjectProperty.PUSHABLE);

                if (hadDifferentProps) {
                    propertiesChanged = true;
                }
            }
        }

        // Create a map to track which objects should have which formula-defined properties
        Map<GameObject, Set<ObjectProperty>> newPropertiesMap = new HashMap<>();

        // Determine which properties each object should have based on current formulas
        for (Formula formula : activeFormulas) {
            String targetType = formula.getSubjectValue();
            ObjectProperty property = formula.getPropertyValue();

            if (property == null) continue;

            for (GameObject obj : movementHandler.getGameObjects()) {
                // Skip formula elements
                if (obj instanceof FormulaElement) {
                    continue;
                }

                // If object matches the formula's subject
                if (obj.getType().equalsIgnoreCase(targetType) ||
                        obj.getLabel().equalsIgnoreCase(targetType)) {
                    newPropertiesMap.computeIfAbsent(obj, k -> new HashSet<>()).add(property);
                }
            }
        }

        // Apply changes: add/remove properties
        for (GameObject obj : movementHandler.getGameObjects()) {
            // Skip formula elements
            if (obj instanceof FormulaElement) {
                continue;
            }

            // Get properties this object should have
            Set<ObjectProperty> newProperties = newPropertiesMap.getOrDefault(obj, Collections.emptySet());

            // Add missing properties
            for (ObjectProperty prop : newProperties) {
                if (!obj.hasProperty(prop)) {
                    obj.addProperty(prop);
                    propertiesChanged = true;
                }
            }

            // Remove properties that are not in the new set (only formula-defined ones)
            for (ObjectProperty prop : ObjectProperty.values()) {
                if (isFormulaDefined(prop) && obj.hasProperty(prop) && !newProperties.contains(prop)) {
                    obj.removeProperty(prop);
                    propertiesChanged = true;
                }
            }
        }

        return propertiesChanged;
    }

    // Helper method to determine if a property is typically set by formulas
    private boolean isFormulaDefined(ObjectProperty prop) {
        // These are the properties usually defined by formulas
        return prop == ObjectProperty.PLAYER ||
                prop == ObjectProperty.STOP ||
                prop == ObjectProperty.WIN ||
                prop == ObjectProperty.KILL;
    }

    public List<Formula> getActiveFormulas() {
        return new ArrayList<>(activeFormulas);
    }
}