package org.mathlogic;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.mathlogic.utility.MaximalLiteral;
import org.mathlogic.utility.Substitution;
import org.mathlogic.utility.Unification;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mathlogic.Constant.IDENTITY_SYMBOL;

public class CalculusS extends AutomaticCalculus {
    @Override
    protected Set<Clause> inferAllPossibleClausesFromItself(Clause given, Clause renamedGiven) {
        Set<Clause> newClauses = new HashSet<>();

        // Right overlap on given clause: from given (positive) to given (negative)
        newClauses.addAll(rightSuperposition(given, renamedGiven));

        // Left overlap on given clause: from given (positive) to given (negative)
        newClauses.addAll(leftSuperposition(given, renamedGiven));

        // Resolution for Equality on given clause
        newClauses.addAll(equalityResolution(given));

        // Factorization for Equality on given clause
        newClauses.addAll(equalityFactorization(given));

        return newClauses;
    }

    @Override
    protected Set<Clause> inferAllPossibleClausesFromWorkedClause(Clause given, Clause renamedClauseWo) {
        Set<Clause> newClauses = new HashSet<>();

        // Right overlap on given clause: from given (positive) to Wo clause (negative)
        newClauses.addAll(rightSuperposition(given, renamedClauseWo));

        // Right overlap on given clause: from Wo clause (positive) to given (negative)
        newClauses.addAll(rightSuperposition(renamedClauseWo, given));

        // Left overlap on given clause: from given (positive) to Wo clause (negative)
        newClauses.addAll(leftSuperposition(renamedClauseWo, given));

        // Left overlap on given clause: from Wo clause (positive) to given (negative)
        newClauses.addAll(leftSuperposition(given, renamedClauseWo));

        return newClauses;
    }

    /**
     * All possible left superposition rule application of two clauses.
     */
    private Set<Clause> leftSuperposition(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> newClauses = new HashSet<>();

        return newClauses;
    }

    /**
     * All possible right superposition rule application of two clauses.
     */
    private Set<Clause> rightSuperposition(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> newClauses = new HashSet<>();

        return newClauses;
    }

    /**
     * All possible equality resolution rule application of a clause.
     */
    private Set<Clause> equalityResolution(Clause clause) {
        Set<Clause> newClauses = new HashSet<>();
        for (Literal lit : clause.getMaximalNegativeLiterals()) {
            Clause newClause = applyEqualityResolution(clause, lit);
            if (newClause != null) {
                newClauses.add(newClause);
            }
        }
        return newClauses;
    }

    /**
     * All possible equality factorization rule application of a clause.
     */
    private Set<Clause> equalityFactorization(Clause clause) {
        Set<Clause> newClauses = new HashSet<>();
        for (Literal lit1 : clause.getMaximalPositiveLiterals()) {
            for (Literal lit2 : clause.getPositiveLiterals()) {
                if (lit1.equals(lit2)) continue;

                Clause newClause = applyEqualityFactorization(clause, lit1, lit2);
                if (newClause != null) {
                    newClauses.add(newClause);
                }
            }
        }
        return newClauses;
    }

    /**
     * Equality resolution rule of a clause with the given literal for which
     * we must perform unification considering the symmetry of the identity.
     */
    public Clause applyEqualityResolution(Clause clause, Literal lit) {
        List<Term> termsLit = getMultisetTermsList(lit);

        // Treats identities symmetrically, trying all combinations between identities
        for (int i = 0; i < 2; i++) {
            Term s = termsLit.get(i);
            Term t = termsLit.get(1 - i);

            Literal currentLit = new Literal(lit.isNegated(), IDENTITY_SYMBOL, s, t);
            Clause currentClause = clause.copy();
            currentClause.removeLiteral(lit);
            currentClause.addLiteral(currentLit);

            Map<String, Term> mgu = Unification.unify(s, t);
            if (mgu == Unification.INVALID_SUBSTITUTION ||
                    !equalityResolutionCanBeApplied(currentClause, currentLit, mgu)) {
                continue;
            }

            Clause clauseCopy = clause.copy();
            clauseCopy.removeLiteral(lit);
            return Substitution.applySubstitution(clauseCopy, mgu);
        }
        return null;
    }

    /**
     * Equality factorization rule of a clause with the given literals for which
     * we must perform unification considering the symmetry of the identity.
     */
    public Clause applyEqualityFactorization(
            Clause clause,
            Literal lit1,
            Literal lit2
    ) {
        List<Term> termsLit1 = getMultisetTermsList(lit1);
        List<Term> termsLit2 = getMultisetTermsList(lit2);

        // Treats identities symmetrically, trying all combinations between identities
        for (int i = 0; i < 2; i++) {
            Term s1 = termsLit1.get(i);
            Term t1 = termsLit1.get(1 - i);

            for (int j = 0; j < 2; j++) {
                Term s2 = termsLit2.get(j);
                Term t2 = termsLit2.get(1 - j);

                Map<String, Term> mgu = Unification.unify(s1, s2);
                if (mgu == Unification.INVALID_SUBSTITUTION ||
                        !equalityFactoringCanBeApplied(clause, lit1, mgu)) {
                    continue;
                }

                Clause clauseCopy = clause.copy();
                clauseCopy.removeLiteral(lit1);
                clauseCopy.removeLiteral(lit2);

                Literal newLit1 = new Literal(true, IDENTITY_SYMBOL, t1, t2);
                Literal newLit2 = new Literal(false, IDENTITY_SYMBOL, s1, t2);

                clauseCopy.addLiteral(newLit1);
                clauseCopy.addLiteral(newLit2);
                return Substitution.applySubstitution(clauseCopy, mgu);
            }
        }
        return null;
    }

    /**
     * Function that returns whether the equality resolution rule can be applied.
     */
    private boolean equalityResolutionCanBeApplied(
            Clause clause,
            Literal lit,
            @NotNull Map<String, Term> mgu
    ) {
        Clause subClause = Substitution.applySubstitution(clause, mgu);
        Literal subLit = Substitution.applySubstitution(lit, mgu);
        return MaximalLiteral.isMaximal(subLit, subClause);
    }

    /**
     * Function that returns whether the equality factorization rule can be applied.
     */
    private boolean equalityFactoringCanBeApplied(
            Clause clause,
            Literal lit,
            @NotNull Map<String, Term> mgu
    ) {
        Clause subClause = Substitution.applySubstitution(clause, mgu);
        Literal subLit = Substitution.applySubstitution(lit, mgu);
        return MaximalLiteral.isMaximal(subLit, subClause);
    }

    /**
     * Extracts terms from a literal with respect to multiset view.
     */
    private List<Term> getMultisetTermsList(Literal lit) {
        List<Term> termsList = new ArrayList<>();
        Map<Term, Integer> multiset = lit.getMultisetView();

        // If the multiset has size 1, it means that the
        // right and left terms of identity are the same
        if (multiset.size() == 1) {
            Term term = multiset.keySet().iterator().next();
            termsList.add(term);
            termsList.add(term);
        } else {
            termsList.addAll(multiset.keySet());
        }

        return termsList;
    }
}
