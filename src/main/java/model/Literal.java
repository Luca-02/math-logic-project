package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Literal {
    private final boolean isNegated;
    private final String predicate;
    private final List<Term> terms;

    public Literal(boolean isNegated, String predicate, List<Term> terms) {
        this.isNegated = isNegated;
        this.predicate = predicate;
        this.terms = terms;
    }

    public Literal(boolean isNegated, String predicate) {
        this(isNegated, predicate, new ArrayList<>());
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

    public Literal negate() {
        return new Literal(!isNegated, predicate, terms);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Literal other))
            return false;
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
        return (isNegated ? "¬" : "") + predicate + "(" +
                String.join(", ", terms.stream().map(Term::toString).toList()) + ")";
    }

    public static Literal parse(String input) {
        input = input.replaceAll("\\s+", "");

        boolean isNegated = input.startsWith("¬");
        if (isNegated) {
            input = input.substring(1);
        }

        if (!input.contains("(")) {
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
