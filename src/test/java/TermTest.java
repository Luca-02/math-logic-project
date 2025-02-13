import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TermTest {
    @Test
    public void testEquals() {
        testEquals("?x");
        testEquals("a");
        testEquals("f(g(?x), a)");
    }

    private void testEquals(String termStr) {
        Term term1 = Term.parse(termStr);
        Term term2 = Term.parse(termStr);

        assertEquals(term1, term2);
    }

    @Test
    public void testParsingVariable() {
        testParsing("?x", new Term("?x"), false);
    }

    @Test
    public void testParsingConstant() {
        testParsing("f", new Term("f"), true);
        testParsing("f()", "f", new Term("f"), true);
    }

    @Test
    public void testParsingFunction() {
        testParsing("f(?x)", new Term("f",
                List.of(new Term("?x"))), true);
        testParsing("?f(?x)", new Term("?f",
                List.of(new Term("?x"))), true);
        testParsing("f(?x, a)", new Term("f",
                List.of(new Term("?x"), new Term("a"))), true);
    }

    @Test
    public void testParsingNestedFunction() {
        testParsing("f(g(?x), a)", new Term("f",
                List.of(new Term("g", List.of(new Term("?x"))), new Term("a"))), true);
    }

    private void testParsing(String termStr, Term expected, boolean isFunction) {
        testParsing(termStr, termStr, expected, isFunction);
    }

    private void testParsing(String termStr, String expectedStr, Term expected, boolean isFunction) {
        Term term = Term.parse(termStr);

        assertEquals(expected, term);
        assertTrue(isFunction ? term.isFunction() : term.isVariable());
        assertEquals(expectedStr, term.toString());
    }


    @Test
    public void testVariableToString() {
        testToString("x?");
        testToString("a");
    }

    @Test
    public void testFunctionToString() {
        testToString("?f(a)");
        testToString("f(g(?x), a)");
    }

    private void testToString(String termStr) {
        Term term = Term.parse(termStr);

        assertEquals(termStr, term.toString());
    }

    @Test
    public void testVariableOccurInVariable() {
        testTermOccurIn("?x", "?x", true);
    }

    @Test
    public void testVariableOccurInFunction() {
        testTermOccurIn("?x", "f(?x)", true);
        testTermOccurIn("?y", "f(?x, ?y)", true);
    }

    @Test
    public void testConstantOccurInFunction() {
        testTermOccurIn("a", "f(a)", true);
        testTermOccurIn("b", "f(a, b)", true);
    }

    @Test
    public void testVariableOccurInNestedFunction() {
        testTermOccurIn("?x", "f(g(?x), ?y)", true);
        testTermOccurIn("?y", "f(g(?x), ?y)", true);
    }

    @Test
    public void testFunctionOccurInNestedFunction() {
        testTermOccurIn("g(?x)", "f(g(?x), y)", true);
        testTermOccurIn("g(a)", "f(x, g(a))", true);
    }

    @Test
    public void testVariableNotOccurInFunction() {
        testTermOccurIn("?x", "f(?y)", false);
    }

    @Test
    public void testFunctionNotOccurInNestedFunction() {
        testTermOccurIn("g(?y)", "f(g(?x), ?y)", false);
    }

    private void testTermOccurIn(String termStr1, String termStr2, boolean occurIn) {
        Term t1 = Term.parse(termStr1);
        Term t2 = Term.parse(termStr2);

        assertEquals(occurIn, t1.occurIn(t2));
    }
}