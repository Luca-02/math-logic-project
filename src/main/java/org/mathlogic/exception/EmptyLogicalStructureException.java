package org.mathlogic.exception;

public class EmptyLogicalStructureException extends RuntimeException {
    public EmptyLogicalStructureException() {
        throw new RuntimeException("Logical structure cannot be empty");
    }
}
