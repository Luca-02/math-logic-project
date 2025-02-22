package org.mathlogic.exception;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;

public class LiteralNotFoundInClauseException extends RuntimeException {
    public LiteralNotFoundInClauseException(Literal lit, Clause clause) {
        super(String.format("Literal %s not found in clause %s", lit, clause));
    }
}
