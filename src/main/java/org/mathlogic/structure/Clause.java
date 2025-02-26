package org.mathlogic.structure;

import org.mathlogic.utility.MaximalLiteral;
import org.mathlogic.utility.Parsing;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mathlogic.Constant.CLAUSE_LITERALS_DIVISOR;

/**
 * Represent a clause, a disjunction of literals.
 * It's represented by the two sets of negatives and positives literals.
 */
public class Clause implements LogicalStructure<Clause>, Comparable<Clause> {
    private enum LiteralState {
        NEGATIVE,
        POSITIVE,
        MAXIMAL_NEGATIVE,
        MAXIMAL_POSITIVE
    }

    /**
     * Literals map where key is the negation state and the value the set of literals.
     */
    private final Map<LiteralState, Set<Literal>> literalsMap;

    public Clause(@NotNull Set<Literal> literals) {
        this.literalsMap = new EnumMap<>(LiteralState.class);
        setLiterals(literals);
    }

    public Clause(@NotNull Literal... literals) {
        this(Arrays.stream(literals).collect(Collectors.toSet()));
    }

    private void setLiterals(Set<Literal> literals) {
        setLiteralsMap(literals, literalsMap, false);
        Set<Literal> maximalLiteral = MaximalLiteral.getMaximalLiterals(this);
        setLiteralsMap(maximalLiteral, literalsMap, true);
    }

    private void setLiteralsMap(
            Set<Literal> literals,
            Map<LiteralState, Set<Literal>> map,
            boolean isMaximalLiteralsSet
    ) {
        Set<Literal> negativeLiterals = new HashSet<>();
        Set<Literal> positiveLiterals = new HashSet<>();

        for (Literal lit : literals) {
            if (lit.isNegated()) {
                negativeLiterals.add(lit);
            } else {
                positiveLiterals.add(lit);
            }
        }

        LiteralState negativeState = isMaximalLiteralsSet ?
                LiteralState.MAXIMAL_NEGATIVE : LiteralState.NEGATIVE;
        LiteralState positiveState = isMaximalLiteralsSet ?
                LiteralState.MAXIMAL_POSITIVE : LiteralState.POSITIVE;

        map.put(negativeState, negativeLiterals);
        map.put(positiveState , positiveLiterals);
    }

    public void replace(Clause replacement) {
        setLiterals(replacement.getAllLiterals());
    }

    /**
     * Transform all the clause literals with the identity predicate, if they aren't already.
     */
    public Clause formatLiteralsWrtIdentity() {
        Set<Literal> formattedLit = new HashSet<>();
        for (Literal lit : getAllLiterals()) {
            formattedLit.add(lit.formatWrtIdentity());
        }
        return new Clause(formattedLit);
    }

    public Set<Literal> getNegativeLiterals() {
        return Collections.unmodifiableSet(literalsMap.get(LiteralState.NEGATIVE));
    }

    public Set<Literal> getPositiveLiterals() {
        return Collections.unmodifiableSet(literalsMap.get(LiteralState.POSITIVE));
    }

    public Set<Literal> getAllLiterals() {
        Set<Literal> allLiterals = new HashSet<>();
        allLiterals.addAll(getNegativeLiterals());
        allLiterals.addAll(getPositiveLiterals());
        return Collections.unmodifiableSet(allLiterals);
    }

    public Set<Literal> getMaximalNegativeLiterals() {
        return Collections.unmodifiableSet(literalsMap.get(LiteralState.MAXIMAL_NEGATIVE));
    }

    public Set<Literal> getMaximalPositiveLiterals() {
        return Collections.unmodifiableSet(literalsMap.get(LiteralState.MAXIMAL_POSITIVE));
    }

    public void addLiteral(Literal lit) {
        LiteralState literalState = getLiteralState(lit);
        LiteralState maximalLiteralState = getMaximalLiteralState(lit);
        literalsMap.get(literalState).add(lit);
        if (MaximalLiteral.isMaximal(lit, this)) {
            literalsMap.get(maximalLiteralState).add(lit);
        }
    }

    public void removeLiteral(Literal lit) {
        LiteralState literalState = getLiteralState(lit);
        LiteralState maximalLiteralState = getMaximalLiteralState(lit);
        literalsMap.get(literalState).remove(lit);
        literalsMap.get(maximalLiteralState).remove(lit);
    }

    private LiteralState getLiteralState(Literal lit) {
        return lit.isNegated() ? LiteralState.NEGATIVE : LiteralState.POSITIVE;
    }

    private LiteralState getMaximalLiteralState(Literal lit) {
        return lit.isNegated() ? LiteralState.MAXIMAL_NEGATIVE : LiteralState.MAXIMAL_POSITIVE;
    }

    public boolean isTautology() {
        for (Literal lit : getAllLiterals()) {
            if (lit.isTautology() || getAllLiterals().contains(lit.negate())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return getAllLiterals().isEmpty();
    }

    @Override
    public List<String> collectSymbols() {
        List<String> symbols = new ArrayList<>();
        for (Literal lit : getAllLiterals()) {
            symbols.addAll(lit.collectSymbols());
        }
        return symbols;
    }

    @Override
    public Clause copy() {
        Set<Literal> copiedLiterals = new HashSet<>();
        for (Literal lit : getAllLiterals()) {
            copiedLiterals.add(lit.copy());
        }
        return new Clause(copiedLiterals);
    }

    @Override
    public Clause applySubstitution(@NotNull Map<String, Term> substitutions) {
        Set<Literal> literals = new HashSet<>();
        for (Literal lit : getAllLiterals()) {
            literals.add(lit.applySubstitution(substitutions));
        }
        return new Clause(literals);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Clause other)) return false;
        return Objects.equals(getNegativeLiterals(), other.getNegativeLiterals()) &&
                Objects.equals(getPositiveLiterals(), other.getPositiveLiterals());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNegativeLiterals(), getPositiveLiterals());
    }

    /**
     * Compare clauses by number of symbols.
     */
    @Override
    public int compareTo(Clause o) {
        return Integer.compare(collectSymbols().size(), o.collectSymbols().size());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        Iterator<Literal> negativeLiteralsIterator = getNegativeLiterals().iterator();
        while(negativeLiteralsIterator.hasNext()) {
            builder.append(negativeLiteralsIterator.next().toString().substring(1));
            if (negativeLiteralsIterator.hasNext()) {
                builder.append(", ");
            }
        }

        builder.append(String.format(" %s ", CLAUSE_LITERALS_DIVISOR));

        Iterator<Literal> positiveLiteralsIterator = getPositiveLiterals().iterator();
        while(positiveLiteralsIterator.hasNext()) {
            builder.append(positiveLiteralsIterator.next().toString());
            if (positiveLiteralsIterator.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString().trim();
    }

    public static Clause parse(@NotNull String input) {
        Parsing.checkEmptyLogicalStructure(input);

        input = Parsing.removeWhitespace(input);

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
