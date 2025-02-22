package org.mathlogic.exception;

public class ParsingEmptyLogicalStructureException extends RuntimeException {
    public ParsingEmptyLogicalStructureException() {
        super("Logical structure string representation cannot be empty");
    }
}
