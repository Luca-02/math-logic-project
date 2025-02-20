import comparator.MultisetComparator;
import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.Comparator;
import java.util.Map;

public class MaximalLiteral {
    private static final Comparator<Map<Term , Integer>> comparator = new MultisetComparator();

    public static boolean isMaximal(Literal lit, Clause clause, boolean strictlyMaximal) {
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
