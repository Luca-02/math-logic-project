package org.mathlogic.utility;

import org.mathlogic.comparator.MultisetComparator;
import org.jetbrains.annotations.NotNull;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;

import java.util.Comparator;
import java.util.Map;

public class MaximalLiteral {
    private static final Comparator<Map<Term , Integer>> comparator = new MultisetComparator();

    public static boolean isMaximal(
            @NotNull Literal lit,
            @NotNull Clause clause,
            boolean strictlyMaximal
    ) {
        Map<Term, Integer> litMultiset = lit.getMultiset();
        for (Literal cLit : clause.getAllLiterals()) {
            if (lit.equals(cLit)) continue;

            int cmp = comparator.compare(litMultiset, cLit.getMultiset());
            if (strictlyMaximal ? cmp <= 0 : cmp < 0) {
                return false;
            }
        }
        return true;
    }
}
