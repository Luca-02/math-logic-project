import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SortedCalculusR extends CalculusR {
    public SortedCalculusR() {
        super();
    }

    public SortedCalculusR(Set<Clause> clauses) {
        super(clauses);
    }

    @Override
    protected boolean factorizationCanBeApplied(Clause clause, Literal lit, Map<String, Term> mgu) {
        return mgu != null && isMaximal(lit, clause, mgu);
    }

    @Override
    protected boolean resolutionCanBeApplied(Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete, Map<String, Term> mgu) {
        return mgu != null &&
                isStrictlyMaximal(posToDelete, clauseWithPos, mgu) &&
                isMaximal(negToDelete, clauseWithNeg, mgu);
    }

    public boolean isStrictlyMaximal(Literal lit, Clause clause, Map<String, Term> mgu) {
        return false;
    }

    public boolean isMaximal(Literal lit, Clause clause, Map<String, Term> mgu) {
        return false;
    }
}
