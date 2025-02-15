import model.Clause;
import model.Literal;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResolverRTest {
    @ParameterizedTest(name = "{index} -> clauses={0}, givenClause={1}")
    @MethodSource("provideClauseForSelectGivenClause")
    void testSelectGivenClause(Set<Clause> clauses, Clause givenClause) {
        ResolverR resolver = new ResolverR(clauses);
        Clause result = resolver.selectGivenClause();

        assertEquals(givenClause, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideClauseForRefutationReached")
    void testRefutationReached(Set<Clause> clauses, boolean expected) {
        ResolverR resolver = new ResolverR(clauses);
        boolean result = resolver.refutationReached();

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideClauseForFactorizeClause")
    void testFactorizeClause(Clause clause, Set<Clause> expected) {
        ResolverR resolver = new ResolverR(Set.of());
        Set<Clause> result = resolver.factorizeClause(clause);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> c1={0}, c2={1}, a={2}, b={3}, expected={4}")
    @MethodSource("provideClauseForResolveClauses")
    void testResolveClauses(Clause c1, Clause c2, Literal a, Literal b, Clause expected) {
        ResolverR resolver = new ResolverR(Set.of());
        Clause result = resolver.resolveClauses(c1, c2, a, b);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideClauseForRefute")
    void testRefute(Set<Clause> clauses, boolean expected) {
        ResolverR resolver = new ResolverR(clauses);
        boolean result = resolver.refute();

        assertEquals(expected, result);
    }

    Stream<Arguments> provideClauseForSelectGivenClause() {
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

    Stream<Arguments> provideClauseForRefutationReached() {
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

    Stream<Arguments> provideClauseForFactorizeClause() {
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

    Stream<Arguments> provideClauseForResolveClauses() {
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

    Stream<Arguments> provideClauseForRefute() {
        return Stream.of(
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
                            Clause.parse("Q(f(?y)) => R(c, ?y)"),
                            Clause.parse("=>")
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
}