import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.Map;

public class MatchingReplacementResolution {
    /**
     * Implement <b>Matching Replacement Resolution</b> on two given clauses {@code Γ1 => ∆1, A1} (reference)
     * and {@code Γ2, A2 => ∆2} (target). Let us also assume that there exists a matcher {@code σ} satisfying
     * the following conditions:
     * <ul>
     * <li> {@code A1σ ≡ A2} </li>
     * <li> {@code Γ1σ ⊆ Γ2} </li>
     * <li> {@code ∆1σ ⊆ ∆2} </li>
     * </ul>
     * Under these conditions we can (keeping the clause {@code Γ1 => ∆1, A1}) delete the clause
     * {@code Γ2, A2 => ∆2} and replace it with the simpler clause {@code Γ2 ⇒ ∆2}.
     * The method returns the clause {@code Γ2 => ∆2} with which to replace {@code Γ2, A2 => ∆2}.
     */
    public static Clause apply(Clause reference, Clause target) {
        for (Literal lit1 : reference.getPositiveLiterals()) {
            for (Literal lit2 : target.getNegativeLiterals()) {
                Map<String, Term> sigma = Unification.match(lit1, lit2);

                if (checkMatchingReplacementResolution(reference, target, lit1, lit2, sigma)) {
                    Clause updated = target.clone();
                    updated.getNegativeLiterals().remove(lit2);
                    return updated;
                }
            }
        }

        return null;
    }

    /**
     * Checks whether <b>Matching Replacement Resolution</b> is applicable or not on the two given
     * clause and the two found literal.
     */
    private static boolean checkMatchingReplacementResolution(
            Clause clause1, Clause clause2, Literal lit1, Literal lit2, Map<String, Term> substitution) {
        if (substitution != null) {
            Literal subLit1 = Substitution.applySubstitution(lit1, substitution);

            Clause cleanClause1 = clause1.clone();
            cleanClause1.getPositiveLiterals().remove(lit1);
            Clause subCleanClause1 = Substitution.applySubstitution(cleanClause1, substitution);

            Clause cleanClause2 = clause2.clone();
            cleanClause2.getNegativeLiterals().remove(lit2);

            return subLit1.equals(lit2.negate()) &&
                    cleanClause2.getNegativeLiterals().containsAll(subCleanClause1.getNegativeLiterals()) &&
                    cleanClause2.getPositiveLiterals().containsAll(subCleanClause1.getPositiveLiterals());
        }
        return false;
    }
}
