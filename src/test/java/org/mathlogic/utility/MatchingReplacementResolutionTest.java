package org.mathlogic.utility;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Clause;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchingReplacementResolutionTest {
    @ParameterizedTest(name = "{index} -> clause1={0}, clause2={1}, expected={2}")
    @MethodSource("provideParametersForMatchingReplacementResolution")
    void testMatchingReplacementResolution(Clause clause1, Clause clause2, Clause expected) {
        Clause result = MatchingReplacementResolution.apply(clause1, clause2);

        assertEquals(expected, result);
    }

    Stream<Arguments> provideParametersForMatchingReplacementResolution() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=> P(?x)"),
                        Clause.parse("P(b) => R(c)"),
                        Clause.parse("=> R(c)")
                ),
                Arguments.of(
                    Clause.parse("=> P(f(?x))"),
                    Clause.parse("P(f(a)) => Q(c)"),
                    Clause.parse("=> Q(c)")
                ),
                Arguments.of(
                        Clause.parse("S(?x) => P(f(?x)), Q(c)"),
                        Clause.parse("S(a), P(f(a)) => Q(c)"),
                        Clause.parse("S(a) => Q(c)")
                ),
                Arguments.of(
                        Clause.parse("=> Q(g(?x))"),
                        Clause.parse("R(h(?y)) =>"),
                        null
                )
        );
    }
}