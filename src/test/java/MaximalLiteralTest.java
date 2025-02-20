import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structure.Clause;
import structure.Literal;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximalLiteralTest {
    @ParameterizedTest(name = "{index} -> clauses={0}, expected={1}")
    @MethodSource("provideParametersForIsMaximal")
    void testIsMaximal(Literal lit, Clause clause, boolean strictlyMaximal, boolean expected) {
        boolean result = MaximalLiteral.isMaximal(lit, clause, strictlyMaximal);

        assertEquals(expected, result);
    }

    Stream<Arguments> provideParametersForIsMaximal() {
        return Stream.of(
                Arguments.of(
                        Literal.parse("¬=(e(b), d(b))"),
                        Clause.parse("=(e(b), d(b)) => =(e(b), e(a))"),
                        true,
                        true
                ),
                Arguments.of(
                        Literal.parse("=(e(b), e(a))"),
                        Clause.parse("=(e(b), d(b)) => =(e(b), e(a))"),
                        true,
                        false
                )
        );
    }
}