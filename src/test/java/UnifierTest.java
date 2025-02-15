import model.Literal;
import model.Term;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UnifierTest {
    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideLiteralsForUnification")
    void testUnification(Literal lit1, Literal lit2, Map<String, Term> expected) {
        Map<String, Term> substitutions = Unifier.unify(lit1, lit2);

        if (expected == null) {
            assertNull(substitutions);
        } else {
            assertNotNull(substitutions);
            assertEquals(expected, substitutions);

            // From theory, we know that a unification is correct if and only if
            // it outputs two equal literals when applied to the initial literals.
            assertTrue(Unifier.unificationCorrectness(lit1, lit2, substitutions));
        }
    }

    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideLiteralsForMatching")
    void testMatching(Literal lit1, Literal lit2, Map<String, Term> expected) {
        Map<String, Term> substitutions = Unifier.match(lit1, lit2);

        if (expected == null) {
            assertNull(substitutions);
        } else {
            assertNotNull(substitutions);
            assertEquals(expected, substitutions);

            // From theory, we know that a matching is correct if and only if
            // it outputs two equal literals when applied to the left initial literals.
            assertTrue(Unifier.matchingCorrectness(lit1, lit2, substitutions));
        }
    }

    @Test
    void testFailing() {
        assertTrue(Unifier.isFailing(Term.parse("f(?x)"), Term.parse("g(?x)")));
        assertTrue(Unifier.isFailing(Term.parse("f(?x)"), Term.parse("f(?x, ?y)")));
        assertFalse(Unifier.isFailing(Term.parse("f(?x, ?y)"), Term.parse("f(?z, ?h)")));
    }

    @Test
    void testOccurCheck() {
        assertTrue(Unifier.occurCheck(Term.parse("?x"), Term.parse("f(?x, ?y)")));
        assertFalse(Unifier.occurCheck(Term.parse("?x"), Term.parse("?x")));
    }

    Stream<Arguments> provideLiteralsForUnification() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("P(a)"),
                        Map.of("?x", Term.parse("a"))
                ),
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("P(f(a))"),
                        Map.of("?x", Term.parse("f(a)"))
                ),
                Arguments.of(
                        Literal.parse("P(f(?x))"),
                        Literal.parse("P(f(a))"),
                        Map.of("?x", Term.parse("a"))
                ),
                Arguments.of(
                        Literal.parse("P(f(?x, g(?y)))"),
                        Literal.parse("P(f(a, g(b)))"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b")
                        )
                ),
                Arguments.of(
                        Literal.parse("P(f(g(?x, ?y), h(?z)))"),
                        Literal.parse("P(f(g(a, b), h(c)))"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b"),
                                "?z", Term.parse("c")
                        )
                ),
                Arguments.of(
                        Literal.parse("P(f(g(?x, ?y), h(?z)))"),
                        Literal.parse("P(f(g(a, b), h(c)))"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b"),
                                "?z", Term.parse("c")
                        )
                ),
                Arguments.of(
                        Literal.parse("P(g(?y), f(?x, h(?x), ?y))"),
                        Literal.parse("P(?x, f(g(?z), ?w, ?z))"),
                        Map.of(
                                "?x", Term.parse("g(?z)"),
                                "?y", Term.parse("?z"),
                                "?w", Term.parse("h(g(?z))")
                        )
                ),
                Arguments.of(
                        Literal.parse("P(f(?x, g(?y)), h(?z))"),
                        Literal.parse("P(f(a, g(b)), h(c))"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b"),
                                "?z", Term.parse("c")
                        )
                ),
                Arguments.of(
                        Literal.parse("P(?x, f(?y))"),
                        Literal.parse("P(?y, f(?x))"),
                        Map.of("?x", Term.parse("?y"))
                ),
                Arguments.of(
                        Literal.parse("P(?x, f(?y))"),
                        Literal.parse("P(?y, f(?x))"),
                        Map.of("?x", Term.parse("?y"))
                ),
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("Q(a)"),
                        null // Fail, different predicates
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(?x)"),
                        null // Fail, different literal arity
                ),
                Arguments.of(
                        Literal.parse("P(f(?x, ?x))"),
                        Literal.parse("P(f(a, b))"),
                        null // Fail, no solution
                ),
                Arguments.of(
                        Literal.parse("P(f(?x))"),
                        Literal.parse("P(g(?x))"),
                        null // Fail, different function
                ),
                Arguments.of(
                        Literal.parse("P(f(?x))"),
                        Literal.parse("P(f(?x, ?y))"),
                        null // Fail, different function
                ),
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("P(?x)"),
                        null // Fail, empty solution
                ),
                Arguments.of(
                        Literal.parse("P(?x, f(?x, ?y))"),
                        Literal.parse("P(?x, f(?x, ?y))"),
                        null // Fail, same literals
                ),
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("P(f(?x))"),
                        null // Fail, occurs check
                )
        );
    }

    Stream<Arguments> provideLiteralsForMatching() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("P(a)"),
                        Map.of("?x", Term.parse("a"))
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(a, b)"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b")
                        )
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(a, f(b))"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("f(b)")
                        )
                ),
                Arguments.of(
                        Literal.parse("T(?x, ?y, ?z)"),
                        Literal.parse("T(a, a, c)"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("a"),
                                "?z", Term.parse("c")
                        )
                ),
                Arguments.of(
                        Literal.parse("S(?x, g(?y, ?z))"),
                        Literal.parse("S(a, g(b, c))"),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b"),
                                "?z", Term.parse("c")
                        )
                ),
                Arguments.of(
                        Literal.parse("S(?x, g(?y, ?z))"),
                        Literal.parse("S(?x, g(?b, ?c))"),
                        Map.of(
                                "?y", Term.parse("?b"),
                                "?z", Term.parse("?c")
                        )
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("Q(a, b)"),
                        null // Fail, different predicates
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(a, b, c)"),
                        null // Fail, different literal arity
                ),
                Arguments.of(
                        Literal.parse("P(?x, f(?y))"),
                        Literal.parse("P(a, g(b))"),
                        null // Fail, different function
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(?x, ?y)"),
                        null // Fail, equals literal
                ),
                Arguments.of(
                        Literal.parse("P(g(?y))"),
                        Literal.parse("P(a)"),
                        null // Fail, no solution
                )
        );
    }
}