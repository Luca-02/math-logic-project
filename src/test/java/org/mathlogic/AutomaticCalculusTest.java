package org.mathlogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Clause;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AutomaticCalculusTest {
    private AutomaticCalculus baseResolver;

    @BeforeEach
    void setUp() {
        baseResolver = new SupportAutomaticCalculus();
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, givenClause={1}")
    @MethodSource("provideParametersForSelectGivenClause")
    void testSelectGivenClause(Set<Clause> clauses, Clause givenClause) {
        Clause result = baseResolver.selectGivenClause(clauses);

        assertEquals(givenClause, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForContainsEmptyClause")
    void testContainsEmptyClause(Set<Clause> clauses, boolean expected) {
        boolean result = baseResolver.containsEmptyClause(clauses);

        assertEquals(expected, result);
    }

    Stream<Arguments> provideParametersForSelectGivenClause() {
        return Stream.of(
                Arguments.of(
                        Set.of(
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) => S(f(?x, a))"),
                                Clause.parse("=>"),
                                Clause.parse("S(f(?x, a)) => T(f(?x, a), f(g(?x), ?y))")
                        ),
                        Clause.parse("=>")
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) =>"),
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) => S(f(?x, a))"),
                                Clause.parse("S(f(?x, a)) => T(f(?x, a), f(g(?x), ?y))")
                        ),
                        Clause.parse("Q(f(?x, a), f(g(?x), ?y)) =>")
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("S(f(?x, a)) => T(f(?x, a), f(g(?x), ?y)), Q(f(?x, a))"),
                                Clause.parse("R(f(?x, a), f(g(?x), ?y)) => S(f(?x, a))"),
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) => S(f(?x, a)), T(f(?x, a), f(g(?x), ?y))")
                        ),
                        Clause.parse("R(f(?x, a), f(g(?x), ?y)) => S(f(?x, a))")
                )
        );
    }

    Stream<Arguments> provideParametersForContainsEmptyClause() {
        return Stream.of(
                Arguments.of(
                        Set.of(
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) =>"),
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) => S(f(?x, a))"),
                                Clause.parse("S(f(?x, a)) => T(f(?x, a), f(g(?x), ?y))")
                        ),
                        false
                ),
                Arguments.of(
                        Set.of(
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) =>"),
                                Clause.parse("=>"),
                                Clause.parse("S(f(?x, a)) => T(f(?x, a), f(g(?x), ?y))")
                        ),
                        true
                )
        );
    }

    private static class SupportAutomaticCalculus extends AutomaticCalculus {
        @Override
        protected void initialReduction() {

        }

        @Override
        protected Set<Clause> inferAllPossibleClausesFromItself(Clause given) {
            return Set.of();
        }

        @Override
        protected Set<Clause> inferAllPossibleClausesFromWorkedClause(Clause given, Clause clauseWo) {
            return Set.of();
        }

        @Override
        protected void forwardReduction(Set<Clause> newClauses) {

        }

        @Override
        protected void backwardsReduction(Set<Clause> newClauses) {

        }
    }
}