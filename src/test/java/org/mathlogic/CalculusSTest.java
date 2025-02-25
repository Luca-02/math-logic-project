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
                        Clause.parse("=> =(?x, f(?y)), =(?y, f(?x))"),
                        Literal.parse("=(?x, f(?y))"),
                        Literal.parse("=(?y, f(?x))"),
                        Clause.parse("=(?x, ?x) => =(f(?x), ?x)")
                )
        );
    }
}