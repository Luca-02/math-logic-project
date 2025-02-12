import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiteralTest {
    @Test
    void testNegation() {
        String predicate = "Q";
        Term term = Term.parse("f(g(x), y)");
        Literal literal = new Literal(false, predicate, List.of(term));
        Literal negated = literal.negate();

        assertTrue(negated.isNegated());
        assertEquals(predicate, negated.getPredicate());
        assertEquals(List.of(term), negated.getTerms());
    }

    @Test
    void testEqualsLiteral() {
        String predicate = "Q";
        String termStr1 = "f(x, y)";
        String termStr2 = "f(g(x), y)";
        Term term1 = Term.parse(termStr1);
        Term term2 = Term.parse(termStr2);
        Literal lit1 = new Literal(false, predicate, List.of(term1, term2));
        Literal lit2 = new Literal(false, predicate, List.of(term1, term2));
        Literal litNeg = new Literal(true, predicate, List.of(term1, term2));

        assertEquals(lit1, lit2);
        assertNotEquals(lit1, litNeg);
    }

    @Test
    void testToString() {
        String predicate = "Q";
        String termStr1 = "f(x, y)";
        String termStr2 = "f(g(x), y)";
        Term term1 = Term.parse(termStr1);
        Term term2 = Term.parse(termStr2);
        Literal literal = new Literal(false, predicate, List.of(term1, term2));
        Literal negatedLiteral = new Literal(true, predicate, List.of(term1, term2));
        String expected = predicate + "(" + termStr1 + ", " + termStr2 + ")";
        String expectedNegated = "¬" + predicate + "(" + termStr1 + ", " + termStr2 + ")";

        assertEquals(expected, literal.toString());
        assertEquals(expectedNegated, negatedLiteral.toString());
    }
}
