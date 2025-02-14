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
    String predicate1 = "Q";
    String predicate2 = "S";
    String predicate3 = "T";
    String termStr1 = "f(?x, a)";
    String termStr2 = "f(g(?x), ?y)";
    String litStr1 = "%s(%s, %s)".formatted(predicate1, termStr1, termStr2);
    String litStr2 = "%s(%s)".formatted(predicate2, termStr1);
    String litStr3 = "¬%s(%s, %s)".formatted(predicate3, termStr1, termStr2);
    Literal lit1 = Literal.parse(litStr1);
    Literal lit2 = Literal.parse(litStr2);
    Literal lit3 = Literal.parse(litStr3);
    Clause clause1 = new Clause(lit1);
    Clause clause2 = new Clause(lit1, lit2);
    Clause clause3 = new Clause(lit1, lit3);
    Clause clause4 = new Clause(lit1, lit2, lit3);
    Clause clause5 = new Clause();

    @ParameterizedTest(name = "{index} -> clauses={0}, givenClause={1}")
    @MethodSource("provideClauseForSelectGivenClause")
    void testSelectGivenClause(Set<Clause> clauses, Clause givenClause) {
        ResolverR resolver = new ResolverR(clauses);
        assertEquals(givenClause, resolver.selectGivenClause());
    }

    Stream<Arguments> provideClauseForSelectGivenClause() {
        return Stream.of(
                Arguments.of(Set.of(clause1, clause2, clause3), clause1),
                Arguments.of(Set.of(clause2, clause3, clause4), clause2)
        );
    }

    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideClauseForRefutationReached")
    void testRefutationReached(Set<Clause> clauses, boolean expected) {
        ResolverR resolver = new ResolverR(clauses);
        assertEquals(expected, resolver.refutationReached());
    }

    Stream<Arguments> provideClauseForRefutationReached() {
        return Stream.of(
                Arguments.of(Set.of(clause1, clause2, clause3), false),
                Arguments.of(Set.of(clause2, clause5, clause4), true)
        );
    }
}