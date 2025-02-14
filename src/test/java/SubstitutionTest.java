import model.Literal;
import model.Term;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubstitutionTest {
    @ParameterizedTest(name = "{index} -> term={0}, expected={1}, substitution={2}")
    @MethodSource("provideTermsForSubstitution")
    void testApplySubstitutionToTerm(Term input, Term expected, Map<String, Term> substitution) {
        Term result = Substitution.applySubstitution(input, substitution);
        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> literal={0}, expected={1}, substitution={2}")
    @MethodSource("provideLiteralsForSubstitution")
    void testApplySubstitutionToLiteral(Literal input, Literal expected, Map<String, Term> substitution) {
        Literal result = Substitution.applySubstitution(input, substitution);
        assertEquals(expected, result);
    }

    static Stream<Arguments> provideTermsForSubstitution() {
        return Stream.of(
                Arguments.of(
                        Term.parse("?x"),
                        Term.parse("a"),
                        Map.of("?x", Term.parse("a"))
                ),
                Arguments.of(
                        Term.parse("?z"),
                        Term.parse("?z"),
                        Map.of("?x", Term.parse("a"))),
                Arguments.of(
                        Term.parse("f(?x, ?y)"),
                        Term.parse("f(a, b)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, ?y, ?z)"),
                        Term.parse("f(a, b, ?z)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(g(?x), h(?y, ?z))"),
                        Term.parse("f(g(a), h(b, ?z))"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, ?x, ?y)"),
                        Term.parse("f(a, a, b)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, ?y)"),
                        Term.parse("f(g(a), h(b))"),
                        Map.of("?x", Term.parse("g(a)"), "?y", Term.parse("h(b)"))
                ),
                Arguments.of(
                        Term.parse("f(a, b)"),
                        Term.parse("f(a, b)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, b)"),
                        Term.parse("f(?x, b)"),
                        Map.of()
                ),
                Arguments.of(
                        Term.parse("f(?x, ?y)"),
                        Term.parse("f(?x, ?y)"),
                        Map.of("?z", Term.parse("c"))
                ),
                Arguments.of(
                        Term.parse("?x"),
                        Term.parse("!x"),
                        Map.of("?x", Term.parse("!x"))
                )
        );
    }

    static Stream<Arguments> provideLiteralsForSubstitution() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("P(g(?y), f(?x, h(?x), ?y))"),
                        Literal.parse("P(g(b), f(g(a), h(g(a)), b))"),
                        Map.of("?x", Term.parse("g(a)"), "?y", Term.parse("b"))
                )
        );
    }
}