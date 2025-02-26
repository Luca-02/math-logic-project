package org.mathlogic.comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mathlogic.structure.Literal;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MultisetComparatorTest {
    private MultisetComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new MultisetComparator();
    }

    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideParametersForLiteralComparison")
    void testLiteralComparison(Literal lit1, Literal lit2, int expected) {
        int result = comparator.compare(lit1.getMultisetView(), lit2.getMultisetView());
        int inverse = comparator.compare(lit2.getMultisetView(), lit1.getMultisetView());

        assertEquals(expected, result);
        assertEquals(expected * -1, inverse);
    }

    @ParameterizedTest(name = "{index} -> m={0}, n={1}, expected={2}")
    @MethodSource("provideParametersForCanTransformIntegerMultisets")
    void testCanTransformIntegerMultisets(Map<Integer, Integer> m, Map<Integer, Integer> n, boolean expected) {
        boolean result = comparator.canTransform(m, n);

        assertEquals(expected, result);
    }

    Stream<Arguments> provideParametersForLiteralComparison() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("=(?v, ?w)"),
                        Literal.parse("=(?w, b(?x))"),
                        -1
                ),
                Arguments.of(
                        Literal.parse("¬=(e(?x), d(?x))"),
                        Literal.parse("¬C(?x)"),
                        1
                ),
                Arguments.of(
                        Literal.parse("¬=(e(b), d(b))"),
                        Literal.parse("=(e(b), e(a))"),
                        1
                ),
                Arguments.of(
                        Literal.parse("¬=(e(?x), d(?x))"),
                        Literal.parse("=(e(?y), d(b))"),
                        0
                ),
                Arguments.of(
                        Literal.parse("=(e(?x), d(?y))"),
                        Literal.parse("=(e(?z), d(?h))"),
                        0
                ),
                Arguments.of(
                        Literal.parse("=(e(?x), d(?x))"),
                        Literal.parse("=(e(?x), d(?x))"),
                        0
                ),
                Arguments.of(
                        Literal.parse("=(?x, f(?y))"),
                        Literal.parse("=(?y, f(?x))"),
                        0
                ),
                Arguments.of(
                        Literal.parse("¬=(e(?x), d(?x))"),
                        Literal.parse("=(e(?y), d(a))"),
                        0
                )
        );
    }

    Stream<Arguments> provideParametersForCanTransformIntegerMultisets() {
        return Stream.of(
                Arguments.of(
                        Map.of(3, 1, 1, 2),
                        Map.of(2, 4, 1, 2),
                        true
                ),
                Arguments.of(
                        Map.of(2, 2, 1, 2),
                        Map.of(2, 1, 3, 1),
                        false
                ),
                Arguments.of(
                        Map.of(5, 1),
                        Map.of(4, 1, 0, 3),
                        true
                ),
                Arguments.of(
                        Map.of(5, 1),
                        Map.of(4, 1, 1, 3),
                        true
                ),
                Arguments.of(
                        Map.of(2, 2),
                        Map.of(1, 4),
                        true
                ),
                Arguments.of(
                        Map.of(1, 1, 2, 1, 3, 1),
                        Map.of(1, 1, 2, 1, 3, 1),
                        true
                ),
                Arguments.of(
                        Map.of(2, 1, 0, 1),
                        Map.of(1, 2, 0, 2),
                        true
                ),
                Arguments.of(
                        Map.of(1, 3),
                        Map.of(2, 1),
                        false
                ),
                Arguments.of(
                        Map.of(1, 2, 0, 2),
                        Map.of(0, 1, 2, 1),
                        false
                )
        );
    }
}