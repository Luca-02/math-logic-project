import model.Clause;
import model.Literal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClauseTest {
    @Test
    void testEmptyClause() {
        Clause emptyClause = new Clause();
        assertTrue(emptyClause.isEmpty());
    }

    @Test
    void testClauseEquality() {
        String predicate1 = "Q";
        String predicate2 = "S";
        String termStr1 = "f(?x, a)";
        String termStr2 = "f(g(?x), ?y)";
        String litStr1 = "%s(%s, %s)".formatted(predicate1, termStr1, termStr2);
        String litStr2 = "%s(%s, %s)".formatted(predicate1, termStr1, termStr2);
        String litStr3 = "¬%s(%s, %s)".formatted(predicate2, termStr1, termStr2);

        Literal lit1 = Literal.parse(litStr1);
        Literal lit2 = Literal.parse(litStr2);
        Literal lit3 = Literal.parse(litStr3);

        Clause clause1 = new Clause(lit1, lit3);
        Clause clause2 = new Clause(lit2, lit3);

        assertEquals(clause1, clause2);
    }

    @Test
    void testClauseInequality() {
        String predicate1 = "Q";
        String predicate2 = "S";
        String predicate3 = "T";
        String termStr1 = "f(?x, a)";
        String termStr2 = "f(g(?x), ?y)";
        String litStr1 = "%s(%s, %s)".formatted(predicate1, termStr1, termStr2);
        String litStr2 = "%s(%s, %s)".formatted(predicate2, termStr1, termStr2);
        String litStr3 = "¬%s(%s, %s)".formatted(predicate3, termStr1, termStr2);

        Literal lit1 = Literal.parse(litStr1);
        Literal lit2 = Literal.parse(litStr2);
        Literal lit3 = Literal.parse(litStr3);

        Clause clause1 = new Clause(lit1, lit3);
        Clause clause2 = new Clause(lit2, lit3);

        assertNotEquals(clause1, clause2);
    }

    @Test
    void testToString() {
        String predicate1 = "Q";
        String predicate2 = "S";
        String predicate3 = "T";
        String termStr1 = "f(?x, a)";
        String termStr2 = "f(g(?x), ?y)";
        String litStr1 = "¬%s(%s, %s)".formatted(predicate1, termStr1, termStr2);
        String litStr2 = "¬%s(%s, %s)".formatted(predicate2, termStr1, termStr2);
        String litStr3 = "%s(%s, %s)".formatted(predicate3, termStr1, termStr2);

        Literal lit1 = Literal.parse(litStr1);
        Literal lit2 = Literal.parse(litStr2);
        Literal lit3 = Literal.parse(litStr3);

        Clause clause = new Clause(lit1, lit2, lit3);
        String expected = "[" + lit1 + ", " + lit2 + "] => [" + lit3 + "]";

        assertEquals(expected, clause.toString());
    }
}