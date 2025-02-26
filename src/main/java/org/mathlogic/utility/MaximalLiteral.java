package org.mathlogic.utility;

import org.mathlogic.comparator.LpoComparator;
import org.mathlogic.comparator.MultisetComparator;
import org.mathlogic.exception.LiteralNotFoundInClauseException;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaximalLiteral {
    private static final LpoComparator lpoComparator = new LpoComparator();
    private static final Comparator<Map<Term , Integer>> multisetComparator = new MultisetComparator();

    /**
     * Get the maximal literals from a given clause.
     */
    public static Set<Literal> getMaximalLiterals(@NotNull Clause clause) {
        List<Literal> literals = new ArrayList<>(clause.getAllLiterals());

        // Sort literals in descending order based on their multiset views
        literals.sort((lit1, lit2) ->
                multisetComparator.compare(lit2.getMultisetView(), lit1.getMultisetView()));

        Set<Literal> maximalLiterals = new HashSet<>();
        if (!literals.isEmpty()) {
            maximalLiterals.add(literals.get(0));
            Map<Term, Integer> maxMultiset = literals.get(0).getMultisetView();

            // Collect all literals that have the same weight as the first literal
            for (int i = 1; i < literals.size(); i++) {
                Literal lit = literals.get(i);
                if (multisetComparator.compare(maxMultiset, lit.getMultisetView()) == 0) {
                    maximalLiterals.add(lit);
                } else {
                    break;
                }
            }
        }
        return maximalLiterals;
    }

    /**
     * Check if a literal is maximal in a given clause
     */
    public static boolean isMaximal(
            @NotNull Literal lit,
            @NotNull Clause clause
    ) {
        return isMaximal(lit, clause, false);
    }

    /**
     * Check if a literal is <b>strictly</b> maximal in a given clause
     */
    public static boolean isStrictlyMaximal(
            @NotNull Literal lit,
            @NotNull Clause clause
    ) {
        return isMaximal(lit, clause, true);
    }

    private static boolean isMaximal(
            Literal lit,
            Clause clause,
            boolean strictlyMaximal
    ) {
        // If the given literal is not in the given clause, there is an error
        if (!clause.getAllLiterals().contains(lit)) {
            throw new LiteralNotFoundInClauseException(lit, clause);
        }

        Set<Literal> maximalLiterals = getMaximalLiterals(clause);

        if (strictlyMaximal) {
            return strictlyMaximalConstraint(lit, maximalLiterals);
        } else {
            return maximalLiterals.contains(lit);
        }
    }

    /**
     * Check the strictly maximal constraint on a given set of maximal literals with a given literals.
     */
    private static boolean strictlyMaximalConstraint(
            Literal lit,
            Set<Literal> maximalLiterals
    ) {
        // Treat all the variable as the same
        Literal renamedLit = Renaming.renameLogicalStructureToSameVariable(lit).sortTermsIfIdentity(lpoComparator);
        Set<Literal> renamedMaximalLiterals = new HashSet<>();

        for (Literal maximalLit : maximalLiterals) {
            Literal renMaximalLit = Renaming.renameLogicalStructureToSameVariable(maximalLit);
            renamedMaximalLiterals.add(renMaximalLit);
        }

        // Treat the reflexivity of the literal with identity
        // The arguments of literals with identity will be ordered in the same way.
        Literal sortedLit = renamedLit.sortTermsIfIdentity(lpoComparator);
        Set<Literal> sortedMaximalLiterals = new HashSet<>();
        for (Literal renamedMaximalLit : renamedMaximalLiterals) {
            sortedMaximalLiterals.add(renamedMaximalLit.sortTermsIfIdentity(lpoComparator));
        }

        return sortedMaximalLiterals.contains(sortedLit) &&
                sortedMaximalLiterals.size() == 1;
    }
}
