import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Clause {
    private final Set<Literal> literals;

    public Clause(Literal... literals) {
        this.literals = new HashSet<>(Arrays.asList(literals));
    }

    public Set<Literal> getLiterals() {
        return literals;
    }

    public boolean isEmpty() {
        return literals.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Clause other))
            return false;
        return Objects.equals(literals, other.literals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals);
    }

    @Override
    public String toString() {
        return literals.toString();
    }
}
