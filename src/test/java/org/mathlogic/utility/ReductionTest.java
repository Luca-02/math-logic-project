package org.mathlogic.utility;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Clause;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReductionTest {
    @ParameterizedTest(name = "{index} -> original={0}, expected={1}")
    @MethodSource("provideParametersForRemoveTautology")
    void testRemoveTautology(Set<Clause> original, Set<Clause> expected) {
        Reduction.removeTautology(original);

        assertEquals(expected, original);
    }

    @ParameterizedTest(name = "{index} -> original={0}, expected={1}")
    @MethodSource("provideParametersForSubsumptionReduction")
    void testSubsumptionReduction(Set<Clause> original, Set<Clause> expected) {
        Reduction.subsumptionReduction(original);

        assertEquals(expected, original);
    }

    Stream<Arguments> provideParametersForRemoveTautology() {
        return Stream.of(
                Arguments.of(
                        new HashSet<>(Set.of(
                                Clause.parse("P(f(a)) => P(f(a))")
                        )),
                        Set.of()
                ),
                Arguments.of(
                        new HashSet<>(Set.of(
                                Clause.parse("R(?x, s(?x)) => Q(f(?x)), R(?x, s(?x))"),
                                Clause.parse("R(c, ?y) => R(c, ?y)"),
                                Clause.parse("Q(f(?y)) =>")
                        )),
                        Set.of(
                                Clause.parse("Q(f(?y)) =>")
                        )
                )
        );
    }

    Stream<Arguments> provideParametersForSubsumptionReduction() {
        return Stream.of(
                Arguments.of(
                        new HashSet<>(Set.of(
                                Clause.parse("=> P(?x, ?y)"),
                                Clause.parse("Q(?z, f(?h)) => P(a, b)")
                        )),
                        Set.of(
                                Clause.parse("=> P(?x, ?y)")
                        )
                ),
                Arguments.of(
                        new HashSet<>(Set.of(
                                Clause.parse("Q(?z, f(?h)) => P(?x, ?y)"),
                                Clause.parse("Q(?z, f(?h)) => P(a, b), T(f(?x))")
                        )),
                        Set.of(
                                Clause.parse("Q(?z, f(?h)) => P(?x, ?y)")
                        )
                ),
                Arguments.of(
                        new HashSet<>(Set.of(
                                Clause.parse("S(?z, f(?h)) => P(?x, ?y)"),
                                Clause.parse("Q(?z, f(?h)) => P(a, b), T(f(?x))")
                        )),
                        Set.of(
                                Clause.parse("S(?z, f(?h)) => P(?x, ?y)"),
                                Clause.parse("Q(?z, f(?h)) => P(a, b), T(f(?x))")
                        )
                )
        );
    }
}