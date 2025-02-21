package org.mathlogic.structure;

import java.util.List;

public interface LogicalStructure<T> {
    List<String> collectSymbols();
    T copy();
}
