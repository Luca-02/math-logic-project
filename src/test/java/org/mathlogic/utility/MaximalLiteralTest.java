package org.mathlogic.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.exception.LiteralNotFoundInClauseException;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximalLiteralTest {
    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForGetMaximalLiterals")
    void testGetMaximalLiterals(Clause clause, Set<Literal> expected) {
        Set<Literal> result = MaximalLiteral.getMaximalLiterals(clause);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> lit={0}, clauses={1}, strictlyMaximal={2}, expected={3}")
    @MethodSource("provideParametersForIsMaximal")
    void testIsMaximal(Literal lit, Clause clause, boolean strictlyMaximal, boolean expected) {
        boolean result;
        if (strictlyMaximal) {
            result = MaximalLiteral.isStrictlyMaximal(lit, clause);
        } else {
            result = MaximalLiteral.isMaximal(lit, clause);
        }

        assertEquals(expected, result);
    }

    @Test
    void testIsMaximalException() {
        Literal lit = Literal.parse("c(?x)");
        Clause clause = Clause.parse("c(?x), =(e(?x), d(?x)) => =(e(?y), e(a))");
        assertThrows(
                LiteralNotFoundInClauseException.class,
                () -> MaximalLiteral.isMaximal(lit, clause)
        );
    }

    Stream<Arguments> provideParametersForGetMaximalLiterals() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=>"),
                        Set.of()
                ),
                Arguments.of(
                        Clause.parse("=(e(b), d(b)) => =(e(b), e(a))"),
                        Set.of(Literal.parse("¬=(e(b), d(b))"))
                ),
                Arguments.of(
                        Clause.parse("b(?x), =(d(?x), c(?x)) => =(d(?y), c(a))"),
                        Set.of(Literal.parse("¬=(d(?x), c(?x))"), Literal.parse("=(d(?y), c(a))"))
                ),
                Arguments.of(
                        Clause.parse("=> =(?x, f(?y)), =(?y, f(?x))"),
                        Set.of(Literal.parse("=(?x, f(?y))"), Literal.parse("=(?y, f(?x))"))
                ),
                Arguments.of(
                        Clause.parse("=> =(b(?x), ?w), =(b(?x), ?u), =(?w, ?u)"),
                        Set.of(Literal.parse("=(b(?x), ?u)"), Literal.parse("=(b(?x), ?w)"))
                ),
                Arguments.of(
                        Clause.parse("=> =(b(?x), ?v), =(?w, b(?x))"),
                        Set.of(Literal.parse("=(b(?x), ?v)"), Literal.parse("=(?w, b(?x))"))
                )
        );
    }

    Stream<Arguments> provideParametersForIsMaximal() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("¬c(?x)"),
                        Clause.parse("c(?x), =(e(?x), d(?x)) => =(e(?y), e(a))"),
                        false,
                        false
                ),
                Arguments.of(
                        Literal.parse("¬=(e(?x), d(?x))"),
                        Clause.parse("c(?x), =(e(?x), d(?x)) => =(e(?y), d(a))"),
                        false,
                        true
                ),
                Arguments.of(
                        Literal.parse("=(e(?y), d(a))"),
                        Clause.parse("c(?x), =(e(?x), d(?x)) => =(e(?y), d(a))"),
                        true,
                        false
                ),
                Arguments.of(
                        Literal.parse("=(e(?y), e(a))"),
                        Clause.parse("c(?x), =(e(?x), d(?x)) => =(e(?y), e(a))"),
                        false,
                        true
                ),
                Arguments.of(
                        Literal.parse("¬=(e(?x), d(?x))"),
                        Clause.parse("c(?x), =(e(?x), d(?x)) => =(e(?y), e(a))"),
                        true,
                        false
                ),
                Arguments.of(
                        Literal.parse("=(e(?y), d(a))"),
                        Clause.parse("c(?x), =(e(?x), d(?x)) => =(e(?y), d(a))"),
                        true,
                        false
                ),
                Arguments.of(
                        Literal.parse("¬=(e(b), d(b))"),
                        Clause.parse("=(e(b), d(b)) => =(e(b), e(a))"),
                        true,
                        true
                ),
                Arguments.of(
                        Literal.parse("=(e(b), e(a))"),
                        Clause.parse("=(e(b), d(b)) => =(e(b), e(a))"),
                        false,
                        false
                ),
                Arguments.of(
                        Literal.parse("=(e(b), e(a))"),
                        Clause.parse("=(e(b), d(b)) => =(e(b), e(a))"),
                        true,
                        false
                ),
                Arguments.of(
                        Literal.parse("=(b(?x), ?u)"),
                        Clause.parse("=> =(b(?x), ?w), =(b(?x), ?u), =(?w, ?u)"),
                        false,
                        true
                ),
                Arguments.of(
                        Literal.parse("=(b(?x), ?u)"),
                        Clause.parse("=> =(b(?x), ?w), =(b(?x), ?u), =(?w, ?u)"),
                        true,
                        true
                ),
                Arguments.of(
                        Literal.parse("=(b(?x), ?v)"),
                        Clause.parse("=> =(?v, ?w), =(b(?x), ?v), =(?w, b(?x))"),
                        true,
                        true
                )
        );
    }
}