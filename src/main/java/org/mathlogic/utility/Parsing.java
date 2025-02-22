package org.mathlogic.utility;

import org.mathlogic.exception.ParsingEmptyLogicalStructureException;

public class Parsing {
    public static void checkEmptyLogicalStructure(
            String input
    ) throws ParsingEmptyLogicalStructureException {
        if (input.isEmpty()) {
            throw new ParsingEmptyLogicalStructureException();
        }
    }

    public static String removeWhitespace(String input) {
        return input.replaceAll("\\s+", "");
    }

    public static boolean dontContainsParentheses(String input) {
        return !input.contains("(") || !input.contains(")");
    }
}
