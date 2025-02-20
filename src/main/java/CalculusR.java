import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.*;

public abstract class CalculusR {
    private final Set<Clause> usable; // Us
    private final Set<Clause> worked; // Wo

    public CalculusR() {
        this(Collections.emptySet());
    }

    public CalculusR(Set<Clause> clauses) {
        this.usable = new HashSet<>();
        this.worked = new HashSet<>();
        initClauses(clauses);
    }

    public void initClauses(Set<Clause> clauses) {
        usable.addAll(clauses);
        Reduction.removeTautology(usable);
        Reduction.subsumptionReduction(usable);
        Reduction.matchingReplacementResolution(usable, usable);
    }

    /**
     * Refute the clauses. Return {@code true} if a refutation is reached,
     * otherwise {@code false}.
     */
    public boolean refute() {
        do {
            // Returns true if we found a refutation.
            if (refutationReached()) {
                return true;
            }

            // 1. Select the given clause
            Clause given = selectGivenClause();
            worked.add(given);
            usable.remove(given);

            // 2. Generates new clauses by inferences between given clause and clauses in Wo and Us
            Set<Clause> newClauses = inferClauses(given, worked);

            // 3. Apply forward reductions on new clauses
            Reduction.removeTautology(newClauses);
            Reduction.subsumptionReduction(newClauses);
            Reduction.matchingReplacementResolution(newClauses, newClauses);
            Reduction.matchingReplacementResolution(worked, newClauses);
            Reduction.matchingReplacementResolution(usable, newClauses);

            // 4. Apply backwards reductions on olds clauses in Us and Wo with the new ones
            Reduction.matchingReplacementResolution(newClauses, worked);
            Reduction.matchingReplacementResolution(newClauses, usable);

            // 4. Add the new clauses to Us
            usable.addAll(newClauses);
        } while (!usable.isEmpty());

        // Return false to indicate that it did not find a refutation, so it is satisfiable
        return false;
    }

    /**
     * Select the given clause by means of an appropriate choice function
     * (in this case the clause with the minimum number of symbols).
     */
    public Clause selectGivenClause() {
        Clause given = null;
        for (Clause clause : usable) {
            if (given == null || clause.compareTo(given) < 0) {
                given = clause;
            }
        }
        return given;
    }

    /**
     * If {@code Us} contains an empty clause we have reached a refutation.
     */
    public boolean refutationReached() {
        return usable.stream().anyMatch(Clause::isEmpty);
    }

    /**
     * Apply all possible inference between given clause and the clauses in {@code Wo}.
     */
    private Set<Clause> inferClauses(Clause given, Set<Clause> worked) {
        // Apply factorization on given clause
        Set<Clause> newClauses = new HashSet<>(factorizeClause(given));

        Clause cloneGiven = given.clone();
        Renaming.renameClausesToDisjointVariable(given, cloneGiven);

        // Resolution on literals: from given (positive) to given (negative)
        newClauses.addAll(resolveClauses(given, cloneGiven));

        for (Clause c : worked) {
            // Apply renomination to make sure that the tow clause have disjoint variables
            Renaming.renameClausesToDisjointVariable(c, given);

            // Resolution on literals: from given (positive) to Wo clause (negative)
            newClauses.addAll(resolveClauses(given, c));

            // Resolution on literals: from c (positive) to given Wo clause (negative)
            newClauses.addAll(resolveClauses(c, given));
        }

        return newClauses;
    }

    /**
     * Resolution of two clauses, possibly renamed to have disjoint variables
     */
    private Set<Clause> resolveClauses(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> resolutions = new HashSet<>();
        for (Literal pos : clauseWithPos.getPositiveLiterals()) {
            for (Literal neg : clauseWithNeg.getNegativeLiterals()) {
                Clause resolvent = resolveClauses(clauseWithPos, clauseWithNeg, pos, neg);

                if (resolvent != null) {
                    resolutions.add(resolvent);
                }
            }
        }
        return resolutions;
    }

    /**
     * Resolution of two clauses with the given literals for which we have done the unification
     */
    public abstract Clause resolveClauses(
            Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete);
    /**
     * Right factorization on a given clause
     */
    public abstract Set<Clause> factorizeClause(Clause clause);
}
