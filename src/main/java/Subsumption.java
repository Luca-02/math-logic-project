import model.Clause;
import model.Literal;
import model.Term;

import java.util.Map;

public class Subsumption {
    /**
     * Check whether clause 1 subsumes clause 2.
     */
    public static boolean isSubsumed(Clause clause1, Clause clause2) {
        for (Literal lit1 : clause1.getAllLiterals()) {
            for (Literal lit2 : clause2.getAllLiterals()) {
                Map<String, Term> substitution = Unification.match(lit1, lit2);

                if (checkSubsumption(clause1, clause2, substitution)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the literals set of clause1 (with a substitution)
     * is contained in the literals set of clause 2.
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
