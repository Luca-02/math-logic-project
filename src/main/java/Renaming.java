import structure.Clause;
import structure.Term;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static global.Constant.RENAMING_VARIABLE_SYMBOL;

public class Renaming {
    /**
     * Rename the clause variables so that they have disjoint variables
     * between them.
     */
    public static void renameClausesToDisjointVariable(Clause original, Clause toRename) {
        Map<String, Term> substitutions = getSubstitutionForDisjointVariables(original, toRename);

        if (substitutions != null) {
            toRename.replace(Substitution.applySubstitution(toRename, substitutions));
        }
    }

    /**
     * Create a substitution map for two clause so that, if applied,
     * they have disjoint variables between them.
     */
    public static Map<String, Term> getSubstitutionForDisjointVariables(Clause original, Clause toRename) {
        Set<String> originalVariables = new HashSet<>(original.collectSymbols());
        Set<String> toRenameVariables = new HashSet<>(toRename.collectSymbols());
        originalVariables.removeIf(symbol -> symbol.charAt(0) != '?');
        toRenameVariables.removeIf(symbol -> symbol.charAt(0) != '?');

        Map<String, Term> substitutions = new HashMap<>();
        for (String originalVar : originalVariables) {
            for (String toRenameVar : toRenameVariables) {
                if (originalVar.equals(toRenameVar) && !substitutions.containsKey(toRenameVar)) {
                    substitutions.put(toRenameVar, new Term(toRenameVar + RENAMING_VARIABLE_SYMBOL));
                }
            }
        }

        return substitutions.isEmpty() ? null : substitutions;
    }
}
