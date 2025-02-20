import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structure.Clause;
import structure.Literal;
import structure.Term;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalculusRTest {
    @ParameterizedTest(name = "{index} -> clauses={0}, givenClause={1}")
    @MethodSource("provideParametersForSelectGivenClause")
    void testSelectGivenClause(Set<Clause> clauses, Clause givenClause) {
        CalculusR resolver = new SupportCalculusR(clauses);
        Clause result = resolver.selectGivenClause();

        assertEquals(givenClause, result);
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForRefutationReached")
    void testRefutationReached(Set<Clause> clauses, boolean expected) {
        CalculusR resolver = new SupportCalculusR(clauses);
        boolean result = resolver.refutationReached();

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

    private static class SupportCalculusR extends CalculusR {
        public SupportCalculusR(Set<Clause> clauses) {
            super(clauses);
        }

        @Override
        protected boolean factorizationCanBeApplied(Clause clause, Literal lit, Map<String, Term> mgu) {
            return false;
        }

        @Override
        protected boolean resolutionCanBeApplied(
                Clause clauseWithPos, Clause clauseWithNeg, Literal posToDelete, Literal negToDelete, Map<String, Term> mgu) {
            return false;
        }
    }
}