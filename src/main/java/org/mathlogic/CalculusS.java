package org.mathlogic;

import org.mathlogic.comparator.LpoComparator;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.mathlogic.utility.MaximalLiteral;
import org.mathlogic.utility.Unification;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mathlogic.Constant.IDENTITY_SYMBOL;

public class CalculusS extends AutomaticCalculus {
    private static final LpoComparator lpoComparator = new LpoComparator();

    @Override
    protected void initClausesSets(Set<Clause> clauses) {
        Set<Clause> formattedClauses = formatClausesWrtIdentity(clauses);
        super.initClausesSets(formattedClauses);
    }

    @Override
    protected void initialReduction() {
        // Not implemented
    }

    @Override
    protected void forwardReduction(Set<Clause> newClauses) {
        // Not implemented
    }

    @Override
    protected void backwardsReduction(Set<Clause> newClauses) {
        // Not implemented
    }

    /**
     * Format all clauses with literals formatted as identities.
     */
    private Set<Clause> formatClausesWrtIdentity(Set<Clause> clauses) {
        Set<Clause> formattedClauses = new HashSet<>();
        for (Clause clause : clauses) {
            formattedClauses.add(clause.formatLiteralsWrtIdentity());
        }
        return formattedClauses;
    }

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
     * All possible Right Superposition rule application of two clauses.
     */
    private Set<Clause> rightSuperposition(Clause clauseWithPos1, Clause clauseWithPos2) {
        Set<Clause> newClauses = new HashSet<>();
        for (Literal pos1 : clauseWithPos1.getMaximalPositiveLiterals()) {
            for (Literal pos2 : clauseWithPos2.getMaximalPositiveLiterals()) {
                Clause newClause = applyLeftOrRightSuperposition(clauseWithPos1, clauseWithPos2, pos1, pos2, false);
                if (newClause != null) {
                    newClauses.add(newClause);
                }
            }
        }
        return newClauses;
    }

    /**
     * All possible Left Superposition rule application of two clauses.
     */
    private Set<Clause> leftSuperposition(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> newClauses = new HashSet<>();
        for (Literal pos : clauseWithPos.getMaximalPositiveLiterals()) {
            for (Literal neg : clauseWithNeg.getMaximalNegativeLiterals()) {
                Clause newClause = applyLeftOrRightSuperposition(clauseWithPos, clauseWithNeg, pos, neg, true);
                if (newClause != null) {
                    newClauses.add(newClause);
                }
            }
        }
        return newClauses;
    }

    /**
     * All possible Equality Resolution rule application of a clause.
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
     * All possible Equality Factorization rule application of a clause.
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
     * Left or Right Superposition of two clauses with the given literals for which we need to perform the unification.
     */
    public Clause applyLeftOrRightSuperposition(
            Clause clauseWithLit1,
            Clause clauseWithLit2,
            Literal lit1,
            Literal lit2,
            boolean isLeft
    ) {
        // Sort the arguments in descending order
        // Treats identities not symmetrically, using only the maximal terms
        Literal sortLit1 = sortLiteralArgumentsDesc(lit1);
        Literal sortLit2 = sortLiteralArgumentsDesc(lit2);

        Clause updatedClauseWithLit1 = clauseWithLit1.copy();
        updatedClauseWithLit1.removeLiteral(lit1);
        updatedClauseWithLit1.addLiteral(sortLit1);

        Clause updatedClauseWithLit2 = clauseWithLit2.copy();
        updatedClauseWithLit2.removeLiteral(lit2);
        updatedClauseWithLit2.addLiteral(sortLit2);

        // pos1: l = r
        Term r = sortLit1.getTerms().get(1);
        Term l = sortLit1.getTerms().get(0);
        // pos2: s = t
        Term s = sortLit2.getTerms().get(0);
        Term t = sortLit2.getTerms().get(1);

        List<Term> sElements = new ArrayList<>(s.getArguments());
        sElements.add(s);
        for (int p = 0; p <= sElements.size(); p++) {
            // Treat also the whole term s
            Term sArgument = p >= s.getArguments().size() ? s : sElements.get(p);

            // We dont manage s|p like a variable
            if (!sArgument.isVariable()) {
                Map<String, Term> mgu = Unification.unify(sArgument, l);

                if (Unification.invalidSubstitution(mgu) ||
                        !leftOrRightSuperpositionCanBeApplied(
                                updatedClauseWithLit1, updatedClauseWithLit2, sortLit1, sortLit2, mgu, isLeft)) {
                    continue;
                }

                Clause updatedClauseWithLit1Copy = updatedClauseWithLit1.copy();
                Clause updatedClauseWithLit2Copy = updatedClauseWithLit2.copy();
                updatedClauseWithLit1Copy.removeLiteral(sortLit1);
                updatedClauseWithLit2Copy.removeLiteral(sortLit2);

                Set<Literal> mergedLiterals = new HashSet<>();
                Literal newLit = new Literal(
                        isLeft,
                        IDENTITY_SYMBOL,
                        s.replaceArgument(p, r),
                        t
                );

                mergedLiterals.addAll(updatedClauseWithLit1Copy.getAllLiterals());
                mergedLiterals.addAll(updatedClauseWithLit2Copy.getAllLiterals());
                mergedLiterals.add(newLit);

                Clause resolvent = new Clause(mergedLiterals);
                return resolvent.applySubstitution(mgu);
            }
        }
        return null;
    }

    /**
     * Equality Resolution rule of a clause with the given literal for which
     * we must perform unification considering the symmetry of the identity.
     */
    public Clause applyEqualityResolution(Clause clause, Literal lit) {
        // Treats identities symmetrically, trying all combinations between identities
        for (int i = 0; i < 2; i++) {
            // lit: s = t
            Term s = lit.getTerms().get(i);
            Term t = lit.getTerms().get(1 - i);

            Literal currentLit = new Literal(lit.isNegated(), IDENTITY_SYMBOL, s, t);
            Clause currentClause = clause.copy();
            currentClause.removeLiteral(lit);
            currentClause.addLiteral(currentLit);

            Map<String, Term> mgu = Unification.unify(s, t);
            if (Unification.invalidSubstitution(mgu) ||
                    !equalityResolutionOrFactoringCanBeApplied(currentClause, currentLit, mgu)) {
                continue;
            }

            Clause clauseCopy = clause.copy();
            clauseCopy.removeLiteral(lit);
            return clauseCopy.applySubstitution(mgu);
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
        // Treats identities symmetrically, trying all combinations between identities
        for (int i = 0; i < 2; i++) {
            // lit1: s = t
            Term s1 = lit1.getTerms().get(i);
            Term t1 = lit1.getTerms().get(1 - i);

            for (int j = 0; j < 2; j++) {
                // lit2: s' = t'
                Term s2 = lit2.getTerms().get(j);
                Term t2 = lit2.getTerms().get(1 - j);

                Map<String, Term> mgu = Unification.unify(s1, s2);
                if (Unification.invalidSubstitution(mgu) ||
                        !equalityResolutionOrFactoringCanBeApplied(clause, lit1, mgu)) {
                    continue;
                }

                Clause clauseCopy = clause.copy();
                clauseCopy.removeLiteral(lit1);
                clauseCopy.removeLiteral(lit2);

                Literal newLit1 = new Literal(true, IDENTITY_SYMBOL, t1, t2);
                Literal newLit2 = new Literal(false, IDENTITY_SYMBOL, s1, t2);

                clauseCopy.addLiteral(newLit1);
                clauseCopy.addLiteral(newLit2);
                return clauseCopy.applySubstitution(mgu);
            }
        }
        return null;
    }

    /**
     * Function that returns whether the Left or Right Superposition rule can be applied.
     */
    private boolean leftOrRightSuperpositionCanBeApplied(
            Clause clauseWithLit1,
            Clause clauseWithLit2,
            Literal lit1,
            Literal lit2,
            @NotNull Map<String, Term> mgu,
            boolean isLeft
    ) {
        Clause subClauseWithLit1 = clauseWithLit1.applySubstitution(mgu);
        Clause subClauseWithLit2 = clauseWithLit2.applySubstitution(mgu);
        Literal subLit1 = lit1.applySubstitution(mgu);
        Literal subLit2 = lit2.applySubstitution(mgu);
        Term lmu = subLit1.getTerms().get(0);
        Term rmu = subLit1.getTerms().get(1);
        Term smu = subLit2.getTerms().get(0);
        Term tmu = subLit2.getTerms().get(1);
        return lpoComparator.compare(lmu, rmu) > 0 &&
                lpoComparator.compare(smu, tmu) > 0 &&
                MaximalLiteral.isStrictlyMaximal(subLit1, subClauseWithLit1) &&
                (isLeft ? MaximalLiteral.isMaximal(subLit2, subClauseWithLit2) :
                        MaximalLiteral.isStrictlyMaximal(subLit2, subClauseWithLit2));
    }

    /**
     * Function that returns whether the Equality Resolution or Factoring rule can be applied.
     */
    private boolean equalityResolutionOrFactoringCanBeApplied(
            Clause clause,
            Literal lit,
            @NotNull Map<String, Term> mgu
    ) {
        Clause subClause = clause.applySubstitution(mgu);
        Literal subLit = lit.applySubstitution(mgu);
        return MaximalLiteral.isMaximal(subLit, subClause);
    }

    /**
     * Return the literal with identity with the argument sorted in descending order.
     * So, in the case that we have s = t, if t > s then return the new literal t = s.
     */
    private Literal sortLiteralArgumentsDesc(Literal lit) {
        if (lpoComparator.compare(lit.getTerms().get(0), lit.getTerms().get(1)) >= 0) {
            return lit;
        }

        return lit.sortTermsIfIdentity(lpoComparator.reversed());
    }
}
