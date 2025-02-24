package org.mathlogic;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.mathlogic.utility.MaximalLiteral;
import org.mathlogic.utility.Substitution;

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
        Clause subClauseWithPos = Substitution.applySubstitution(clauseWithPos, mgu);
        Literal subPosToDelete = Substitution.applySubstitution(posToDelete, mgu);

        Clause subClauseWithNeg = Substitution.applySubstitution(clauseWithNeg, mgu);
        Literal subNegToDelete = Substitution.applySubstitution(negToDelete, mgu);

        return MaximalLiteral.isStrictlyMaximal(subPosToDelete, subClauseWithPos) &&
                MaximalLiteral.isMaximal(subNegToDelete, subClauseWithNeg);
    }

    @Override
    protected boolean rightFactorizationCanBeApplied(
            Clause clause,
            Literal lit,
            @NotNull Map<String, Term> mgu
    ) {
        Clause subClause = Substitution.applySubstitution(clause, mgu);
        Literal subLit = Substitution.applySubstitution(lit, mgu);
        return MaximalLiteral.isMaximal(subLit, subClause);
    }
}
