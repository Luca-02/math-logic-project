import model.Clause;
import model.Literal;
import model.Term;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResolverR {
    private final Set<Clause> usable; // Us
    private final Set<Clause> worked; // Wo

    public ResolverR(Set<Clause> clauses) {
        this.usable = new HashSet<>(clauses);
        this.worked = new HashSet<>();
        initReduce(usable);
    }

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
            reduceForward(newClauses);

            // 4. Apply backwards reductions on olds clauses in Us and Wo with the new ones
            reduceBackward(newClauses);

            // 5. Add the new clauses to Us
            usable.addAll(newClauses);
        } while (!usable.isEmpty());

        // Return false to indicate that it did not find a refutation, so it is satisfiable
        return false;
    }

    /**
     * Select the minimum clause by number of literals
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
     * Check if we have reached a refutation
     */
    public boolean refutationReached() {
        return usable.stream().anyMatch(Clause::isEmpty);
    }

    public Set<Clause> inferClauses(Clause given, Set<Clause> worked) {
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
    public Set<Clause> resolveClauses(Clause clauseWithPos, Clause clauseWithNeg) {
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
    public Clause resolveClauses(Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete) {
        Map<String, Term> mgu = Unification.unify(posToDelete, negToDelete);

        if (mgu != null) {
            Clause clauseWithPosClone = clauseWithPos.clone();
            Clause clauseWithNegClone = clauseWithNeg.clone();
            clauseWithPosClone.getPositiveLiterals().remove(posToDelete);
            clauseWithNegClone.getNegativeLiterals().remove(negToDelete);

            Set<Literal> mergedLiterals = new HashSet<>();
            mergedLiterals.addAll(clauseWithPosClone.getNegativeLiterals());
            mergedLiterals.addAll(clauseWithNegClone.getNegativeLiterals());
            mergedLiterals.addAll(clauseWithPosClone.getPositiveLiterals());
            mergedLiterals.addAll(clauseWithNegClone.getPositiveLiterals());

            Clause resolvent = new Clause(mergedLiterals);
            return Substitution.applySubstitution(resolvent, mgu);
        }

        return null;
    }

    /**
     * Right factorization on a given clause
     */
    public Set<Clause> factorizeClause(Clause clause) {
        Set<Clause> factorizations = new HashSet<>();

        List<Literal> posList = new ArrayList<>(clause.getPositiveLiterals());
        for (int i = 0; i < posList.size(); i++) {
            for (int j = i + 1; j < posList.size(); j++) {
                Map<String, Term> mgu = Unification.unify(posList.get(i), posList.get(j));
                if (mgu != null) {
                    // By applying the substitution on the clause, it will automatically merge
                    // the literals A and B on which the unification has been done through the mgu
                    Clause factClause = Substitution.applySubstitution(clause, mgu);
                    factorizations.add(factClause);
                }
            }
        }

        return factorizations;
    }

    /**
     * Initial reduction on input clauses.
     */
    private void initReduce(Set<Clause> clauses) {
        // Remove tautologies
        clauses.removeIf(Clause::isTautology);
        // Reduce with itself
        applySubsumptionReduction(clauses, clauses);
    }

    /**
     * Forward Reduction: Remove tautologies and remove subsumed clauses.
     */
    private void reduceForward(Set<Clause> newClauses) {
        // Remove tautologies
        newClauses.removeIf(Clause::isTautology);
        // Reduce New between itself
        applySubsumptionReduction(newClauses, newClauses);
        // Reduce New between Wo
        applySubsumptionReduction(newClauses, worked);
        // Reduce New between Us
        applySubsumptionReduction(newClauses, usable);
    }

    /**
     * Backward Reduction: Remove subsumed clauses from Us and Wo.
     */
    private void reduceBackward(Set<Clause> newClauses) {
        // Remove tautologies
        worked.removeIf(Clause::isTautology);
        usable.removeIf(Clause::isTautology);
        // Reduce Wo compared to new clauses
        applySubsumptionReduction(worked, newClauses);
        // Reduce Us compared to new clauses
        applySubsumptionReduction(usable, newClauses);
    }

    /**
     * Helper function to remove subsumed clauses in target using clauses in reference.
     */
    private void applySubsumptionReduction(Set<Clause> target, Set<Clause> reference) {
        Set<Clause> toRemove = new HashSet<>();

        for (Clause ref : reference) {
            if (toRemove.contains(ref)) continue;
            for (Clause t : target) {
                if (toRemove.contains(t)) continue;
                if (Subsumption.isSubsumed(ref, t)) {
                    toRemove.add(t);
                }
            }
        }

        target.removeAll(toRemove);
    }
}
