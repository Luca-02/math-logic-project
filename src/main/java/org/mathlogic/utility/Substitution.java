package org.mathlogic.utility;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Substitution {
    /**
     * Apply a given substitution to a term.
     */
    public static Term applySubstitution(
            @NotNull Term term,
            @NotNull Map<String, Term> substitutions
    ) {
        if (term.isVariable()) {
            Term sub = substitutions.get(term.getName());
            return sub != null ? sub.copy() : term;
        } else {
            List<Term> args = term.getArguments().stream()
                    .map(arg -> applySubstitution(arg, substitutions))
                    .toList();
            return new Term(term.getName(), args);
        }
    }

    /**
     * Apply a given substitution to a literal.
     */
    public static Literal applySubstitution(
            @NotNull Literal lit,
            @NotNull Map<String, Term> substitutions
    ) {
        List<Term> substitutedTerms = lit.getTerms().stream()
                .map(term -> applySubstitution(term, substitutions))
                .toList();
        return new Literal(lit.isNegated(), lit.getPredicate(), substitutedTerms);
    }

    /**
     * Apply a given substitution to a clause.
     */
    public static Clause applySubstitution(
            @NotNull Clause clause,
            @NotNull Map<String, Term> substitutions
    ) {
        Set<Literal> literals = new HashSet<>();
        for (Literal lit : clause.getAllLiterals()) {
            literals.add(applySubstitution(lit, substitutions));
        }
        return new Clause(literals);
    }
}
