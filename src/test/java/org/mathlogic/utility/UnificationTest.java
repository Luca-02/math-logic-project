package org.mathlogic.utility;

import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnificationTest {
    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideParametersForUnification")
    void testUnification(Literal lit1, Literal lit2, Map<String, Term> expected) {
        Map<String, Term> substitutions = Unification.unify(lit1, lit2);

        if (substitutions != Unification.INVALID_SUBSTITUTION) {
            assertNotNull(substitutions);
            assertEquals(expected, substitutions);

            // From theory, we know that a unification is correct if and only if
            // it outputs two equal literals when applied to the initial literals.
            assertTrue(Unification.unificationCorrectness(lit1, lit2, substitutions));
        }
    }

    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideParametersForMatching")
    void testMatching(Literal lit1, Literal lit2, Map<String, Term> expected) {
        Map<String, Term> substitutions = Unification.match(lit1, lit2);

        if (substitutions != Unification.INVALID_SUBSTITUTION) {
            assertNotNull(substitutions);
            assertEquals(expected, substitutions);

            // From theory, we know that a matching is correct if and only if
            // it outputs two equal literals when applied to the left initial literals.
            assertTrue(Unification.matchingCorrectness(lit1, lit2, substitutions));
        }
    }

    @Test
    void testFailing() {
        assertTrue(Unification.isFailing(Term.parse("f(?x)"), Term.parse("g(?x)")));
        assertTrue(Unification.isFailing(Term.parse("f(?x)"), Term.parse("f(?x, ?y)")));
        assertFalse(Unification.isFailing(Term.parse("f(?x, ?y)"), Term.parse("f(?z, ?h)")));
    }

    @Test
    void testOccurCheck() {
        assertTrue(Unification.occurCheck(Term.parse("?x"), Term.parse("f(?x, ?y)")));
        assertFalse(Unification.occurCheck(Term.parse("?x"), Term.parse("?x")));
    }

    Stream<Arguments> provideParametersForUnification() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("¬P(a)"),
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
                        Literal.parse("P(?x)"),
                        Collections.emptyMap() // Empty solution
                ),
                Arguments.of(
                        Literal.parse("P(?x, f(?x, ?y))"),
                        Literal.parse("P(?x, f(?x, ?y))"),
                        Collections.emptyMap() // Same literals
                ),
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("Q(a)"),
                        Unification.INVALID_SUBSTITUTION // Fail, different predicates
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(?x)"),
                        Unification.INVALID_SUBSTITUTION // Fail, different literal arity
                ),
                Arguments.of(
                        Literal.parse("P(f(?x, ?x))"),
                        Literal.parse("P(f(a, b))"),
                        Unification.INVALID_SUBSTITUTION // Fail, no solution
                ),
                Arguments.of(
                        Literal.parse("P(f(?x))"),
                        Literal.parse("P(g(?x))"),
                        Unification.INVALID_SUBSTITUTION // Fail, different function
                ),
                Arguments.of(
                        Literal.parse("P(f(?x))"),
                        Literal.parse("P(f(?x, ?y))"),
                        Unification.INVALID_SUBSTITUTION // Fail, different function
                ),
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("P(f(?x))"),
                        Unification.INVALID_SUBSTITUTION // Fail, occurs check
                )
        );
    }

    Stream<Arguments> provideParametersForMatching() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("P(?x)"),
                        Literal.parse("¬P(a)"),
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
                        Literal.parse("P(?x, ?y)"),
                        Collections.emptyMap() // Same literal
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("Q(a, b)"),
                        Unification.INVALID_SUBSTITUTION // Fail, different predicates
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(a, b, c)"),
                        Unification.INVALID_SUBSTITUTION // Fail, different literal arity
                ),
                Arguments.of(
                        Literal.parse("P(?x, f(?y))"),
                        Literal.parse("P(a, g(b))"),
                        Unification.INVALID_SUBSTITUTION // Fail, different function
                ),
                Arguments.of(
                        Literal.parse("P(g(?y))"),
                        Literal.parse("P(a)"),
                        Unification.INVALID_SUBSTITUTION // Fail, no solution
                )
        );
    }
}