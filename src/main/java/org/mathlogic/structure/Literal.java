package org.mathlogic.structure;

import org.jetbrains.annotations.NotNull;
import org.mathlogic.exception.EmptyLogicalStructureException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mathlogic.Constant.NOT_SYMBOL;

/**
 * Identify a literal (atomic formula) P(t1, ..., tn),
 * where P is a predicate and each ti a term.
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
        return terms;
    }

    public boolean isIdentity() {
        return predicate.equals("=") && terms.size() == 2;
    }

    /**
     * Get the multiset view of the literals.
     */
    public Map<Term, Integer> getMultisetView() {
        Map<Term, Integer> multiset = new HashMap<>();

        if (isIdentity()) {
            multiset.put(terms.get(0), isNegated() ? 2 : 1);
            multiset.put(terms.get(1), isNegated() ? 2 : 1);
        } else {
            multiset.put(new Term(predicate, terms), isNegated() ? 2 : 1);
            multiset.put(Term.MINIMAL, isNegated() ? 2 : 1);
        }

        return multiset;
    }

    public Literal negate() {
        Literal clone = copy();
        clone.isNegated = !isNegated;
        return clone;
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
        List<Term> clonedTerms = new ArrayList<>();
        for (Term term : terms) {
            clonedTerms.add(term.copy());
        }
        return new Literal(isNegated, predicate, clonedTerms);
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
        if (input.isEmpty()) {
            throw new EmptyLogicalStructureException();
        }

        input = input.replaceAll("\\s+", "");

        boolean isNegated = input.startsWith("¬");
        if (isNegated) {
            input = input.substring(1);
        }

        if (!input.contains("(") || !input.contains(")")) {
            return new Literal(isNegated, input);
        }

        int openParen = input.indexOf("(");
        int closeParen = input.lastIndexOf(")");
        String predicate = input.substring(0, openParen);
        String argsString = input.substring(openParen + 1, closeParen);

        List<Term> terms = new ArrayList<>();
        int depth = 0;
        StringBuilder currentArg = new StringBuilder();
        for (char c : argsString.toCharArray()) {
            if (c == ',' && depth == 0) {
                terms.add(Term.parse(currentArg.toString().trim()));
                currentArg = new StringBuilder();
            } else {
                if (c == '(') depth++;
                if (c == ')') depth--;
                currentArg.append(c);
            }
        }

        if (!currentArg.isEmpty()) {
            terms.add(Term.parse(currentArg.toString().trim()));
        }

        return new Literal(isNegated, predicate, terms);
    }
}
