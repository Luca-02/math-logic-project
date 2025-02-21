package org.mathlogic.structure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiteralTest {
    @ParameterizedTest(name = "{index} -> literal={0}, expected={1}")
    @MethodSource("provideParametersForCollectSymbols")
    void testCollectSymbols(Literal literal, int expected) {
        assertEquals(expected, literal.collectSymbols().size());
    }

    @ParameterizedTest(name = "{index} -> literal={0}, expected={1}")
    @MethodSource("provideParametersForGetMultiset")
    void testGetMultiset(Literal literal, Map<Term, Integer> expected) {
        assertEquals(expected, literal.getMultiset());
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
        Literal clone = literal.copy();

        assertEquals(literal, clone);
        assertNotSame(literal, clone);
        assertNotSame(literal.getTerms(), clone.getTerms());
        for (int i = 0; i < literal.getTerms().size(); i++) {
            assertNotSame(literal.getTerms().get(i), clone.getTerms().get(i));
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

    Stream<Arguments> provideParametersForGetMultiset() {
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
                )
        );
    }
}
