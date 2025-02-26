package org.mathlogic;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.mathlogic.utility.MaximalLiteral;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

public class SortedCalculus extends CalculusR {
    @Override
    protected Set<Literal> getPossibleSolvablePositiveLiterals(Clause clause) {
        return clause.getMaximalPositiveLiterals();
    }

    @Override
    protected Set<Literal> getPossibleSolvableNegativeLiterals(Clause clause) {
        return clause.getMaximalNegativeLiterals();
    }

    @Override
    protected Set<Literal> getPossibleFactorizableLiterals(Clause clause) {
        return clause.getMaximalPositiveLiterals();
    }

    @Override
    protected boolean resolutionCanBeApplied(
            Clause clauseWithPos,
            Clause clauseWithNeg,
            Literal posToDelete,
            Literal negToDelete,
            @NotNull Map<String, Term> mgu
    ) {
        Clause subClauseWithPos = clauseWithPos.applySubstitution(mgu);
        Clause subClauseWithNeg = clauseWithNeg.applySubstitution(mgu);
        Literal subPosToDelete = posToDelete.applySubstitution(mgu);
        Literal subNegToDelete = negToDelete.applySubstitution(mgu);
        return MaximalLiteral.isStrictlyMaximal(subPosToDelete, subClauseWithPos) &&
                MaximalLiteral.isMaximal(subNegToDelete, subClauseWithNeg);
    }

    @Override
    protected boolean rightFactorizationCanBeApplied(
            Clause clause,
            Literal lit,
            @NotNull Map<String, Term> mgu
    ) {
        Clause subClause = clause.applySubstitution(mgu);
        Literal subLit = lit.applySubstitution(mgu);
        return MaximalLiteral.isMaximal(subLit, subClause);
    }
}
