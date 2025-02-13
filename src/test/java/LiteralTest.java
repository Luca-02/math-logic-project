import model.Literal;
import model.Term;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiteralTest {
    @Test
    void testNegation() {
        String predicate = "Q";
        String termStr = "f(g(?x), ?y)";
        String litStr = "%s(%s)".formatted(predicate, termStr);

        Literal literal = Literal.parse(litStr);
        Literal negated = literal.negate();

        assertTrue(negated.isNegated());
        assertEquals(predicate, negated.getPredicate());
        assertEquals(List.of(Term.parse(termStr)), negated.getTerms());
    }

    @Test
    void testEqualsLiteral() {
        String predicate = "Q";
        String termStr1 = "f(?x, ?y)";
        String termStr2 = "f(g(?x), ?y)";
        String litStr1 = "%s(%s, %s)".formatted(predicate, termStr1, termStr2);
        String litStr2 = "%s(%s, %s)".formatted(predicate, termStr1, termStr2);
        String litStr3 = "¬%s(%s, %s)".formatted(predicate, termStr1, termStr2);

        Literal lit1 = Literal.parse(litStr1);
        Literal lit2 = Literal.parse(litStr2);
        Literal litNeg = Literal.parse(litStr3);

        assertEquals(lit1, lit2);
        assertNotEquals(lit1, litNeg);
    }

    @Test
    void testToString() {
        String predicate = "Q";
        String termStr1 = "f(?x, ?y)";
        String termStr2 = "f(g(?x), ?y)";
        String litStr1 = "%s(%s, %s)".formatted(predicate, termStr1, termStr2);
        String litStr2 = "¬%s(%s, %s)".formatted(predicate, termStr1, termStr2);

        Literal literal = Literal.parse(litStr1);
        Literal negatedLiteral = Literal.parse(litStr2);

        assertEquals(litStr1, literal.toString());
        assertEquals(litStr2, negatedLiteral.toString());
    }
}
