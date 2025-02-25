package org.mathlogic.utility;

import org.mathlogic.structure.Clause;
import org.mathlogic.structure.Term;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RenamingTest {
    @ParameterizedTest(name = "{index} -> original={0}, toRename={1}, expected={2}")
    @MethodSource("provideParametersForRenameClausesToDisjointVariable")
    void testRenameClausesToDisjointVariable(Clause original, Clause toRename, Clause expected) {
        Renaming.renameClausesToDisjointVariable(original, toRename);

        assertEquals(expected, toRename);
    }

    Stream<Arguments> provideParametersForRenameClausesToDisjointVariable() {
        return Stream.of(
                Arguments.of(
                        Clause.parse("R(?x, s(?x)) => Q(f(?x))"),
                        Clause.parse("Q(f(?y)) =>"),
                        Clause.parse("Q(f(?y)) =>")
                ),
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