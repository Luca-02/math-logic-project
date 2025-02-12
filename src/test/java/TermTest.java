import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TermTest {
    @Test
    public void testEqualsVariableOrConstant() {
        String termStr = "x";
        Term term1 = Term.parse(termStr);
        Term term2 = Term.parse(termStr);

        assertEquals(term1, term2);
    }

    @Test
    public void testEqualsFunctionWithArguments() {
        String termStr = "f(g(x), y)";
        Term term1 = Term.parse(termStr);
        Term term2 = Term.parse(termStr);

        assertEquals(term1, term2);
    }

    @Test
    public void testParsingVariableOrConstant() {
        String termStr = "x";
        Term term = Term.parse(termStr);
        Term expected = new Term("x");

        assertEquals(expected, term);
        assertFalse(term.isFunction());
        assertTrue(term.isVarOrConst());
        assertEquals(termStr, term.toString());
    }

    @Test
    public void testParsingFunctionWithoutArguments() {
        // Note: function without argument is a constant
        String termStr = "f()";
        Term term = Term.parse(termStr);
        Term expected = new Term("f");

        assertEquals(expected, term);
        assertFalse(term.isFunction());
        assertTrue(term.isVarOrConst());
        assertEquals(termStr.substring(0, 1), term.toString());
    }

    @Test
    public void testParsingFunctionWithArguments() {
        String termStr = "f(x, y)";
        Term term = Term.parse(termStr);
        Term expected = new Term("f", List.of(new Term("x"), new Term("y")));

        assertEquals(expected, term);
        assertTrue(term.isFunction());
        assertFalse(term.isVarOrConst());
        assertEquals(termStr, term.toString());
    }

    @Test
    public void testParsingNestedFunctions() {
        String termStr = "f(g(x), y)";
        Term term = Term.parse(termStr);
        Term expected = new Term("f", List.of(new Term("g", List.of(new Term("x"))), new Term("y")));

        assertEquals(expected, term);
        assertTrue(term.isFunction());
        assertFalse(term.isVarOrConst());
        assertEquals(termStr, term.toString());
    }

    @Test
    public void testVariableOrConstantToString() {
        String termStr = "x";
        Term term = Term.parse(termStr);

        assertEquals(termStr, term.toString());
    }

    @Test
    public void testFunctionWithArgumentsToString() {
        String termStr = "f(g(x), y)";
        Term term = Term.parse(termStr);

        assertEquals(termStr, term.toString());
    }
}