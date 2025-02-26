package org.mathlogic.structure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.exception.ArgumentIndexOutOfBoundsException;
import org.mathlogic.exception.ParsingEmptyLogicalStructureException;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TermTest {
    @ParameterizedTest(name = "{index} -> termStr={0}")
    @MethodSource("provideParametersForEquals")
    void testEquals(String termStr) {
        Term term1 = Term.parse(termStr);
        Term term2 = Term.parse(termStr);

        assertEquals(term1, term2);
    }

    @ParameterizedTest(name = "{index} -> termStr={0}, expected={1}, isFunction={2}, symbolNumber={3}")
    @MethodSource("provideParametersForParsing")
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
    void testInvalidTerm() {
        assertThrows(ParsingEmptyLogicalStructureException.class, () -> Term.parse(""));
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
    @MethodSource("provideParametersForToString")
    void testToString(String termStr) {
        Term term = Term.parse(termStr);

        assertEquals(termStr, term.toString());
    }

    @ParameterizedTest(name = "{index} -> termStr1={0}, termStr2={1}, expected={2}")
    @MethodSource("provideParametersForOccurCheck")
    void testTermOccurIn(String termStr1, String termStr2, boolean expected) {
        Term term1 = Term.parse(termStr1);
        Term term2 = Term.parse(termStr2);

        assertEquals(expected, term1.occurIn(term2));
    }

    @ParameterizedTest(name = "{index} -> term={0}, expected={1}, substitution={2}")
    @MethodSource("provideParametersForApplySubstitutionToTerm")
    void testApplySubstitutionToTerm(Term term, Term expected, Map<String, Term> substitution) {
        Term result = term.applySubstitution(substitution);

        assertEquals(expected, result);
    }

    @Test
    void testCopy() {
        String termStr = "f(g(?x), a)";
        Term term = Term.parse(termStr);
        Term copy = term.copy();

        assertEquals(term, copy);
        assertNotSame(term, copy);
        assertNotSame(term.getArguments(), copy.getArguments());
        for (int i = 0; i < term.getArguments().size(); i++) {
            assertNotSame(term.getArguments().get(i), copy.getArguments().get(i));
        }
    }

    @Test
    void testReplaceArgument() {
        Term term = Term.parse("f(?x, g(?y))");
        Term replacement = Term.parse("h(a, b)");
        Term expected = Term.parse("f(?x, h(a, b))");
        Term result = term.replaceArgument(1, replacement);

        assertEquals(expected, result);
    }

    @Test
    void testReplaceArgumentException() {
        assertThrows(
                ArgumentIndexOutOfBoundsException.class,
                () -> {
                    Term term = Term.parse("f(?x, g(?y))");
                    Term replacement = Term.parse("h(a, b)");
                    term.replaceArgument(2, replacement);
                }
        );
    }

    Stream<String> provideParametersForEquals() {
        return Stream.of("?x", "a", "f(?x)", "f(g(?x), a)");
    }

    Stream<Arguments> provideParametersForParsing() {
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

    Stream<String> provideParametersForToString() {
        return Stream.of("?x", "a", "?f(a)", "f(g(?x), a)");
    }

    Stream<Arguments> provideParametersForOccurCheck() {
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

    Stream<Arguments> provideParametersForApplySubstitutionToTerm() {
        return Stream.of(
                Arguments.of(
                        Term.parse("?x"),
                        Term.parse("a"),
                        Map.of("?x", Term.parse("a"))
                ),
                Arguments.of(
                        Term.parse("?z"),
                        Term.parse("?z"),
                        Map.of("?x", Term.parse("a"))),
                Arguments.of(
                        Term.parse("f(?x, ?y)"),
                        Term.parse("f(a, b)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, ?y, ?z)"),
                        Term.parse("f(a, b, ?z)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(g(?x), h(?y, ?z))"),
                        Term.parse("f(g(a), h(b, ?z))"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, ?x, ?y)"),
                        Term.parse("f(a, a, b)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, ?y)"),
                        Term.parse("f(g(a), h(b))"),
                        Map.of("?x", Term.parse("g(a)"), "?y", Term.parse("h(b)"))
                ),
                Arguments.of(
                        Term.parse("f(a, b)"),
                        Term.parse("f(a, b)"),
                        Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
                ),
                Arguments.of(
                        Term.parse("f(?x, b)"),
                        Term.parse("f(?x, b)"),
                        Map.of()
                ),
                Arguments.of(
                        Term.parse("f(?x, ?y)"),
                        Term.parse("f(?x, ?y)"),
                        Map.of("?z", Term.parse("c"))
                ),
                Arguments.of(
                        Term.parse("?x"),
                        Term.parse("!x"),
                        Map.of("?x", Term.parse("!x"))
                )
        );
    }
}