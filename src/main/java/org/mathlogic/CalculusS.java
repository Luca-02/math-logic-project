package org.mathlogic;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.mathlogic.utility.Unification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalculusS extends AutomaticCalculus {
    @Override
    protected Set<Clause> inferAllPossibleClausesFromItself(Clause given, Clause renamedGiven) {
        Set<Clause> newClauses = new HashSet<>();

        // Right overlap on given clause: from given (positive) to given (negative)
        newClauses.addAll(superpositionRight(given, renamedGiven));

        // Left overlap on given clause: from given (positive) to given (negative)
        newClauses.addAll(superpositionLeft(given, renamedGiven));

        // Resolution for Equality on given clause
        newClauses.addAll(equalityResolution(given));

        // Factorization for Equality on given clause
        newClauses.addAll(equalityFactoring(given));

        return newClauses;
    }

    @Override
    protected Set<Clause> inferAllPossibleClausesFromWorkedClause(Clause given, Clause renamedClauseWo) {
        Set<Clause> newClauses = new HashSet<>();

        // Right overlap on given clause: from given (positive) to Wo clause (negative)
        newClauses.addAll(superpositionRight(given, renamedClauseWo));

        // Right overlap on given clause: from Wo clause (positive) to given (negative)
        newClauses.addAll(superpositionRight(renamedClauseWo, given));

        // Left overlap on given clause: from given (positive) to Wo clause (negative)
        newClauses.addAll(superpositionLeft(renamedClauseWo, given));

        // Left overlap on given clause: from Wo clause (positive) to given (negative)
        newClauses.addAll(superpositionLeft(given, renamedClauseWo));

        return newClauses;
    }

    private Set<Clause> superpositionLeft(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> newClauses = new HashSet<>();

        return newClauses;
    }

    private Set<Clause> superpositionRight(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> newClauses = new HashSet<>();

        return newClauses;
    }

    private Set<Clause> equalityResolution(Clause clause) {
        Set<Clause> newClauses = new HashSet<>();

        return newClauses;
    }

    private Set<Clause> equalityFactoring(Clause clause) {
        Set<Clause> newClauses = new HashSet<>();

        return newClauses;
    }
}
