package org.mathlogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalculusSTest {
    private CalculusS resolver;

    @BeforeEach
    void setUp() {
        resolver = new CalculusS();
    }

    @ParameterizedTest(name = "{index} -> clause={0}, lit1={1}, lit2={2}, expected={3}")
    @MethodSource("provideParametersForApplyRightSuperposition")
    void testApplyRightSuperposition(Clause c1, Clause c2, Literal l1, Literal l2, Clause expected) {
        Clause result = resolver.applyRightSuperposition(c1, c2, l1, l2);

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
}