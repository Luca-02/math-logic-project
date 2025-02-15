import model.Clause;
import model.Literal;
import model.Term;

import java.util.*;

public class ResolverR {
    private final Set<Clause> usable; // Us
    private final Set<Clause> worked; // Wo

    public ResolverR(Set<Clause> clauses) {
        this.usable = new HashSet<>(clauses);
        this.worked = new HashSet<>();
        reduceForward(usable);
    }

    public boolean refute() {
        if (refutationReached()) {
            return true;
        }

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

    /**
     * Check if we have reached a refutation
     */
    public boolean refutationReached() {
        return usable.stream().anyMatch(Clause::isEmpty);
    }

    public Set<Clause> inferClauses(Clause given, Set<Clause> worked) {
        Clause givenClone = given.clone();
        // Apply renomination to make sure that the tow clause have disjoint variables
        Renamer.renameClausesToDisjointVariable(given, givenClone);

        // Resolution on literals: from given (positive) to given (negative)
        Set<Clause> newClauses = new HashSet<>(resolveClauses(given, givenClone));

        for (Clause c : worked) {
            // Apply renomination to make sure that the tow clause have disjoint variables
            Renamer.renameClausesToDisjointVariable(c, given);

            // Resolution on literals: from given (positive) to c (negative)
            newClauses.addAll(resolveClauses(given, c));

            // Resolution on literals: from c (positive) to given (negative)
            newClauses.addAll(resolveClauses(c, given));
        }

        // Apply factorization on given clause
        newClauses.addAll(factorizeClause(given));

        return newClauses;
    }

    /**
     * Resolution of two clauses, possibly renamed to have disjoint variables
     */
    public Set<Clause> resolveClauses(Clause clauseWithPos, Clause clauseWithNeg) {
        Set<Clause> resolutions = new HashSet<>();
        for (Literal pos : clauseWithPos.getPositiveLiterals()) {
            for (Literal neg : clauseWithNeg.getNegativeLiterals()) {
                Clause resolvent = resolveClauses(clauseWithPos, clauseWithNeg, pos, neg);
                if (resolvent != null) {
                    resolutions.add(resolvent);
                }
            }
        }
        return resolutions;
    }

    /**
     * Resolution of two clauses with the given literals for which we have done the unification
     */
    public Clause resolveClauses(Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete) {
        Map<String, Term> mgu = Unifier.unify(posToDelete, negToDelete);

        if (mgu != null) {
            Clause clauseWithPosClone = clauseWithPos.clone();
            Clause clauseWithNegClone = clauseWithNeg.clone();
            clauseWithPosClone.getPositiveLiterals().remove(posToDelete);
            clauseWithNegClone.getNegativeLiterals().remove(negToDelete);

            Set<Literal> mergedLiterals = new HashSet<>();
            mergedLiterals.addAll(clauseWithPosClone.getNegativeLiterals());
            mergedLiterals.addAll(clauseWithNegClone.getNegativeLiterals());
            mergedLiterals.addAll(clauseWithPosClone.getPositiveLiterals());
            mergedLiterals.addAll(clauseWithNegClone.getPositiveLiterals());

            Clause resolvent = new Clause(mergedLiterals);
            return Substitution.applySubstitution(resolvent, mgu);
        }

        return null;
    }

    /**
     * Right factorization on a given clause
     */
    public Set<Clause> factorizeClause(Clause clause) {
        Set<Clause> factorizations = new HashSet<>();

        List<Literal> posList = new ArrayList<>(clause.getPositiveLiterals());
        for (int i = 0; i < posList.size(); i++) {
            for (int j = i + 1; j < posList.size(); j++) {
                var mgu = Unifier.unify(posList.get(i), posList.get(j));
                if (mgu != null) {
                    // By applying the substitution on the clause, it will automatically merge
                    // the literals A and B on which the unification has been done through the mgu
                    Clause factClause = Substitution.applySubstitution(clause, mgu);
                    factorizations.add(factClause);
                }
            }
        }

        return factorizations;
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
