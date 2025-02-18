package structure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TermTest {
    @ParameterizedTest(name = "{index} -> termStr={0}")
    @MethodSource("provideEqualTerms")
    void testEquals(String termStr) {
        Term term1 = Term.parse(termStr);
        Term term2 = Term.parse(termStr);
        assertEquals(term1, term2);
    }

    @ParameterizedTest(name = "{index} -> termStr={0}, expected={1}, isFunction={2}")
    @MethodSource("provideTermsForParsing")
    void testParsing(String termStr, Term expected, boolean isFunction, int symbolNumber) {
        Term term = Term.parse(termStr);
        assertEquals(expected, term);
        assertTrue(isFunction ? term.isFunction() : term.isVariable());
        if (isFunction && (term.getArguments() == null || term.getArguments().isEmpty())) {
            assertTrue(term.isConstant());
        }
        assertEquals(termStr, term.toString());
        assertEquals(symbolNumber, term.collectSymbols().size());
    }

    @Test
    void testStrangeFunctionName() {
        String termStr = "?f(?x)";
        Term term = Term.parse(termStr);
        assertEquals(new Term("?f", new Term("?x")), term);
        assertTrue(true);
        assertEquals(termStr, term.toString());
    }

    @ParameterizedTest(name = "{index} -> termStr={0}")
    @MethodSource("provideTermsForToString")
    void testToString(String termStr) {
        Term term = Term.parse(termStr);
        assertEquals(termStr, term.toString());
    }

    @ParameterizedTest(name = "{index} -> termStr1={0}, termStr2={1}, expected={2}")
    @MethodSource("provideTermsForOccurCheck")
    void testTermOccurIn(String termStr1, String termStr2, boolean expected) {
        Term term1 = Term.parse(termStr1);
        Term term2 = Term.parse(termStr2);
        assertEquals(expected, term1.occurIn(term2));
    }

    @Test
    void testClone() {
        String termStr = "f(g(?x), a)";
        Term term = Term.parse(termStr);
        Term clone = term.clone();

        assertEquals(term, clone);
        assertNotSame(term, clone);
        assertNotSame(term.getArguments(), clone.getArguments());
        for (int i = 0; i < term.getArguments().size(); i++) {
            assertNotSame(term.getArguments().get(i), clone.getArguments().get(i));
        }
    }

    Stream<String> provideEqualTerms() {
        return Stream.of("?x", "a", "f(?x)", "f(g(?x), a)");
    }

    Stream<Arguments> provideTermsForParsing() {
        return Stream.of(
                Arguments.of("?x", new Term("?x"), false, 1),
                Arguments.of("?x'", new Term("?x'"), false, 1),
                Arguments.of("a", new Term("a"), true, 1),
                Arguments.of("f(?x)", new Term("f", new Term("?x")), true, 2),
                Arguments.of("f(a)", new Term("f", new Term("a")), true, 2),
                Arguments.of("?f(?x)", new Term("?f", new Term("?x")), true, 2),
                Arguments.of("f(?x, a)", new Term("f", new Term("?x"), new Term("a")), true, 3),
                Arguments.of("f(g(?x), a)", new Term("f", new Term("g", List.of(new Term("?x"))), new Term("a")), true, 4)
        );
    }

    Stream<String> provideTermsForToString() {
        return Stream.of("?x", "a", "?f(a)", "f(g(?x), a)");
    }

    Stream<Arguments> provideTermsForOccurCheck() {
        return Stream.of(
                Arguments.of("?x", "?x", true),
                Arguments.of("?x", "f(?x)", true),
                Arguments.of("?y", "f(?x, ?y)", true),
                Arguments.of("a", "f(a)", true),
                Arguments.of("b", "f(a, b)", true),
                Arguments.of("?x", "f(g(?x), ?y)", true),
                Arguments.of("?y", "f(g(?x), ?y)", true),
                Arguments.of("g(?x)", "f(g(?x), y)", true),
                Arguments.of("g(a)", "f(x, g(a))", true),
                Arguments.of("?x", "f(?y)", false),
                Arguments.of("g(?y)", "f(g(?x), ?y)", false)
        );
    }
}