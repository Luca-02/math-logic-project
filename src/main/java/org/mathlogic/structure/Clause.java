package org.mathlogic.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mathlogic.Constant.CLAUSE_LITERALS_DIVISOR;

/**
 * Represent a clause, a disjunction of literals.
 * It's represented by the two sets of negatives and positives literals.
 */
public class Clause implements LogicalStructure<Clause>, Comparable<Clause> {
    private final Set<Literal> negativeLiterals;
    private final Set<Literal> positiveLiterals;

    public Clause(Set<Literal> literals) {
        this.negativeLiterals = new HashSet<>();
        this.positiveLiterals = new HashSet<>();
        for (Literal lit : literals) {
            if (lit.isNegated()) {
                negativeLiterals.add(lit);
            } else {
                positiveLiterals.add(lit);
            }
        }
    }

    public Clause(Literal... literals) {
        this(Arrays.stream(literals).collect(Collectors.toSet()));
    }

    public Set<Literal> getNegativeLiterals() {
        return negativeLiterals;
    }

    public Set<Literal> getPositiveLiterals() {
        return positiveLiterals;
    }

    public Set<Literal> getAllLiterals() {
        Set<Literal> allLiterals = new HashSet<>();
        allLiterals.addAll(negativeLiterals);
        allLiterals.addAll(positiveLiterals);
        return allLiterals;
    }

    public boolean isTautology() {
        for (Literal lit : positiveLiterals) {
            if (negativeLiterals.contains(lit.negate())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return negativeLiterals.isEmpty() && positiveLiterals.isEmpty();
    }

    public List<String> collectSymbols() {
        List<String> symbols = new ArrayList<>();
        for (Literal lit : negativeLiterals) {
            symbols.addAll(lit.collectSymbols());
        }
        for (Literal lit : positiveLiterals) {
            symbols.addAll(lit.collectSymbols());
        }
        return symbols;
    }

    public void replace(Clause clause) {
        negativeLiterals.clear();
        negativeLiterals.addAll(clause.getNegativeLiterals());
        positiveLiterals.clear();
        positiveLiterals.addAll(clause.getPositiveLiterals());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Clause other)) return false;
        return Objects.equals(negativeLiterals, other.negativeLiterals) &&
                Objects.equals(positiveLiterals, other.positiveLiterals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(negativeLiterals, positiveLiterals);
    }

    @Override
    public Clause copy() {
        Set<Literal> clonedLiterals = new HashSet<>();
        for (Literal lit : negativeLiterals) {
            clonedLiterals.add(lit.copy());
        }
        for (Literal lit : positiveLiterals) {
            clonedLiterals.add(lit.copy());
        }
        return new Clause(clonedLiterals);
    }

    @Override
    public int compareTo(Clause o) {
        // Compare by number of symbols
        return Integer.compare(collectSymbols().size(), o.collectSymbols().size());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        Iterator<Literal> negativeLiteralsIterator = negativeLiterals.iterator();
        while(negativeLiteralsIterator.hasNext()) {
            builder.append(negativeLiteralsIterator.next().toString().substring(1));
            if (negativeLiteralsIterator.hasNext()) {
                builder.append(", ");
            }
        }

        builder.append(String.format(" %s ", CLAUSE_LITERALS_DIVISOR));

        Iterator<Literal> positiveLiteralsIterator = positiveLiterals.iterator();
        while(positiveLiteralsIterator.hasNext()) {
            builder.append(positiveLiteralsIterator.next().toString());
            if (positiveLiteralsIterator.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString().trim();
    }

    public static Clause parse(String input) {
        if (input == null || input.isEmpty()) {
            throw new RuntimeException("Clause cannot be empty");
        }

        input = input.replaceAll("\\s+", "");

        String[] parts = input.split(CLAUSE_LITERALS_DIVISOR, 2);

        Set<Literal> literals = new HashSet<>();

        if (!parts[0].isEmpty()) {
            List<String> negativeLiteralsStr = splitLiterals(parts[0]);
            for (String litStr : negativeLiteralsStr) {
                literals.add(Literal.parse(litStr).negate());
            }
        }

        if (parts.length > 1 && !parts[1].isEmpty()) {
            List<String> positiveLiteralsStr = splitLiterals(parts[1]);
            for (String litStr : positiveLiteralsStr) {
                literals.add(Literal.parse(litStr));
            }
        }

        return new Clause(literals);
    }

    private static List<String> splitLiterals(String input) {
        List<String> literals = new ArrayList<>();
        int depth = 0;
        StringBuilder currentLiteral = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == ',' && depth == 0) {
                literals.add(currentLiteral.toString().trim());
                currentLiteral = new StringBuilder();
            } else {
                if (c == '(') depth++;
                if (c == ')') depth--;
                currentLiteral.append(c);
            }
        }

        if (!currentLiteral.isEmpty()) {
            literals.add(currentLiteral.toString().trim());
        }

        return literals;
    }
}
