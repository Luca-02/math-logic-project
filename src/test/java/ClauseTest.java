import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ClauseTest {
    @Test
    public void testEmptyClause() {
        Clause emptyClause = new Clause();
        assertTrue(emptyClause.isEmpty());
    }

    @Test
    public void testClauseEquality() {
        String predicate1 = "Q";
        String predicate2 = "S";
        String termStr1 = "f(x, y)";
        String termStr2 = "f(g(x), y)";
        Term term1 = Term.parse(termStr1);
        Term term2 = Term.parse(termStr2);
        Literal lit1 = new Literal(false, predicate1, List.of(term1, term2));
        Literal lit2 = new Literal(false, predicate1, List.of(term1, term2));
        Literal lit3 = new Literal(true, predicate2, List.of(term1, term2));

        Clause clause1 = new Clause(lit1, lit3);
        Clause clause2 = new Clause(lit2, lit3);

        assertEquals(clause1, clause2);
    }

    @Test
    public void testClauseInequality() {
        String predicate1 = "Q";
        String predicate2 = "S";
        String predicate3 = "T";
        String termStr1 = "f(x, y)";
        String termStr2 = "f(g(x), y)";
        Term term1 = Term.parse(termStr1);
        Term term2 = Term.parse(termStr2);
        Literal lit1 = new Literal(false, predicate1, List.of(term1, term2));
        Literal lit2 = new Literal(false, predicate2, List.of(term1, term2));
        Literal lit3 = new Literal(true, predicate3, List.of(term1, term2));

        Clause clause1 = new Clause(lit1, lit3);
        Clause clause2 = new Clause(lit2, lit3);

        assertNotEquals(clause1, clause2);
    }
}