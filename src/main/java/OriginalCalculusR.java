import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.Map;
import java.util.Set;

public class OriginalCalculusR extends CalculusR {
    public OriginalCalculusR() {
        super();
    }

    public OriginalCalculusR(Set<Clause> clauses) {
        super(clauses);
    }

    @Override
    protected boolean factorizationCanBeApplied(Clause clause, Literal lit, Map<String, Term> mgu) {
        return mgu != null;
    }

    @Override
    protected boolean resolutionCanBeApplied(
            Clause clauseWithPos,
            Clause clauseWithNeg,
            Literal posToDelete,
            Literal negToDelete,
            Map<String, Term> mgu
    ) {
        return mgu != null;
    }
}
