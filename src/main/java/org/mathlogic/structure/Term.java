package org.mathlogic.structure;

import org.mathlogic.utility.Parsing;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mathlogic.Constant.VARIABLE_IDENTIFIER;

/**
 * Identify a term, so a variable or a function.
 * A special function of arity 0 is a constant.
 */
public class Term implements LogicalStructure<Term> {
    /**
     * Minimum term in the ordering of terms.
     */
    public static final Term MINIMAL = new Term("true");

    /**
     * Term's function name.
     */
    private final String name;
    private final List<Term> arguments;

    public Term(@NotNull String name, @NotNull List<Term> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public Term(@NotNull String name, @NotNull Term... arguments) {
        this(name, List.of(arguments));
    }

    public String getName() {
        return name;
    }

    public List<Term> getArguments() {
        return arguments;
    }

    public boolean isVariable() {
        return !isFunction();
    }

    public boolean isConstant() {
        return arguments.isEmpty() && !name.contains(VARIABLE_IDENTIFIER);
    }

    public boolean isFunction() {
        return !arguments.isEmpty() || !name.contains(VARIABLE_IDENTIFIER);
    }

    /**
     * Check if a {@code external} term is contained in this term.
     */
    public boolean occurIn(Term external) {
        if (external.isVariable()) {
            return equals(external);
        } else {
            if (equals(external)) {
                return true;
            }
            for (Term arg : external.getArguments()) {
                if (occurIn(arg)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public List<String> collectSymbols() {
        List<String> symbols = new ArrayList<>();
        symbols.add(name);
        for (Term arg : arguments) {
            symbols.addAll(arg.collectSymbols());
        }
        return symbols;
    }

    @Override
    public Term copy() {
        List<Term> clonedArguments = new ArrayList<>();
        for (Term arg : arguments) {
            clonedArguments.add(arg.copy());
        }
        return new Term(name, clonedArguments);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Term other)) return false;
        return Objects.equals(name, other.name) &&
                Objects.equals(arguments, other.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }

    @Override
    public String toString() {
        if (isVariable()) return name;
        if (arguments == null || arguments.isEmpty()) return name;
        return name + "(" + String.join(", ", arguments.stream().map(Term::toString).toList()) + ")";
    }

    public static Term parse(@NotNull String input) {
        Parsing.checkEmptyLogicalStructure(input);

        input = Parsing.removeWhitespace(input);

        if (Parsing.dontContainsParentheses(input)) {
            return new Term(input);
        }

        int openParen = input.indexOf("(");
        int closeParen = input.lastIndexOf(")");
        String functionName = input.substring(0, openParen);
        String argsString = input.substring(openParen + 1, closeParen);
        List<Term> arguments = parseArguments(argsString);

        return new Term(functionName, arguments);
    }

    public static List<Term> parseArguments(String argsString) {
        List<Term> arguments = new ArrayList<>();
        int depth = 0;
        StringBuilder currentArg = new StringBuilder();
        for (char c : argsString.toCharArray()) {
            if (c == ',' && depth == 0) {
                arguments.add(parse(currentArg.toString().trim()));
                currentArg = new StringBuilder();
            } else {
                if (c == '(') depth++;
                if (c == ')') depth--;
                currentArg.append(c);
            }
        }

        if (!currentArg.isEmpty()) {
            arguments.add(parse(currentArg.toString().trim()));
        }

        return arguments;
    }
}