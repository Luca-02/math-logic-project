import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.Map;

public class DefaultCalculusR extends CalculusR {
    @Override
    protected boolean factorizationCanBeApplied(
            Clause clause,
            Literal lit,
            Map<String, Term> mgu
    ) {
        return true;
    }

    @Override
    protected boolean resolutionCanBeApplied(
            Clause clauseWithPos,
            Clause clauseWithNeg,
            Literal posToDelete,
            Literal negToDelete,
            Map<String, Term> mgu
    ) {
        return true;
    }
}
