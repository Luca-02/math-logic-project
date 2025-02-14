import model.Clause;

import java.util.HashSet;
import java.util.Set;

public class ResolverR {
    private final Set<Clause> usable; // Us
    private final Set<Clause> worked; // Wo

    public ResolverR(Set<Clause> clauses) {
        this.usable = new HashSet<>(clauses);
        this.worked = new HashSet<>();
        reduceForward(usable);
    }

    public boolean resolve() {
        while (!usable.isEmpty()) {
            // 1. Select given clause (the first found)
            Clause given = selectGivenClause();

            worked.add(given);
            usable.remove(given);

            // 2. Generates new clauses by inferences between given clause and clauses in Wo and Us
            Set<Clause> newClauses = inferClauses(given, worked);

            // 3. Apply forward reductions on new clauses
            reduceForward(newClauses);

            // 4. Apply backwards reductions on olds clauses in Us and Wo with the new ones
            reduceBackward(usable, worked, newClauses);

            // 5. Add the new clauses to Us
            usable.addAll(newClauses);

            // Returns true if we found a refutation.
            if (refutationReached()) {
                return true;
            }
        }

        // Return false to indicate that it did not find a refutation, so it is satisfiable
        return false;
    }

    public boolean refutationReached() {
        return usable.stream().anyMatch(Clause::isEmpty);
    }

    /**
     * Select the minimum clause by number of literals
     */
    public Clause selectGivenClause() {
        Clause given = null;
        for (Clause clause : usable) {
            if (given == null || clause.compareTo(given) < 0) {
                given = clause;
            }
        }
        return given;
    }

    public Set<Clause> inferClauses(Clause given, Set<Clause> worked) {
        // TODO
        return null;
    }

    public static void reduceForward(Set<Clause> clauses) {
        clauses.removeIf(Clause::isTautology);
        // TODO: Si potrebbe applicare qui la sussunzione e la Matching Replacement Resolution
    }

    public static void reduceBackward(Set<Clause> usable, Set<Clause> worked, Set<Clause> newClauses) {
        // If a clause in Wo is subsumed by a clause in newClauses, remove it.
//        for (Clause newC : newClauses) {
//            usable.removeIf(existing -> newC.subsumes(existing));
//            worked.removeIf(existing -> newC.subsumes(existing));
//        }
    }
}
