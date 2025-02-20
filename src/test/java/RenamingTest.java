import structure.Clause;
import structure.Term;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RenamingTest {
    @ParameterizedTest(name = "{index} -> original={0}, toRename={1}, substitutions={2}")
    @MethodSource("provideParametersForGetSubstitutionForDisjointVariables")
    void testGetSubstitutionForDisjointVariables(Clause original, Clause toRename, Map<String, Term> expected) {
        Map<String, Term> result = Renaming.getSubstitutionForDisjointVariables(original, toRename);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "{index} -> original={0}, toRename={1}, substitutions={2}")
    @MethodSource("provideParametersForRenameClausesToDisjointVariable")
    void testRenameClausesToDisjointVariable(Clause original, Clause toRename, Clause expected) {
        Renaming.renameClausesToDisjointVariable(original, toRename);

        assertEquals(expected, toRename);
    }

    Stream<Arguments> provideParametersForGetSubstitutionForDisjointVariables() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("R(?x, s(?x)) => Q(f(?x))"),
                        Clause.parse("Q(f(?y)) =>"),
                        null
                ),
                Arguments.of(
                        Clause.parse("R(c, ?x, ?y) =>"),
                        Clause.parse("Q(f(?x)) =>"),
                        Map.of("?x", Term.parse("?x'"))
                ),
                Arguments.of(
                        Clause.parse("=> R(c, ?x, ?y)"),
                        Clause.parse("R(c, s(?x), ?y) =>"),
                        Map.of("?x", Term.parse("?x'"), "?y", Term.parse("?y'"))
                ),
                Arguments.of(
                        Clause.parse("R(c, ?x, ?y), Q(f(?x)) => R(c, s(?x), ?z)"),
                        Clause.parse("R(c, ?y, ?h) => Q(f(?x))"),
                        Map.of("?x", Term.parse("?x'"), "?y", Term.parse("?y'"))
                )
        );
    }

    Stream<Arguments> provideParametersForRenameClausesToDisjointVariable() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("R(?x, s(?x)) => Q(f(?x))"),
                        Clause.parse("Q(f(?x)) =>"),
                        Clause.parse("Q(f(?x')) =>")
                ),
                Arguments.of(
                        Clause.parse("=> R(c, ?x, ?y)"),
                        Clause.parse("R(c, s(?x), ?y) =>"),
                        Clause.parse("R(c, s(?x'), ?y') =>")
                ),
                Arguments.of(
                        Clause.parse("R(c, ?x, ?y), Q(f(?x)) => R(c, s(?x), ?z)"),
                        Clause.parse("R(c, ?y, ?h) => Q(f(?x))"),
                        Clause.parse("R(c, ?y', ?h) => Q(f(?x'))")
                )
        );
    }
}