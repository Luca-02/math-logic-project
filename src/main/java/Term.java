import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Term {
    private final String function;
    private final List<Term> arguments;

    public Term(String function, List<Term> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public Term(String function) {
        this(function, new ArrayList<>());
    }

    public String getFunction() {
        return function;
    }

    public List<Term> getArguments() {
        return arguments;
    }

    public boolean isVarOrConst() {
        return !isFunction() &&
                function.length() == 1 &&
                Character.isLowerCase(function.charAt(0));
    }

    public boolean isFunction() {
        return arguments != null && !arguments.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Term other))
            return false;
        return Objects.equals(function, other.function) &&
                Objects.equals(arguments, other.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, arguments);
    }

    @Override
    public String toString() {
        if (isVarOrConst()) return function;
        if (arguments == null || arguments.isEmpty()) return function;
        return function + "(" + String.join(", ", arguments.stream().map(Term::toString).toList()) + ")";
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