import model.Literal;
import model.Term;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.stream.Stream;

public class UnifierTest {
    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideLiteralsForUnification")
    void testUnify(Literal lit1, Literal lit2, Map<String, Term> expected) {
        Map<String, Term> substitution = Unifier.unify(lit1, lit2);

        if (expected == null) {
            assertNull(substitution);
        } else {
            assertNotNull(substitution);
            assertEquals(expected, substitution);

            // From theory, we know that a unification is correct if and only if
            // it outputs two equal literals when applied to the initial literals.
            Literal subLit1 = Substitution.applySubstitution(lit1, substitution);
            Literal subLit2 = Substitution.applySubstitution(lit2, substitution);
            assertEquals(subLit1, subLit2, "Substituted literals do not match");
        }
    }

    static Stream<Arguments> provideLiteralsForUnification() {
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
                        Literal.parse("P(?x)"),
                        Literal.parse("Q(a)"),
                        null // Fail, different predicates
                ),
                Arguments.of(
                        Literal.parse("P(?x, ?y)"),
                        Literal.parse("P(?x)"),
                        null // Fail, different predicates arity
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
                )
        );
    }
}