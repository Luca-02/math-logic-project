package org.mathlogic.structure;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface LogicalStructure<T> extends Copyable<T> {
    /**
     * Collect <b>all</b> the logical structure symbols.
     */
    List<String> collectSymbols();

    /**
     * Apply a given substitution to a structure.
     */
    T applySubstitution(@NotNull Map<String, Term> substitutions);
}
