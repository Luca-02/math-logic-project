import org.mathlogic.AutomaticCalculus;
import org.mathlogic.structure.Clause;

import java.util.Set;

public class CalculusS extends AutomaticCalculus {
    @Override
    protected void initialReduction() {

    }

    @Override
    protected Set<Clause> inferAllPossibleClausesFromItself(Clause given) {
        return Set.of();
    }

    @Override
    protected Set<Clause> inferAllPossibleClausesFromWorkedClause(Clause given, Clause clauseWo) {
        return Set.of();
    }

    @Override
    protected void forwardReduction(Set<Clause> newClauses) {

    }

    @Override
    protected void backwardsReduction(Set<Clause> newClauses) {

    }
}
