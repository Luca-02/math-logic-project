package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiteralTest {
    String predicate = "Q";
    String termStr1 = "f(?x, ?y)";
    String termStr2 = "f(g(?x), ?y)";
    String litStr1 = "%s(%s, %s)".formatted(predicate, termStr1, termStr2);
    String litStr2 = "%s(%s, %s)".formatted(predicate, termStr1, termStr2);
    String litStr3 = "¬%s(%s, %s)".formatted(predicate, termStr1, termStr2);

    @Test
    void testNegation() {
        Literal literal = Literal.parse(litStr1);
        Literal negated = literal.negate();

        assertTrue(negated.isNegated());
        assertEquals(predicate, negated.getPredicate());
        assertEquals(List.of(Term.parse(termStr1), Term.parse(termStr2)), negated.getTerms());
    }

    @Test
    void testEqualsLiteral() {
        Literal lit1 = Literal.parse(litStr1);
        Literal lit2 = Literal.parse(litStr2);
        Literal litNeg = Literal.parse(litStr3);

        assertEquals(lit1, lit2);
        assertNotEquals(lit1, litNeg);
    }

    @Test
    void testToString() {
        Literal literal = Literal.parse(litStr1);
        Literal negatedLiteral = Literal.parse(litStr2);

        assertEquals(litStr1, literal.toString());
        assertEquals(litStr2, negatedLiteral.toString());
    }

    @Test
    void testCollectSymbols() {
        Literal literal = Literal.parse(litStr1);

        assertEquals(5, literal.collectSymbols().size());
    }
}
