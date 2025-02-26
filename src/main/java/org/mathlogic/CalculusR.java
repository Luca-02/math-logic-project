package org.mathlogic;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.mathlogic.utility.Unification;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalculusR extends AutomaticCalculus {
    @Override
    protected Set<Clause> inferAllPossibleClausesFromItself(Clause given, Clause renamedGiven) {
        // Factorization on given clause
        Set<Clause> newClauses = new HashSet<>(rightFactorization(given));

        // Resolution on literals: from given (positive) to given (negative)
        newClauses.addAll(resolution(given, renamedGiven));

        return newClauses;
    }

    @Override
    protected Set<Clause> inferAllPossibleClausesFromWorkedClause(Clause given, Clause renamedClauseWo) {
        Set<Clause> newClauses = new HashSet<>();

        // Resolution on literals: from given (positive) to Wo clause (negative)
        newClauses.addAll(resolution(given, renamedClauseWo));

        // Resolution on literals: from Wo clause (positive) to given Wo clause (negative)
        newClauses.addAll(resolution(renamedClauseWo, given));

        return newClauses;
    }

    /**
     * All possible Resolution of two clauses.
     */
    private Set<Clause> resolution(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> newClauses = new HashSet<>();
        for (Literal pos : getPossibleSolvablePositiveLiterals(clauseWithPos)) {
            for (Literal neg : getPossibleSolvableNegativeLiterals(clauseWithNeg)) {
                Clause resolvent = applyResolution(clauseWithPos, clauseWithNeg, pos, neg);
                if (resolvent != null) {
                    newClauses.add(resolvent);
                }
            }
        }
        return newClauses;
    }

    /**
     * All possible Right Factorization of a clause.
     */
    private Set<Clause> rightFactorization(Clause clause) {
        Set<Clause> newClauses = new HashSet<>();
        List<Literal> posList = new ArrayList<>(getPossibleFactorizableLiterals(clause));
        // Avoid to check the same pair of literals two times
        for (int i = 0; i < posList.size(); i++) {
            for (int j = i + 1; j < posList.size(); j++) {
                if (posList.get(i).equals(posList.get(j))) continue;

                Clause factorized = applyRightFactorize(clause, posList.get(i), posList.get(j));
                if (factorized != null) {
                    newClauses.add(factorized);
                }
            }
        }
        return newClauses;
    }

    /**
     * Resolution of two clauses with the given literals for which we need to perform the unification.
     */
    public Clause applyResolution(
            Clause clauseWithPos,
            Clause clauseWithNeg,
            Literal posToDelete,
            Literal negToDelete
    ) {
        Map<String, Term> mgu = Unification.unify(posToDelete, negToDelete);
        if (mgu == Unification.INVALID_SUBSTITUTION ||
                !resolutionCanBeApplied(clauseWithPos, clauseWithNeg, posToDelete, negToDelete, mgu)) {
            return null;
        }

        Clause clauseWithPosCopy = clauseWithPos.copy();
        Clause clauseWithNegCopy = clauseWithNeg.copy();
        clauseWithPosCopy.removeLiteral(posToDelete);
        clauseWithNegCopy.removeLiteral(negToDelete);

        Set<Literal> mergedLiterals = new HashSet<>();
        mergedLiterals.addAll(clauseWithPosCopy.getAllLiterals());
        mergedLiterals.addAll(clauseWithNegCopy.getAllLiterals());

        Clause resolvent = new Clause(mergedLiterals);
        return resolvent.applySubstitution(mgu);
    }

    /**
     * Right Factorization of a clause with the given literals for which we need to perform the unification.
     */
    public Clause applyRightFactorize(Clause clause, Literal lit1, Literal lit2) {
        Map<String, Term> mgu = Unification.unify(lit1, lit2);
        if (mgu == Unification.INVALID_SUBSTITUTION ||
                !rightFactorizationCanBeApplied(clause, lit1, mgu)) {
            return null;
        }

        // By applying the substitution on the clause, it will automatically merge
        // the literals A and B on which the unification has been done through the mgu
        return clause.applySubstitution(mgu);
    }

    /**
     * Provide the list of positive literals of a clause for which we can apply Resolution rule.
     */
    protected Set<Literal> getPossibleSolvablePositiveLiterals(Clause clause) {
        return clause.getPositiveLiterals();
    }

    /**
     * Provide the list of negative literals of a clause for which we can apply Resolution rule.
     */
    protected Set<Literal> getPossibleSolvableNegativeLiterals(Clause clause) {
        return clause.getNegativeLiterals();
    }

    /**
     * Provide the list of literals of a clause for which we can apply Right Factorization rule.
     */
    protected Set<Literal> getPossibleFactorizableLiterals(Clause clause) {
        return clause.getPositiveLiterals();
    }

    /**
     * Function that returns whether the Resolution rule can be applied according to
     * certain rules with respect to the type of calculus being adopted.
     * In the calculus R case, the Resolution is always applied.
     */
    protected boolean resolutionCanBeApplied(
            Clause clauseWithPos,
            Clause clauseWithNeg,
            Literal posToDelete,
            Literal negToDelete,
            @NotNull Map<String, Term> mgu
    ) {
        return true;
    }

    /**
     * Function that returns whether the Right Factorization rule can be applied according to
     * certain rules with respect to the type of calculus being adopted.
     * In the calculus R case, the Right Factorization is always applied.
     */
    protected boolean rightFactorizationCanBeApplied(
            Clause clause,
            Literal lit,
            @NotNull Map<String, Term> mgu
    ) {
        return true;
    }
}
