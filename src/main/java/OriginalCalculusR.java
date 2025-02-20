import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.*;

public class OriginalCalculusR extends CalculusR {
    public OriginalCalculusR() {
        super();
    }

    public OriginalCalculusR(Set<Clause> clauses) {
        super(clauses);
    }

    @Override
    public Clause resolveClauses(Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete) {
        Map<String, Term> mgu = Unification.unify(posToDelete, negToDelete);

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

    @Override
    public Set<Clause> factorizeClause(Clause clause) {
        Set<Clause> factorizations = new HashSet<>();

        List<Literal> posList = new ArrayList<>(clause.getPositiveLiterals());
        for (int i = 0; i < posList.size(); i++) {
            for (int j = i + 1; j < posList.size(); j++) {
                Map<String, Term> mgu = Unification.unify(posList.get(i), posList.get(j));
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
}
