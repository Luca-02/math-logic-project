package org.mathlogic.structure;

public interface Copyable<T> {
    /**
     * Create a copy of the logical structure.
     */
    T copy();
}
