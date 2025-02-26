package org.mathlogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalculusSTest {
    private CalculusS resolver;

    @BeforeEach
    void setUp() {
        resolver = new CalculusS();
    }

    @ParameterizedTest(name = "{index} -> c1={0}, c2={1}, l1={2}, l2={3}, expected={4}")
    @MethodSource("provideParametersForApplyRightSuperposition")
    void testApplyRightSuperposition(Clause c1, Clause c2, Literal l1, Literal l2, Clause expected) {
        Clause result = resolver.applyLeftOrRightSuperposition(c1, c2, l1, l2, false);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> c1={0}, c2={1}, l1={2}, l2={3}, expected={4}")
    @MethodSource("provideParametersForApplyLeftSuperposition")
    void testApplyLeftSuperposition(Clause c1, Clause c2, Literal l1, Literal l2, Clause expected) {
        Clause result = resolver.applyLeftOrRightSuperposition(c1, c2, l1, l2, true);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clause={0}, lit={1}, expected={2}")
    @MethodSource("provideParametersForApplyEqualityResolution")
    void testApplyEqualityResolution(Clause clause, Literal lit, Clause expected) {
        Clause result = resolver.applyEqualityResolution(clause, lit);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clause={0}, lit1={1}, lit2={2}, expected={3}")
    @MethodSource("provideParametersForApplyEqualityFactorization")
    void testApplyEqualityFactorization(Clause clause, Literal lit1, Literal lit2, Clause expected) {
        Clause result = resolver.applyEqualityFactorization(clause, lit1, lit2);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForRefute")
    void testRefute(Set<Clause> clauses, boolean expected) {
        boolean result = resolver.refute(clauses);

        assertEquals(expected, result);
    }

    Stream<Arguments> provideParametersForApplyRightSuperposition() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=> =(?u, ?v), =(?w, ?u), =(?v, ?w)"),
                        Clause.parse("=> =(a(?x', b(?x')), true)"),
                        Literal.parse("=(?u, ?v)"),
                        Literal.parse("=(a(?x', b(?x')), true)"),
                        Clause.parse("=> =(?v, ?w), =(a(?x', ?v), true), =(?w, b(?x'))")
                ),
                Arguments.of(
                        Clause.parse("=> =(?w, b(b(?x))), =(?x, ?w)"),
                        Clause.parse("=> =(a(?x', b(?x')), true)"),
                        Literal.parse("=(?w, b(b(?x)))"),
                        Literal.parse("=(a(?x', b(?x')), true)"),
                        Clause.parse("=> =(?x, ?w), =(a(b(?x), ?w), true)")
                ),
                Arguments.of(
                        Clause.parse("=(?x, ?x) => =(b(?x), ?x)"),
                        Clause.parse("=> =(a(?x', b(?x')), true)"),
                        Literal.parse("=(b(?x), ?x)"),
                        Literal.parse("=(a(?x', b(?x')), true)"),
                        Clause.parse("=(?x, ?x) => =(a(?x, ?x), true)")
                )
        );
    }

    Stream<Arguments> provideParametersForApplyLeftSuperposition() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=> =(p(c), a)"),
                        Clause.parse("=(p(?x'), p(?y')), =(m(?x'), m(?y')) => =(f(?x', ?y'), true)"),
                        Literal.parse("=(p(c), a)"),
                        Literal.parse("¬=(p(?x'), p(?y'))"),
                        Clause.parse("=(a, p(?y')), =(m(c), m(?y')) => =(f(c, ?y'), true)")
                ),
                Arguments.of(
                        Clause.parse("=> =(p(d), a)"),
                        Clause.parse("=(p(?y'), a), =(m(?y'), m(c)) => =(f(c, ?y'), true)"),
                        Literal.parse("=(p(d), a)"),
                        Literal.parse("¬=(p(?y'), a)"),
                        Clause.parse("=(m(d), m(c)), =(a, a) => =(f(c, d), true)")
                ),
                Arguments.of(
                        Clause.parse("=> =(m(d), b)"),
                        Clause.parse("=(a, a), =(m(d), m(c)) => =(f(c, d), true)"),
                        Literal.parse("=(m(d), b)"),
                        Literal.parse("¬=(m(d), m(c))"),
                        Clause.parse("=(b, m(c)), =(a, a) => =(f(c, d), true)")
                )
        );
    }

    Stream<Arguments> provideParametersForApplyEqualityResolution() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=(?x, ?x) =>"),
                        Literal.parse("¬=(?x, ?x)"),
                        Clause.parse("=>")
                ),
                Arguments.of(
                        Clause.parse("=(a, a) =>"),
                        Literal.parse("¬=(a, a)"),
                        Clause.parse("=>")
                )
        );
    }

    Stream<Arguments> provideParametersForApplyEqualityFactorization() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=> =(f(?y), ?x), =(?y, f(?x))"),
                        Literal.parse("=(f(?y), ?x)"),
                        Literal.parse("=(?y, f(?x))"),
                        Clause.parse("=(?x, ?x) => =(f(?x), ?x)")
                )
        );
    }

    Stream<Arguments> provideParametersForRefute() {
        return Stream.of(
                Arguments.of(
                        Set.of(
                                Clause.parse("Q(f(?y)) => R(c, ?y)"),
                                Clause.parse("=>")
                        ),
                        true
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("=> =(p(d), a)"),
                                Clause.parse("=> =(p(c), a)"),
                                Clause.parse("f(c, d) =>"),
                                Clause.parse("=(p(?x), a) => =(m(?x), b)"),
                                Clause.parse("=(p(?x), p(?y)), =(m(?x), m(?y)) => f(?x, ?y)"),
                                Clause.parse("f(?x, ?y) => =(m(?x), m(?y))"),
                                Clause.parse("f(?x, ?y) => =(p(?x), p(?y))")
                        ),
                        true
                ),
                Arguments.of(
                        Set.of(),
                        false // Empty clauses set
                )
        );
    }
}