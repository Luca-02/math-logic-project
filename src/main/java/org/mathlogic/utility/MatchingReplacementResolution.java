package org.mathlogic.utility;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;

import javax.validation.constraints.NotNull;
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
    public static Clause apply(@NotNull Clause reference, @NotNull Clause target) {
        for (Literal lit1 : reference.getPositiveLiterals()) {
            for (Literal lit2 : target.getNegativeLiterals()) {
                Map<String, Term> sigma = Unification.match(lit1, lit2);

                if (sigma != Unification.INVALID_SUBSTITUTION &&
                        checkMatchingReplacementResolution(reference, target, lit1, lit2, sigma)) {
                    Clause updated = target.copy();
                    updated.removeLiteral(lit2);
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
            Clause clause1,
            Clause clause2,
            Literal lit1,
            Literal lit2,
            @NotNull Map<String, Term> substitution
    ) {
        Literal subLit1 = lit1.applySubstitution(substitution);

        Clause cleanClause1 = clause1.copy();
        cleanClause1.removeLiteral(lit1);
        Clause subCleanClause1 = cleanClause1.applySubstitution(substitution);

        Clause cleanClause2 = clause2.copy();
        cleanClause2.removeLiteral(lit2);

        return subLit1.equals(lit2.negate()) &&
                cleanClause2.getNegativeLiterals().containsAll(subCleanClause1.getNegativeLiterals()) &&
                cleanClause2.getPositiveLiterals().containsAll(subCleanClause1.getPositiveLiterals());
    }
}
