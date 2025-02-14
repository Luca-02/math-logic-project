import model.Literal;
import model.Term;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Substitution {
    public static Term applySubstitution(Term term, Map<String, Term> substitutions) {
        if (term.isVariable()) {
            Term sub = substitutions.get(term.getName());
            return sub != null ? sub.clone() : term;
        } else {
            List<Term> args = term.getArguments().stream()
                    .map(arg -> applySubstitution(arg, substitutions))
                    .collect(Collectors.toList());
            return new Term(term.getName(), args);
        }
    }

    public static Literal applySubstitution(Literal lit, Map<String, Term> substitutions) {
        List<Term> substitutedTerms = lit.getTerms().stream()
                .map(term -> applySubstitution(term, substitutions))
                .collect(Collectors.toList());
        return new Literal(lit.isNegated(), lit.getPredicate(), substitutedTerms);
    }
}
