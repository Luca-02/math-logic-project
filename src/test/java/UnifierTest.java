import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
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
                        new Literal(false, "P", List.of(Term.parse("?x"))),
                        new Literal(false, "P", List.of(Term.parse("a"))),
                        Map.of("?x", Term.parse("a"))
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("?x"))),
                        new Literal(false, "P", List.of(Term.parse("f(a)"))),
                        Map.of("?x", Term.parse("f(a)"))
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                        new Literal(false, "P", List.of(Term.parse("f(a)"))),
                        Map.of("?x", Term.parse("a"))
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("?x"))),
                        new Literal(false, "Q", List.of(Term.parse("a"))),
                        null // Fail, different predicates
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("?x"), Term.parse("?y"))),
                        new Literal(false, "P", List.of(Term.parse("?x"))),
                        null // Fail, different predicates arity
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(?x, ?x)"))),
                        new Literal(false, "P", List.of(Term.parse("f(a, b)"))),
                        null // Fail, no solution
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                        new Literal(false, "P", List.of(Term.parse("g(?x)"))),
                        null // Fail, different function
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                        new Literal(false, "P", List.of(Term.parse("f(?x, ?y)"))),
                        null // Fail, different function
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("?x"))),
                        new Literal(false, "P", List.of(Term.parse("?x"))),
                        null // Fail, empty solution
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("x"), Term.parse("f(x, y)"))),
                        new Literal(false, "P", List.of(Term.parse("x"), Term.parse("f(x, y)"))),
                        null // Fail, same literals
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("?x"))),
                        new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                        null // Fail, occurs check
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(?x, g(?y))"))),
                        new Literal(false, "P", List.of(Term.parse("f(a, g(b))"))),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b")
                        )
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(g(?x, ?y), h(?z))"))),
                        new Literal(false, "P", List.of(Term.parse("f(g(a, b), h(c))"))),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b"),
                                "?z", Term.parse("c")
                        )
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(g(?x, ?y), h(?z))"))),
                        new Literal(false, "P", List.of(Term.parse("f(g(a, b), h(c))"))),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b"),
                                "?z", Term.parse("c")
                        )
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("g(?y)"), Term.parse("f(?x, h(?x), ?y)"))),
                        new Literal(false, "P", List.of(Term.parse("?x"), Term.parse("f(g(?z), ?w, ?z)"))),
                        Map.of(
                                "?x", Term.parse("g(?z)"),
                                "?y", Term.parse("?z"),
                                "?w", Term.parse("h(g(?z))")
                        )
                ),
                Arguments.of(
                        new Literal(false, "P", List.of(Term.parse("f(?x, g(?y))"), Term.parse("h(?z)"))),
                        new Literal(false, "P", List.of(Term.parse("f(a, g(b))"), Term.parse("h(c)"))),
                        Map.of(
                                "?x", Term.parse("a"),
                                "?y", Term.parse("b"),
                                "?z", Term.parse("c")
                        )
                )
        );
    }
}