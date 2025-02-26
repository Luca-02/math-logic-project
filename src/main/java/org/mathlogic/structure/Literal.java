package org.mathlogic.structure;

import org.mathlogic.utility.Parsing;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mathlogic.Constant.IDENTITY_SYMBOL;
import static org.mathlogic.Constant.NOT_SYMBOL;

/**
 * Identify a literal (atomic formula) {@code P(t1, ..., tn)},
 * where {@code P} is a predicate and each {@code ti} a term.
 */
public class Literal implements LogicalStructure<Literal> {
    private boolean isNegated;
    private final String predicate;
    private final List<Term> terms;

    public Literal(boolean isNegated, @NotNull String predicate, @NotNull List<Term> terms) {
        this.isNegated = isNegated;
        this.predicate = predicate;
        this.terms = terms;
    }

    public Literal(boolean isNegated, @NotNull String predicate, @NotNull Term... terms) {
        this(isNegated, predicate, List.of(terms));
    }

    public boolean isNegated() {
        return isNegated;
    }

    public String getPredicate() {
        return predicate;
    }

    public List<Term> getTerms() {
        return Collections.unmodifiableList(terms);
    }

    public boolean isIdentity() {
        return predicate.equals(IDENTITY_SYMBOL) && terms.size() == 2;
    }

    public boolean isTautology() {
        return isIdentity() && terms.get(0).equals(terms.get(1));
    }

    /**
     * Transform the current literal with the identity predicate, if it isn't already.
     * In case the predicate of the literal is not the identity, we can write
     * the atomic formulas of the type {@code P(t1, ..., tn)} in the form
     * {@code p(t1, ..., tn) = true}, where {@code p} is a new n-ary function symbol
     * and {@code true} a new constant.
     */
    public Literal formatWrtIdentity() {
        if (isIdentity()) {
            return this;
        }

        List<Term> newTerms = new ArrayList<>();
        newTerms.add(new Term(predicate, terms));
        newTerms.add(Term.MINIMAL);
        return new Literal(isNegated, IDENTITY_SYMBOL, newTerms);
    }

    /**
     * Return the literal with the argument sorted with through
     * {@code comparator} order if it is an identity.
     */
    public Literal sortTermsIfIdentity(Comparator<Term> comparator) {
        if (!isIdentity()) {
            return this;
        }

        List<Term> sortedTerms = new ArrayList<>(getTerms());
        sortedTerms.sort(comparator);
        return new Literal(isNegated(), getPredicate(), sortedTerms);
    }

    /**
     * Returns a multiset view of the literals with their multiplicity.
     * If the literal is negated, its multiplicity is doubled.
     * In case the predicate of the literal is not the identity, we can write
     * the atomic formulas of the type {@code P(t1, ..., tn)} in the form
     * {@code p(t1, ..., tn) = true}, where {@code p} is a new n-ary function symbol
     * and {@code true} a new constant.
     */
    public Map<Term, Integer> getMultisetView() {
        Map<Term, Integer> multiset = new HashMap<>();
        int multiplicity = isNegated() ? 2 : 1;

        if (isIdentity()) {
            Term left = terms.get(0);
            Term right = terms.get(1);

            if (left.equals(right)) {
                multiset.put(left, multiplicity * 2);
            } else {
                multiset.put(left, multiplicity);
                multiset.put(right, multiplicity);
            }
        } else {
            multiset.put(new Term(predicate, terms), multiplicity);
            multiset.put(Term.MINIMAL, multiplicity);
        }

        return multiset;
    }

    public Literal negate() {
        Literal copy = copy();
        copy.isNegated = !isNegated;
        return copy;
    }

    @Override
    public List<String> collectSymbols() {
        List<String> symbols = new ArrayList<>();
        symbols.add(predicate);
        for (Term term : terms) {
            symbols.addAll(term.collectSymbols());
        }
        return symbols;
    }

    @Override
    public Literal copy() {
        List<Term> copiedTerms = new ArrayList<>();
        for (Term term : terms) {
            copiedTerms.add(term.copy());
        }
        return new Literal(isNegated, predicate, copiedTerms);
    }

    @Override
    public Literal applySubstitution(@NotNull Map<String, Term> substitutions) {
        List<Term> substitutedTerms = getTerms().stream()
                .map(term -> term.applySubstitution(substitutions))
                .toList();
        return new Literal(isNegated(), getPredicate(), substitutedTerms);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Literal other)) return false;
        return Objects.equals(isNegated, other.isNegated) &&
                Objects.equals(predicate, other.predicate) &&
                Objects.equals(terms, other.terms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isNegated, predicate, terms);
    }

    @Override
    public String toString() {
        return (isNegated ? NOT_SYMBOL : "") + predicate + "(" +
                String.join(", ", terms.stream().map(Term::toString).toList()) + ")";
    }

    public static Literal parse(@NotNull String input) {
        Parsing.checkEmptyLogicalStructure(input);

        input = Parsing.removeWhitespace(input);

        boolean isNegated = input.startsWith(NOT_SYMBOL);
        if (isNegated) {
            input = input.substring(1);
        }

        if (Parsing.dontContainsParentheses(input)) {
            return new Literal(isNegated, input);
        }

        int openParen = input.indexOf("(");
        int closeParen = input.lastIndexOf(")");
        String predicate = input.substring(0, openParen);
        String argsString = input.substring(openParen + 1, closeParen);

        List<Term> terms = Term.parseArguments(argsString);

        return new Literal(isNegated, predicate, terms);
    }
}
