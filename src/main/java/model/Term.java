package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static model.Constant.VARIABLE_IDENTIFIER;

/**
 * Identify a term, so a variable or a function.
 * A special function of arity 0 is a constant.
 */
public class Term implements Cloneable {
    private final String name;
    private final List<Term> arguments;

    public Term(String name, List<Term> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public Term(String name, Term... arguments) {
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

    public boolean occurIn(Term term) {
        if (term.isVariable()) {
            return equals(term);
        } else {
            if (equals(term)) {
                return true;
            }
            for (Term arg : term.getArguments()) {
                if (occurIn(arg)) {
                    return true;
                }
            }
            return false;
        }
    }

    public List<String> collectSymbols() {
        List<String> symbols = new ArrayList<>();
        symbols.add(name);
        for (Term arg : arguments) {
            symbols.addAll(arg.collectSymbols());
        }
        return symbols;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Term other))
            return false;
        return Objects.equals(name, other.name) &&
                Objects.equals(arguments, other.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }

    @Override
    public Term clone() {
        try {
            Term cloned = (Term) super.clone();
            List<Term> clonedArguments = new ArrayList<>();
            for (Term arg : arguments) {
                clonedArguments.add(arg.clone());
            }
            return new Term(cloned.name, clonedArguments);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        if (isVariable()) return name;
        if (arguments == null || arguments.isEmpty()) return name;
        return name + "(" + String.join(", ", arguments.stream().map(Term::toString).toList()) + ")";
    }

    public static Term parse(String input) {
        // Delete space
        input = input.replaceAll("\\s+", "");

        // Variable or constant
        if (!input.contains("(")) {
            return new Term(input);
        }

        int openParen = input.indexOf("(");
        int closeParen = input.lastIndexOf(")");
        String functionName = input.substring(0, openParen);
        String argsString = input.substring(openParen + 1, closeParen);
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

        return new Term(functionName, arguments);
    }
}