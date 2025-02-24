package org.mathlogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Literal;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalculusRTest {
    private CalculusR baseResolver;
    private List<AutomaticCalculus> resolvers;

    @BeforeEach
    void setUp() {
        baseResolver = new CalculusR();
        resolvers = List.of(
                new CalculusR(),
                new SortedCalculus()
        );
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForApplyRightFactorize")
    void testApplyRightFactorize(Clause clause, Literal lit1, Literal lit2, Clause expected) {
        Clause result = baseResolver.applyRightFactorize(clause, lit1, lit2);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> c1={0}, c2={1}, a={2}, b={3}, expected={4}")
    @MethodSource("provideParametersForApplyResolution")
    void testApplyResolution(Clause c1, Clause c2, Literal a, Literal b, Clause expected) {
        Clause result = baseResolver.applyResolution(c1, c2, a, b);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForRefute")
    void testRefute(Set<Clause> clauses, boolean expected) {
        for (AutomaticCalculus resolver : resolvers) {
            boolean result = resolver.refute(clauses);

            assertEquals(expected, result);
        }
    }

    Stream<Arguments> provideParametersForApplyRightFactorize() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=> R(?x, f(?y)), R(?y, f(?x))"),
                        Literal.parse("R(?y, f(?x))"),
                        Literal.parse("R(?x, f(?y))"),
                        Clause.parse("=> R(?x, f(?x))")
                ),
                Arguments.of(
                        Clause.parse("Q(f(?x, g(?y)), ?h) => P(f(?x, g(?y)), h(?z)), P(f(a, g(b)), h(c))"),
                        Literal.parse("P(f(?x, g(?y)), h(?z))"),
                        Literal.parse("P(f(a, g(b)), h(c))"),
                        Clause.parse("Q(f(a, g(b)), ?h) => P(f(a, g(b)), h(c))")
                )
        );
    }

    Stream<Arguments> provideParametersForApplyResolution() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("R(?x, s(?x)) => Q(f(?x))"),
                        Clause.parse("Q(f(?y)) =>"),
                        Literal.parse("Q(f(?x))"),
                        Literal.parse("¬Q(f(?y))"),
                        Clause.parse("R(?y, s(?y)) =>")
                ),
                Arguments.of(
                        Clause.parse("=> R(c, ?y)"),
                        Clause.parse("R(?x, s(?x)) =>"),
                        Literal.parse("R(c, ?y)"),
                        Literal.parse("¬R(?x, s(?x))"),
                        Clause.parse("=>")
                ),
                Arguments.of(
                        Clause.parse("P(f(?x)) => P(?x)"),
                        Clause.parse("P(f(?x')) => P(?x')"),
                        Literal.parse("P(?x)"),
                        Literal.parse("¬P(f(?x'))"),
                        Clause.parse("P(f(f(?x'))) => P(?x')")
                ),
                Arguments.of(
                        Clause.parse("P(f(?x)) => P(?x)"),
                        Clause.parse("P(f(?x')) => P(?x')"),
                        Literal.parse("P(?x)"),
                        Literal.parse("¬P(f(?x'))"),
                        Clause.parse("P(f(f(?x'))) => P(?x')")
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
                                Clause.parse("=> P(f(a))"),
                                Clause.parse("P(f(a)) =>")
                        ),
                        false
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("=> P(f(a))"),
                                Clause.parse("P(f(?x)) =>")
                        ),
                        true
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("P(f(?x)) =>"),
                                Clause.parse("P(f(a)), Q(f(?x)) =>"),
                                Clause.parse("=> P(f(a))")
                        ),
                        true
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("R(?x, s(?x)) => Q(f(?x))"),
                                Clause.parse("=> R(c, ?y)"),
                                Clause.parse("Q(f(?y)) =>")
                        ),
                        true
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("=> R(?x, f(?y)), R(?y, f(?x))"),
                                Clause.parse("R(?x, f(?y)), R(?y, f(?x)) =>")
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
