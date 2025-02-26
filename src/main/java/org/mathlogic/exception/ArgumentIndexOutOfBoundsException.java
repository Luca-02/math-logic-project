package org.mathlogic.exception;

import org.mathlogic.structure.Term;

import java.util.List;

public class ArgumentIndexOutOfBoundsException extends RuntimeException {
    public ArgumentIndexOutOfBoundsException(int index, List<Term> arguments) {
        super(String.format("Index %s is out of bounds for arguments list of size %s.".formatted(index, arguments)));
    }
}
