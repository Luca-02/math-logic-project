package org.mathlogic.structure;

import org.junit.jupiter.api.Test;
import org.mathlogic.exception.ParsingEmptyLogicalStructureException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClauseTest {
    String predicate1 = "Q";
    String predicate2 = "S";
    String predicate3 = "T";
    String termStr1 = "f(?x, a)";
    String termStr2 = "f(g(?x), ?y)";
    String litStr1 = String.format("%s(%s)", predicate1, termStr1);
    String litStr2 = String.format("%s(%s, %s)", predicate2, termStr1, termStr2);
    String litStr3 = String.format("%s(%s, %s)", predicate3, termStr1, termStr2);

    @Test
    void testInvalidClause() {
        assertThrows(ParsingEmptyLogicalStructureException.class, () -> Clause.parse(""));
    }

    @Test
    void testEmptyClause() {
        Clause emptyClause = Clause.parse("=>");
        assertTrue(emptyClause.isEmpty());
    }

    @Test
    void testClauseEquality() {
        Clause clause1 = Clause.parse(String.format("%s => %s", litStr1, litStr2));
        Clause clause2 = Clause.parse(String.format("%s => %s", litStr1, litStr2));

        assertEquals(clause1, clause2);
    }

    @Test
    void testClauseInequality() {
        Clause clause1 = Clause.parse(String.format("%s => %s", litStr1, litStr3));
        Clause clause2 = Clause.parse(String.format("%s => %s", litStr2, litStr3));

        assertNotEquals(clause1, clause2);
    }

    @Test
    void testTautology() {
        Clause clause1 = Clause.parse(String.format("%s => %s", litStr1, litStr2));
        Clause clause2 = Clause.parse(String.format("%s => %s", litStr2, litStr2));
        Clause clause3 = Clause.parse("=(a, a) =>");

        assertFalse(clause1.isTautology());
        assertTrue(clause2.isTautology());
        assertTrue(clause3.isTautology());
    }

    @Test
    void testToString() {
        Literal lit1 = Literal.parse(litStr1);
        Literal lit2 = Literal.parse(litStr2);
        Literal lit3 = Literal.parse(litStr3);

        Clause clause = Clause.parse(String.format("%s => %s, %s", litStr1, litStr2, litStr3));
        String expected = String.format("%s => %s, %s", lit1, lit2, lit3);

        assertEquals(expected, clause.toString());
    }

    @Test
    void testCollectSymbols() {
        Clause clause = Clause.parse(String.format("%s => %s, %s", litStr1, litStr2, litStr3));

        assertEquals(20, clause.collectSymbols().size());
    }

    @Test
    void testCopy() {
        Clause clause = Clause.parse(String.format("%s => %s, %s", litStr1, litStr2, litStr3));
        Clause copy = clause.copy();

        assertEquals(clause, copy);
        assertNotSame(clause, copy);

        List<Literal> positive = clause.getPositiveLiterals().stream().toList();
        List<Literal> positiveClone = copy.getPositiveLiterals().stream().toList();
        assertNotSame(clause.getPositiveLiterals(), copy.getPositiveLiterals());
        for (int i = 0; i < positive.size(); i++) {
            assertNotSame(positive.get(i), positiveClone.get(i));
        }

        assertNotSame(clause.getNegativeLiterals(), copy.getNegativeLiterals());
        List<Literal> negative = clause.getNegativeLiterals().stream().toList();
        List<Literal> negativeClone = copy.getNegativeLiterals().stream().toList();
        for (int i = 0; i < negative.size(); i++) {
            assertNotSame(negative.get(i), negativeClone.get(i));
        }
    }
}