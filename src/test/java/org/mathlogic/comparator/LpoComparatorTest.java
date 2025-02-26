package org.mathlogic.comparator;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Term;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LpoComparatorTest {
    @ParameterizedTest(name = "{index} -> s={0}, s={1}, expected={2}")
    @MethodSource("provideParametersForLpoComparison")
    void testLpoComparison(Term s, Term t, int expected) {
        LpoComparator comparator = new LpoComparator();
        int result = comparator.compare(s, t);
        int inverse = comparator.compare(t, s);

        assertEquals(expected, result);
        assertEquals(expected * -1, inverse);
    }

    Stream<Arguments> provideParametersForLpoComparison() {
        return Stream.of(
                Arguments.of(Term.MINIMAL, Term.parse("?x"), -1),
                Arguments.of(Term.parse("?x"), Term.parse("?x"), 0),
                Arguments.of(Term.parse("?x"), Term.parse("?y"), 0),
                Arguments.of(Term.parse("f(g(a), h(a))"), Term.parse("f(g(a), h(a))"), 0),
                Arguments.of(Term.parse("?a"), Term.parse("?b"), 0),
                Arguments.of(Term.parse("?w"), Term.parse("b(?x)"), -1),
                Arguments.of(Term.parse("?x"), Term.parse("f(a)"), -1),
                Arguments.of(Term.parse("f(?x)"), Term.parse("g(?x)"), -1),
                Arguments.of(Term.parse("f(a)"), Term.parse("g(b)"), -1),
                Arguments.of(Term.parse("f(?x, b)"), Term.parse("g(b, c)"), -1),
                Arguments.of(Term.parse("f(a, b)"), Term.parse("g(b, c)"), -1),
                Arguments.of(Term.parse("f(a, s(c))"), Term.parse("g(b, z(c, d))"), -1),
                Arguments.of(Term.parse("f(a, b)"), Term.parse("f(a, c)"), -1),
                Arguments.of(Term.parse("f(a)"), Term.parse("f(a, b)"), -1),
                Arguments.of(Term.parse("f(a, b, c)"), Term.parse("f(a, b, c, d)"), -1),
                Arguments.of(Term.parse("f(a, b, c)"), Term.parse("f(d, e, f)"), -1),
                Arguments.of(Term.parse("f(a, b, c)"), Term.parse("f(a, b, c, d)"), -1),
                Arguments.of(Term.parse("f(a, b, c)"), Term.parse("f(d, e, f, g)"), -1),
                Arguments.of(Term.parse("a"), Term.parse("f(a, b)"), -1),
                Arguments.of(Term.parse("f(a)"), Term.parse("f(g(a))"), -1),
                Arguments.of(Term.parse("f(a, g(b))"), Term.parse("f(a, g(c))"), -1)
        );
    }
}