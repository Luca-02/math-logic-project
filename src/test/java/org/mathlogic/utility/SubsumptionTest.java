package org.mathlogic.utility;

import org.mathlogic.structure.Clause;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubsumptionTest {
    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideParametersForSubsumption")
    void testSubsumption(Clause c1, Clause c2, boolean expected) {
        boolean result = Subsumption.isSubsumed(c1, c2);
        assertEquals(expected, result);
    }

    Stream<Arguments> provideParametersForSubsumption() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("P(?x) =>"),
                        Clause.parse("P(a) =>"),
                        true
                ),
                Arguments.of(
                        Clause.parse("=> P(?x, ?y)"),
                        Clause.parse("=> P(a, f(?z))"),
                        true
                ),
                Arguments.of(
                        Clause.parse("P(?x) =>"),
                        Clause.parse("P(a) => P(?x, ?y)"),
                        true
                ),
                Arguments.of(
                        Clause.parse("=> P(?x, ?y)"),
                        Clause.parse("Q(?z, f(?h)) => P(a, b)"),
                        true
                ),
                Arguments.of(
                        Clause.parse("Q(?z, f(?h)) => P(?x, ?y)"),
                        Clause.parse("Q(?z, f(?h)) => P(a, b), T(f(?x))"),
                        true
                ),
                Arguments.of(
                        Clause.parse("P(?x) =>"),
                        Clause.parse("P(a, f(b)) => P(?x, ?y)"),
                        false
                ),
                Arguments.of(
                        Clause.parse("=> P(?x, ?y)"),
                        Clause.parse("Q(?z, f(?h)) => P(a, b, f(?z))"),
                        false
                ),
                Arguments.of(
                        Clause.parse("S(?z, f(?h)) => P(?x, ?y)"),
                        Clause.parse("Q(?z, f(?h)) => P(a, b), T(f(?x))"),
                        false
                )
        );
    }
}