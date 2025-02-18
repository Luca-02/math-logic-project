import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.Map;

public class Subsumption {
    /**
     * Check whether {@code clause1} subsumes {@code clause2}. The clause {@code Γ => ∆} subsumes the clause
     * {@code Γ' => ∆'} iff for a matcher {@code σ} we have {@code Γσ ⊆ Γ'} and {@code ∆σ ⊆ ∆'}.
     */
    public static boolean isSubsumed(Clause clause1, Clause clause2) {
        for (Literal lit1 : clause1.getAllLiterals()) {
            for (Literal lit2 : clause2.getAllLiterals()) {
                Map<String, Term> sigma = Unification.match(lit1, lit2);

                if (checkSubsumption(clause1, clause2, sigma)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the literals set of {@code clause1} (with a substitution)
     * is contained in the literals set of {@code clause2}.
     */
    private static boolean checkSubsumption(Clause clause1, Clause clause2, Map<String, Term> substitution) {
        if (substitution != null) {
            Clause subClause1 = Substitution.applySubstitution(clause1, substitution);
            return clause2.getNegativeLiterals().containsAll(subClause1.getNegativeLiterals()) &&
                    clause2.getPositiveLiterals().containsAll(subClause1.getPositiveLiterals());
        }
        return false;
    }
}
