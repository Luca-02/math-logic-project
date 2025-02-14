package model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represent a clause, a disjunction of literals.
 * It's represented by the two sets of negatives and positives literals.
 */
public class Clause implements Comparable<Clause> {
    private final Set<Literal> negativeLiterals;
    private final Set<Literal> positiveLiterals;

    public Clause(Literal... literals) {
        this.negativeLiterals = new HashSet<>();
        this.positiveLiterals = new HashSet<>();
        for (Literal lit : literals) {
            if (lit.isNegated()) {
                negativeLiterals.add(lit);
            } else {
                positiveLiterals.add(lit);
            }
        }
    }

    public Set<Literal> getNegativeLiterals() {
        return negativeLiterals;
    }

    public Set<Literal> getPositiveLiterals() {
        return positiveLiterals;
    }

    public int getArity() {
        return negativeLiterals.size() + positiveLiterals.size();
    }

    public boolean isTautology() {
        for (Literal lit : positiveLiterals) {
            if (negativeLiterals.contains(lit.negate())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return negativeLiterals.isEmpty() && positiveLiterals.isEmpty();
    }

    public Set<String> collectSymbols() {
        Set<String> symbols = new HashSet<>();
        for (Literal lit : negativeLiterals) {
            symbols.addAll(lit.collectSymbols());
        }
        for (Literal lit : positiveLiterals) {
            symbols.addAll(lit.collectSymbols());
        }
        return symbols;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Clause other))
            return false;
        return Objects.equals(negativeLiterals, other.negativeLiterals) &&
                Objects.equals(positiveLiterals, other.positiveLiterals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(negativeLiterals, positiveLiterals);
    }

    @Override
    public int compareTo(Clause o) {
        // Compare by total number of literals
        int compare = Integer.compare(getArity(), o.getArity());
        if (compare != 0) {
            return compare;
        }

        // Compare by number of distinct symbols
        return Integer.compare(collectSymbols().size(), o.collectSymbols().size());
    }

    @Override
    public String toString() {
        return negativeLiterals.toString() + " => " + positiveLiterals.toString();
    }
}
