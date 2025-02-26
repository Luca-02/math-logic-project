package org.mathlogic.utility;

import org.mathlogic.structure.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mathlogic.Constant.RENAMING_VARIABLE_SYMBOL;

public class Renaming {
    /**
     * Rename the clause variables so that they have disjoint variables
     * between them.
     */
    public static void renameClausesToDisjointVariable(
            @NotNull Clause original,
            @NotNull Clause toRename
    ) {
        Map<String, Term> substitutions = getSubstitutionForDisjointVariables(original, toRename);
        toRename.replace(toRename.applySubstitution(substitutions));
    }

    /**
     * Rename the logical structure variables so that all the variable in it have the same default value,
     * so we can treat all the variable as the same.
     */
    @SuppressWarnings("unchecked")
    public static <T extends LogicalStructure<?>> T renameLogicalStructureToSameVariable(
            @NotNull T structure
    ) {
        Set<String> clauseVariables = getAllVariables(structure);
        Map<String, Term> substitutions = new HashMap<>();

        for (String clauseVar : clauseVariables) {
            substitutions.put(clauseVar, Term.DEFAULT_VARIABLE);
        }

        return (T) structure.applySubstitution(substitutions);
    }

    /**
     * Create a substitution map for two clause so that, if applied,
     * they have disjoint variables between them.
     */
    private static Map<String, Term> getSubstitutionForDisjointVariables(
            Clause original,
            Clause toRename
    ) {
        Set<String> originalVariables = getAllVariables(original);
        Set<String> toRenameVariables = getAllVariables(toRename);

        Map<String, Term> substitutions = new HashMap<>();
        for (String originalVar : originalVariables) {
            for (String toRenameVar : toRenameVariables) {
                if (originalVar.equals(toRenameVar) && !substitutions.containsKey(toRenameVar)) {
                    substitutions.put(toRenameVar, new Term(toRenameVar + RENAMING_VARIABLE_SYMBOL));
                }
            }
        }
        return substitutions;
    }

    /**
     * Get the set of variables of a logical structure.
     */
    private static Set<String> getAllVariables(LogicalStructure<?> structure) {
        Set<String> clauseVariables = new HashSet<>(structure.collectSymbols());
        clauseVariables.removeIf(symbol -> symbol.charAt(0) != '?');
        return clauseVariables;
    }
}
