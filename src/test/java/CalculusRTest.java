import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalculusRTest {
    private CalculusR baseResolver;
    private List<CalculusR> resolvers;

    @BeforeEach
    void setUp() {
        baseResolver = new SupportCalculusR();
        resolvers = List.of(
                new DefaultCalculusR(),
                new SortedCalculusR()
        );
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, givenClause={1}")
    @MethodSource("provideParametersForSelectGivenClause")
    void testSelectGivenClause(Set<Clause> clauses, Clause givenClause) {
        baseResolver.initClauses(clauses);
        Clause result = baseResolver.selectGivenClause();

        assertEquals(givenClause, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForRefutationReached")
    void testRefutationReached(Set<Clause> clauses, boolean expected) {
        baseResolver.initClauses(clauses);
        boolean result = baseResolver.refutationReached();

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForFactorizeAllPossibleClause")
    void testFactorizeAllPossibleClause(Clause clause, Set<Clause> expected) {
        Set<Clause> result = baseResolver.factorizeAllPossibleClause(clause);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> c1={0}, c2={1}, a={2}, b={3}, expected={4}")
    @MethodSource("provideParametersForResolveClauses")
    void testResolveClauses(Clause c1, Clause c2, Literal a, Literal b, Clause expected) {
        Clause result = baseResolver.resolveClauses(c1, c2, a, b);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForRefute")
    void testRefute(Set<Clause> clauses, boolean expected) {
        for (CalculusR resolver : resolvers) {
            resolver.initClauses(clauses);
            boolean result = resolver.refute();

            assertEquals(expected, result);
        }
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
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) => S(f(?x, a))"),
                                Clause.parse("S(f(?x, a)) => T(f(?x, a), f(g(?x), ?y))"),
                                Clause.parse("Q(f(?x, a), f(g(?x), ?y)) => S(f(?x, a)), T(f(?x, a), f(g(?x), ?y))")
                        ),
                        Clause.parse("S(f(?x, a)) => T(f(?x, a), f(g(?x), ?y))")
                )
        );
    }

    Stream<Arguments> provideParametersForRefutationReached() {
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

    Stream<Arguments> provideParametersForFactorizeAllPossibleClause() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("=> R(?x, f(?y)), R(?y, f(?x))"),
                        Set.of(Clause.parse("=> R(?x, f(?x))"))
                ),
                Arguments.of(
                        Clause.parse("Q(f(?x, g(?y)), ?h) => P(f(?x, g(?y)), h(?z)), P(f(a, g(b)), h(c))"),
                        Set.of(Clause.parse("Q(f(a, g(b)), ?h) => P(f(a, g(b)), h(c))"))
                )
        );
    }

    Stream<Arguments> provideParametersForResolveClauses() {
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
                )
        );
    }

    private static class SupportCalculusR extends CalculusR {
        @Override
        protected boolean factorizationCanBeApplied(Clause clause, Literal lit, Map<String, Term> mgu) {
            return true;
        }

        @Override
        protected boolean resolutionCanBeApplied(
                Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete, Map<String, Term> mgu) {
            return true;
        }
    }
}