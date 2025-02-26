package org.mathlogic.structure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.exception.ParsingEmptyLogicalStructureException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiteralTest {
    @ParameterizedTest(name = "{index} -> literal={0}, expected={1}")
    @MethodSource("provideParametersForCollectSymbols")
    void testCollectSymbols(Literal literal, int expected) {
        int result = literal.collectSymbols().size();

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> literal={0}, expected={1}")
    @MethodSource("provideParametersForFormatLiteralWithIdentity")
    void testFormatLiteralWithIdentity(Literal literal, Literal expected) {
        Literal result = literal.formatWrtIdentity();

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> literal={0}, expected={1}")
    @MethodSource("provideParametersForGetMultisetView")
    void testGetMultisetView(Literal literal, Map<Term, Integer> expected) {
        Map<Term, Integer> result = literal.getMultisetView();

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> lit={0}, expected={1}, substitution={2}")
    @MethodSource("provideParametersForApplySubstitutionToLiteral")
    void testApplySubstitutionToLiteral(Literal lit, Literal expected, Map<String, Term> substitution) {
        Literal result = lit.applySubstitution(substitution);

        assertEquals(expected, result);
    }

    @Test
    void testInvalidLiteral() {
        assertThrows(ParsingEmptyLogicalStructureException.class, () -> Literal.parse(""));
    }

    @Test
    void testIdentity() {
        Literal identity = Literal.parse("=(f(?x, ?y), f(g(?x), ?y))");
        Literal literal = Literal.parse("=(f(?x, ?y))");

        assertTrue(identity.isIdentity());
        assertFalse(literal.isIdentity());
    }

    @Test
    void testNegation() {
        String litStr = "Q(f(?x, ?y), f(g(?x), ?y))";
        Literal literal = Literal.parse(litStr);
        Literal negated = literal.negate();

        assertTrue(negated.isNegated());
    }

    @Test
    void testEqualsLiteral() {
        Literal lit1 = Literal.parse("Q(f(?x, ?y), f(g(?x), ?y))");
        Literal lit2 = Literal.parse("Q(f(?x, ?y), f(g(?x), ?y))");
        Literal litNeg = Literal.parse("¬Q(f(?x, ?y), f(g(?x), ?y))");

        assertEquals(lit1, lit2);
        assertNotEquals(lit1, litNeg);
    }

    @Test
    void testToString() {
        Literal literal = Literal.parse("Q(f(?x, ?y), f(g(?x), ?y))");
        Literal negatedLiteral = Literal.parse("¬Q(f(?x, ?y), f(g(?x), ?y))");

        assertEquals("Q(f(?x, ?y), f(g(?x), ?y))", literal.toString());
        assertEquals("¬Q(f(?x, ?y), f(g(?x), ?y))", negatedLiteral.toString());
    }

    @Test
    void testCopy() {
        Literal literal = Literal.parse("Q(f(?x, ?y), f(g(?x), ?y))");
        Literal copy = literal.copy();

        assertEquals(literal, copy);
        assertNotSame(literal, copy);
        assertNotSame(literal.getTerms(), copy.getTerms());
        for (int i = 0; i < literal.getTerms().size(); i++) {
            assertNotSame(literal.getTerms().get(i), copy.getTerms().get(i));
        }
    }

    Stream<Arguments> provideParametersForCollectSymbols() {
        return Stream.of(
                Arguments.of(Literal.parse("Q(?x)"), 2),
                Arguments.of(Literal.parse("Q(f(?x))"), 3),
                Arguments.of(Literal.parse("Q(f(?x), ?x)"), 4),
                Arguments.of(Literal.parse("Q(f(?x), ?y)"), 4),
                Arguments.of(Literal.parse("Q(f(?x, ?y), f(g(?x), ?y))"), 8)
        );
    }

    Stream<Arguments> provideParametersForFormatLiteralWithIdentity() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("Q(f(g(?x), ?y))"),
                        Literal.parse("=(Q(f(g(?x), ?y)), true)")
                ),
                Arguments.of(
                        Literal.parse("=(f(?x, ?y), f(g(?x), ?y))"),
                        Literal.parse("=(f(?x, ?y), f(g(?x), ?y))")
                )
        );
    }

    Stream<Arguments> provideParametersForGetMultisetView() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("Q(f(g(?x), ?y))"),
                        Map.of(
                                Term.parse("Q(f(g(?x), ?y))"), 1,
                                Term.MINIMAL, 1
                        )
                ),
                Arguments.of(
                        Literal.parse("=(f(?x, ?y), f(g(?x), ?y))"),
                        Map.of(
                                Term.parse("f(?x, ?y)"), 1,
                                Term.parse("f(g(?x), ?y)"), 1
                        )
                ),
                Arguments.of(
                        Literal.parse("¬Q(f(g(?x), ?y))"),
                        Map.of(
                                Term.parse("Q(f(g(?x), ?y))"), 2,
                                Term.MINIMAL, 2
                        )
                ),
                Arguments.of(
                        Literal.parse("¬=(f(?x, ?y), f(g(?x), ?y))"),
                        Map.of(
                                Term.parse("f(?x, ?y)"), 2,
                                Term.parse("f(g(?x), ?y)"), 2
                        )
                ),
                Arguments.of(
                        Literal.parse("=(f(?x, ?y), f(?x, ?y))"),
                        Map.of(
                                Term.parse("f(?x, ?y)"), 2
                        )
                )
        );
    }

    Stream<Arguments> provideParametersForApplySubstitutionToLiteral() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("P(g(?y), f(?x, h(?x), ?y))"),
                        Literal.parse("P(g(b), f(g(a), h(g(a)), b))"),
                        Map.of("?x", Term.parse("g(a)"), "?y", Term.parse("b"))
                )
        );
    }
}
