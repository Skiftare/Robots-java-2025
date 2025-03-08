package gui.system.processing;

import lombok.Getter;
import model.ui.Cell;
import model.ui.Grid;
import model.object.NounWord;
import model.object.OperatorWord;
import model.object.PropertyWord;
import model.object.abstractions.Entity;

import java.util.ArrayList;
import java.util.List;

public class FormulaEvaluator {
    private final Grid grid;

    public FormulaEvaluator(Grid grid) {
        this.grid = grid;
    }

    public List<Formula> evaluateFormulas() {
        List<Formula> formulas = new ArrayList<>();

        // Find all horizontal formulas
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth() - 2; x++) {
                findFormulaAt(x, y, 1, 0, formulas);
            }
        }

        // Find all vertical formulas
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight() - 2; y++) {
                findFormulaAt(x, y, 0, 1, formulas);
            }
        }


        return formulas;
    }

    private void findFormulaAt(int x, int y, int dx, int dy, List<Formula> formulas) {
        Cell cell1 = grid.getCell(x, y);
        Cell cell2 = grid.getCell(x + dx, y + dy);
        Cell cell3 = grid.getCell(x + 2*dx, y + 2*dy);

        if (cell1 == null || cell2 == null || cell3 == null) return;

        for (Entity e1 : cell1.getEntities()) {
            if (e1 instanceof NounWord) {
                for (Entity e2 : cell2.getEntities()) {
                    if (e2 instanceof OperatorWord && "IS".equals(e2.getName())) {
                        for (Entity e3 : cell3.getEntities()) {
                            if (e3 instanceof PropertyWord) {
                                formulas.add(new Formula(e1.getName(), e3.getName()));
                            }
                        }
                    }
                }
            }
        }
    }

    @Getter
    public static class Formula {
        private final String subject;
        private final String property;

        public Formula(String subject, String property) {
            this.subject = subject;
            this.property = property;
        }

    }
}