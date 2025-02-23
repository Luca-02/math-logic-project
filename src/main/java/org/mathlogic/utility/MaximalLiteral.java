package org.mathlogic.utility;

import org.mathlogic.comparator.MultisetComparator;
import org.jetbrains.annotations.NotNull;
import org.mathlogic.exception.LiteralNotFoundInClauseException;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaximalLiteral {
    private static final Comparator<Map<Term , Integer>> comparator = new MultisetComparator();

    /**
     * Get the maximal literals from a given clause.
     */
    public static Set<Literal> getMaximalLiterals(@NotNull Clause clause) {
        List<Literal> literals = new ArrayList<>(clause.getAllLiterals());

        // Sort literals in descending order based on their multiset views
        literals.sort((lit1, lit2) ->
                comparator.compare(lit2.getMultisetView(), lit1.getMultisetView()));

        Set<Literal> maximalLiterals = new HashSet<>();
        if (!literals.isEmpty()) {
            maximalLiterals.add(literals.get(0));
            Map<Term, Integer> maxMultiset = literals.get(0).getMultisetView();

            // Collect all literals that have the same weight as the first literal
            for (int i = 1; i < literals.size(); i++) {
                Literal lit = literals.get(i);
                if (comparator.compare(maxMultiset, lit.getMultisetView()) == 0) {
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
            @NotNull Literal lit,
            @NotNull Clause clause,
            boolean strictlyMaximal
    ) {
        Map<Term, Integer> litMultiset = lit.getMultisetView();
        boolean literalFound = false;

        for (Literal cLit : clause.getAllLiterals()) {
            if (lit.equals(cLit)) {
                literalFound = true;
                continue;
            }

            int cmp = comparator.compare(litMultiset, cLit.getMultisetView());
            if (strictlyMaximal ? cmp <= 0 : cmp < 0) {
                return false;
            }
        }

        // If the given literal is not in the given clause, there is an error
        if (!literalFound) {
            throw new LiteralNotFoundInClauseException(lit, clause);
        }

        return true;
    }
}
