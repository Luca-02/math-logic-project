import structure.Clause;
import structure.Literal;
import structure.Term;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class SortedCalculusR extends CalculusR {
    @Override
    protected boolean factorizationCanBeApplied(
            Clause clause,
            Literal lit,
            Map<String, Term> mgu
    ) {
        Clause subClause = Substitution.applySubstitution(clause, mgu);
        Literal subLit = Substitution.applySubstitution(lit, mgu);
        return MaximalLiteral.isMaximal(subLit, subClause, false);
    }

    @Override
    protected boolean resolutionCanBeApplied(
            Clause clauseWithPos,
            Clause clauseWithNeg,
            Literal posToDelete,
            Literal negToDelete,
            Map<String, Term> mgu
    ) {
        Clause subClauseWithPos = Substitution.applySubstitution(clauseWithPos, mgu);
        Literal subPosToDelete = Substitution.applySubstitution(posToDelete, mgu);

        Clause subClauseWithNeg = Substitution.applySubstitution(clauseWithNeg, mgu);
        Literal subNegToDelete = Substitution.applySubstitution(negToDelete, mgu);

        return MaximalLiteral.isMaximal(subPosToDelete, subClauseWithPos, true) &&
                MaximalLiteral.isMaximal(subNegToDelete, subClauseWithNeg, false);
    }
}
