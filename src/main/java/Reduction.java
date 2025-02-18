import structure.Clause;

import java.util.HashSet;
import java.util.Set;

public class Reduction {
    /**
     * <b>Taut</b>: remove tautological clauses.
     */
    public static void removeTautology(Set<Clause> clauses) {
        clauses.removeIf(Clause::isTautology);
    }

    /**
     * <b>Sub</b>: remove subsumed clauses.
     */
    public static void subsumptionReduction(Set<Clause> clauses) {
        Set<Clause> toRemove = new HashSet<>();

        for (Clause ref : clauses) {
            if (toRemove.contains(ref)) continue;

            for (Clause target : clauses) {
                if (toRemove.contains(target)) continue;

                if (Subsumption.isSubsumed(ref, target)) {
                    System.out.println(target);
                    toRemove.add(target);
                }
            }
        }

        clauses.removeAll(toRemove);
    }

    /**
     * <b>MRR</b>: update target's clauses using Matching Replacement Resolution.
     */
    public static void matchingReplacementResolution(Set<Clause> reference, Set<Clause> target) {
        for (Clause ref : reference) {
            for (Clause t : target) {
                Clause replacer = MatchingReplacementResolution.apply(ref, t);

                if (replacer != null) {
                    t.replace(replacer);
                }
            }
        }
    }
}
