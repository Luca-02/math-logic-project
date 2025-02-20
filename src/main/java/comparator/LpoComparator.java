package comparator;

import structure.Term;

import java.util.Comparator;
import java.util.List;

public class LpoComparator implements Comparator<Term> {
    /**
     * Given a precedence relation {@code <} on {@code Σ} we define {@code <lpo} as the smallest relation on trees that satisfies
     * {@code s = f(s1, ..., sn) <lpo t = g(t1, ..., tm)} iff at least one of the following holds:
     * <ul>
     * <li> {@code f < g} and {@code for all i ∈ {1, ...,n} si <lpo t} </li>
     * <li> {@code f = g} and {@code there exists k ∈ {1, ..., n} s.t. for all i < k si = ti, sk < tk and for all j ∈ {k + 1, ..., n} sj <lpo t} </li>
     * <li> {@code s ≤lpo ti for some i ∈ {1, ..., n}} </li>
     * </ul>
     * where {@code s <=lpo t} is shorthand for {@code s = t or s <lpo t}.
     */
    @Override
    public int compare(Term s, Term t) {
        if (s.equals(t)) return 0;
        if (s == Term.MINIMAL) return -1;
        if (t == Term.MINIMAL) return 1;

        // Variables are considered equals
        if (s.isVariable() && t.isVariable()) {
            return 0;
        }

        // Variable is always considered less than a function
        if (s.isVariable()) return -1;
        if (t.isVariable()) return 1;

        String f = s.getName();
        String g = t.getName();
        List<Term> sArgs = s.getArguments();
        List<Term> tArgs = t.getArguments();

        // If f is alphabetically less than g and for each argument we have si <lpo t
        if (f.compareTo(g) < 0) {
            boolean allLess = true;
            for (Term sArg : sArgs) {
                if (!(compare(sArg, t) < 0)) {
                    allLess = false;
                    break;
                }
            }
            if (allLess) return -1;
            else return 1;
        }

        // If f = g, we look for the first index k where s and t differ
        if (f.equals(g)) {
            int minSize = Math.min(sArgs.size(), tArgs.size());
            int k = 0;
            for (int i = 0; i < minSize; i++) {
                if (!sArgs.get(i).equals(tArgs.get(i))) {
                    k = i;
                    break;
                }
            }

            // We found an index k where sk and tk differ
            if (compare(sArgs.get(k), tArgs.get(k)) < 0) {
                // Let us check that for each j ∈ {k+1, ..., n} we have sj <lpo t
                boolean allLess = true;
                for (int j = k + 1; j < sArgs.size(); j++) {
                    if (!(compare(sArgs.get(j), t) < 0)) {
                        allLess = false;
                        break;
                    }
                }
                if (allLess) return -1;
            }

            // If all initial elements match and s has fewer arguments, s <lpo t
            if (sArgs.size() < tArgs.size()) return -1;
            else return 1;
        }

        // If s ≤lpo ti for some i ∈ {1, ..., n}
        for (Term tArg : tArgs) {
            if (s.equals(tArg) || compare(s, tArg) < 0) return -1;
        }

        return 1;
    }
}