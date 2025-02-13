package model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Clause {
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

    public boolean isEmpty() {
        return negativeLiterals.isEmpty() && positiveLiterals.isEmpty();
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
    public String toString() {
        return negativeLiterals.toString() + " => " + positiveLiterals.toString();
    }
}
