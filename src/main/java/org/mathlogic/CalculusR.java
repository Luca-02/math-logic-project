package org.mathlogic;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.mathlogic.utility.Reduction;
import org.mathlogic.utility.Renaming;
import org.mathlogic.utility.Substitution;
import org.mathlogic.utility.Unification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CalculusR extends AutomaticCalculus {
    @Override
    protected void initialReduction() {
        Reduction.removeTautology(usable);
        Reduction.subsumptionReduction(usable);
        Reduction.matchingReplacementResolution(usable, usable);
    }

    @Override
    protected Set<Clause> inferAllPossibleClausesFromItself(Clause given) {
        // Apply factorization on given clause
        Set<Clause> newClauses = new HashSet<>(factorizeAllPossibleClauses(given));

        Clause cloneGiven = given.copy();
        // Apply renomination to make sure that the tow clause have disjoint variables
        Renaming.renameClausesToDisjointVariable(given, cloneGiven);

        // Resolution on literals: from given (positive) to given (negative)
        newClauses.addAll(resolveAllPossibleClauses(given, cloneGiven));

        return newClauses;
    }

    @Override
    protected Set<Clause> inferAllPossibleClausesFromWorkedClause(Clause given, Clause clauseWo) {
        Set<Clause> newClauses = new HashSet<>();

        // Apply renomination to make sure that the tow clause have disjoint variables
        Renaming.renameClausesToDisjointVariable(clauseWo, given);

        // Resolution on literals: from given (positive) to Wo clause (negative)
        newClauses.addAll(resolveAllPossibleClauses(given, clauseWo));

        // Resolution on literals: from c (positive) to given Wo clause (negative)
        newClauses.addAll(resolveAllPossibleClauses(clauseWo, given));

        return newClauses;
    }

    @Override
    protected void forwardReduction(Set<Clause> newClauses) {
        Reduction.removeTautology(newClauses);
        Reduction.subsumptionReduction(newClauses);
        Reduction.matchingReplacementResolution(newClauses, newClauses);
        Reduction.matchingReplacementResolution(worked, newClauses);
        Reduction.matchingReplacementResolution(usable, newClauses);
    }

    @Override
    protected void backwardsReduction(Set<Clause> newClauses) {
        Reduction.matchingReplacementResolution(newClauses, worked);
        Reduction.matchingReplacementResolution(newClauses, usable);
    }

    /**
     * All possible right factorization of a clause.
     */
    private Set<Clause> factorizeAllPossibleClauses(Clause clause) {
        Set<Clause> factorizations = new HashSet<>();
        List<Literal> posList = new ArrayList<>(clause.getPositiveLiterals());
        // Avoid to check the same pair of literals two times
        for (int i = 0; i < posList.size(); i++) {
            for (int j = i + 1; j < posList.size(); j++) {
                Clause factorized = factorizeClause(clause, posList.get(i), posList.get(j));

                if (factorized != null) {
                    factorizations.add(factorized);
                }
            }
        }
        return factorizations;
    }

    /**
     * All possible resolution of two clauses, possibly renamed to have disjoint variables.
     */
    private Set<Clause> resolveAllPossibleClauses(Clause clauseWithPos, Clause clauseWithNeg) {
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
     * Right factorization of a clause with the given literals for which we have done the unification.
     */
    public Clause factorizeClause(Clause clause, Literal lit1, Literal lit2) {
        Map<String, Term> mgu = Unification.unify(lit1, lit2);

        if (mgu != null && factorizationCanBeApplied(clause, lit1, mgu)) {
            // By applying the substitution on the clause, it will automatically merge
            // the literals A and B on which the unification has been done through the mgu
            return Substitution.applySubstitution(clause, mgu);
        }
        return null;
    }

    /**
     * Resolution of two clauses with the given literals for which we have done the unification
     */
    public Clause resolveClauses(Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete) {
        Map<String, Term> mgu = Unification.unify(posToDelete, negToDelete);

        if (mgu != null && resolutionCanBeApplied(clauseWithPos, clauseWithNeg, posToDelete, negToDelete, mgu)) {
            Clause clauseWithPosClone = clauseWithPos.copy();
            Clause clauseWithNegClone = clauseWithNeg.copy();
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
     * Function that returns whether the factorization rule can be applied according to
     * certain rules with respect to the type of calculus being adopted.
     */
    protected abstract boolean factorizationCanBeApplied(
            Clause clause,
            Literal lit,
            Map<String, Term> mgu
    );

    /**
     * Function that returns whether the resolution rule can be applied according to
     * certain rules with respect to the type of calculus being adopted.
     */
    protected abstract boolean resolutionCanBeApplied(
            Clause clauseWithPos,
            Clause clauseWithNeg,
            Literal posToDelete,
            Literal negToDelete,
            Map<String, Term> mgu
    );
}
