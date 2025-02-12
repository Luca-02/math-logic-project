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
}
