import model.Literal;
import model.Term;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Substitution {
    public static Literal applySubstitution(Literal lit, Map<String, Term> substitution) {
        List<Term> substitutedTerms = lit.getTerms().stream()
                .map(term -> applySubstitution(term, substitution))
                .collect(Collectors.toList());
        return new Literal(lit.isNegated(), lit.getPredicate(), substitutedTerms);
    }

    public static Term applySubstitution(Term term, Map<String, Term> substitution) {
        if (term.isVariable()) {
            Term sub = substitution.get(term.getName());
            return sub != null ? sub : term;
        } else {
            List<Term> args = term.getArguments().stream()
                    .map(arg -> applySubstitution(arg, substitution))
                    .collect(Collectors.toList());
            return new Term(term.getName(), args);
        }
    }
}
