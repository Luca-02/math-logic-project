package org.mathlogic.structure;

import java.util.List;

public interface LogicalStructure<T> {
    /**
     * Collect <b>all</b> the logical structure symbols.
     */
    List<String> collectSymbols();

    /**
     * Create a copy of the logical structure.
     */
    T copy();
}
