package org.mathlogic.utility;

import org.mathlogic.structure.Clause;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class Reduction {
    /**
     * <b>Taut:</b> remove tautological clauses.
     */
    public static void removeTautology(@NotNull Set<Clause> clauses) {
        clauses.removeIf(Clause::isTautology);
    }

    /**
     * <b>Sub:</b> remove subsumed clauses.
     */
    public static void subsumptionReduction(@NotNull Set<Clause> clauses) {
        Set<Clause> toRemove = new HashSet<>();
        for (Clause ref : clauses) {
            if (toRemove.contains(ref)) continue;

            for (Clause target : clauses) {
                if (ref.equals(target) || toRemove.contains(target)) continue;

                if (Subsumption.isSubsumed(ref, target)) {
                    toRemove.add(target);
                }
            }
        }
        clauses.removeAll(toRemove);
    }

    /**
     * <b>MRR:</b> update target's clauses using Matching Replacement Resolution.
     */
    public static void matchingReplacementResolution(
            @NotNull Set<Clause> reference,
            @NotNull Set<Clause> target
    ) {
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
