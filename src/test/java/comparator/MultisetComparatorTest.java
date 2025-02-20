package comparator;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structure.Literal;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MultisetComparatorTest {
    @ParameterizedTest(name = "{index} -> lit1={0}, lit2={1}, expected={2}")
    @MethodSource("provideLiteralsForLiteralComparison")
    void testLiteralComparison(Literal lit1, Literal lit2, int expected) {
        MultisetComparator comparator = new MultisetComparator();
        int result = comparator.compare(lit1.getMultiset(), lit2.getMultiset());
        int inverse = comparator.compare(lit2.getMultiset(), lit1.getMultiset());
        assertEquals(expected, result);
        assertEquals(expected * -1, inverse);
    }

    Stream<Arguments> provideLiteralsForLiteralComparison() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("¬=(c(?x), d(?x))"),
                        Literal.parse("¬B(x)"),
                        -1
                ),
                Arguments.of(
                        Literal.parse("¬=(c(a), b(a))"),
                        Literal.parse("=(c(a), c(b))"),
                        -1
                ),
                Arguments.of(
                        Literal.parse("¬=(c(?x), d(?x))"),
                        Literal.parse("=(d(?y), c(a))"),
                        0
                )
        );
    }
}