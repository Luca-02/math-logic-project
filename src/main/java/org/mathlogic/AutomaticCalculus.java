package org.mathlogic;

import org.mathlogic.structure.Clause;
import org.mathlogic.utility.Reduction;
import org.mathlogic.utility.Renaming;

import java.util.HashSet;
import java.util.Set;

public abstract class AutomaticCalculus {
    protected final Set<Clause> usable; // Us
    protected final Set<Clause> worked; // Wo

    protected AutomaticCalculus() {
        this.usable = new HashSet<>();
        this.worked = new HashSet<>();
    }

    /**
     * Initialize usable and worked clauses sets with a given set of clauses to refute.
     */
    protected void initClausesSets(Set<Clause> clauses) {
        usable.clear();
        worked.clear();
        usable.addAll(clauses);
        initialReduction();
    }

    /**
     * Try to refute the clauses. Return {@code true} if a refutation is reached (Proof Found),
     * otherwise {@code false} (Completion Found).
     */
    public boolean refute(Set<Clause> clauses) {
        initClausesSets(clauses);

        if (usable.isEmpty()) {
            return false;
        }

        do {
            // If Us contains an empty clause we have reached a refutation.
            if (containsEmptyClause(usable)) return true;

            // 1. Select the given clause
            Clause given = selectGivenClause(usable);
            worked.add(given);
            usable.remove(given);

            // 2. Generates new clauses by inferences between given clause and clauses in Wo and Us
            Set<Clause> newClauses = inferAllPossibleClauses(given);

            // 3. Apply forward reductions on new clauses
            forwardReduction(newClauses);

            // 4. Apply backwards reductions on olds clauses in Us and Wo with the new ones
            backwardsReduction(newClauses);

            // 4. Add the new clauses to Us
            usable.addAll(newClauses);
        } while (!usable.isEmpty());

        // Return false to indicate that it did not find a refutation, so it is satisfiable
        return false;
    }

    /**
     * Check if the given set of clauses contains an empty clause.
     */
    public boolean containsEmptyClause(Set<Clause> clauses) {
        return clauses.stream().anyMatch(Clause::isEmpty);
    }

    /**
     * Select the {@code given} clause by means of an appropriate choice function
     * (in this case the clause with the minimum number of symbols).
     */
    public Clause selectGivenClause(Set<Clause> clauses) {
        Clause given = null;
        for (Clause clause : clauses) {
            if (given == null || clause.compareTo(given) < 0) {
                given = clause;
            }
        }
        return given;
    }

    /**
     * Apply all possible inference between {@code given} clause, itself and the clauses of {@code Wo}.
     */
    private Set<Clause> inferAllPossibleClauses(Clause given) {
        Clause givenCopy = given.copy();
        // Apply renomination to make sure that the tow clause have disjoint variables
        Renaming.renameClausesToDisjointVariable(given, givenCopy);

        Set<Clause> newClauses = new HashSet<>(inferAllPossibleClausesFromItself(given, givenCopy));

        for (Clause clauseWo : worked) {
            // Apply renomination to make sure that the tow clause have disjoint variables
            Renaming.renameClausesToDisjointVariable(clauseWo, given);

            newClauses.addAll(inferAllPossibleClausesFromWorkedClause(given, clauseWo));
        }

        return newClauses;
    }

    /**
     * Initial reduction on the given input clauses to refute.
     */
    private void initialReduction() {
        Reduction.removeTautology(usable);
        Reduction.subsumptionReduction(usable);
        Reduction.matchingReplacementResolution(usable, usable);
    }

    /**
     * Forward reductions on the new found clauses.
     */
    private void forwardReduction(Set<Clause> newClauses) {
        Reduction.removeTautology(newClauses);
        Reduction.subsumptionReduction(newClauses);
        Reduction.matchingReplacementResolution(newClauses, newClauses);
        Reduction.matchingReplacementResolution(worked, newClauses);
        Reduction.matchingReplacementResolution(usable, newClauses);
    }

    /**
     * Backwards reductions on olds clauses in {@code Us} and {@code Wo} with the new ones
     */
    private void backwardsReduction(Set<Clause> newClauses) {
        Reduction.matchingReplacementResolution(newClauses, worked);
        Reduction.matchingReplacementResolution(newClauses, usable);
    }

    /**
     * Apply all possible inference between {@code given} clause and itself renamed so that
     * they have disjoint variable.
     */
    protected abstract Set<Clause> inferAllPossibleClausesFromItself(Clause given, Clause renamedGiven);

    /**
     * Apply all possible inference between {@code given} clause and a clause of {@code Wo} renamed
     * so that they have disjoint variable.
     */
    protected abstract Set<Clause> inferAllPossibleClausesFromWorkedClause(Clause given, Clause renamedClauseWo);
}
