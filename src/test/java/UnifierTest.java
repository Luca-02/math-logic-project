import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class UnifierTest {
    @Test
    public void testUnifySimpleVariables() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("?x"))),
                new Literal(false, "P", List.of(Term.parse("a"))),
                Map.of("?x", Term.parse("a"))
        );
    }

    @Test
    public void testUnifyVariableWithFunction() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("?x"))),
                new Literal(false, "P", List.of(Term.parse("f(a)"))),
                Map.of("?x", Term.parse("f(a)"))
        );
    }

    @Test
    public void testUnifyFunctionWithFunction() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                new Literal(false, "P", List.of(Term.parse("f(a)"))),
                Map.of("?x", Term.parse("a"))
        );
    }

    @Test
    public void testUnifyDifferentPredicates() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("?x"))),
                new Literal(false, "Q", List.of(Term.parse("a"))),
                null
        );
    }

    @Test
    public void testUnifyDifferentArity() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("?x"), Term.parse("?y"))),
                new Literal(false, "P", List.of(Term.parse("?x"))),
                null
        );
    }

    @Test
    public void testUnifyFailingNoSolutions() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("f(?x, ?x)"))),
                new Literal(false, "P", List.of(Term.parse("f(a, b)"))),
                null
        );
    }

    @Test
    public void testUnifyFailingDifferentFunctions() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                new Literal(false, "P", List.of(Term.parse("g(?x)"))),
                null
        );
    }

    @Test
    public void testUnifyFailingDifferentFunctionsArity() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                new Literal(false, "P", List.of(Term.parse("f(?x, ?y)"))),
                null
        );
    }

    @Test
    public void testUnifyOccursCheck() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("?x"))),
                new Literal(false, "P", List.of(Term.parse("f(?x)"))),
                null
        );
    }

    @Test
    public void testUnifySameLiteral1() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("?x"))),
                new Literal(false, "P", List.of(Term.parse("?x"))),
                null
        );
    }

    @Test
    public void testUnifySameLiteral2() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("x"), Term.parse("f(x, y)"))),
                new Literal(false, "P", List.of(Term.parse("x"), Term.parse("f(x, y)"))),
                null
        );
    }

    @Test
    public void testUnifyComplexFunction1() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("f(?x, g(?y))"))),
                new Literal(false, "P", List.of(Term.parse("f(a, g(b))"))),
                Map.of(
                        "?x", Term.parse("a"),
                        "?y", Term.parse("b")
                )
        );
    }

    @Test
    public void testUnifyComplexFunction2() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("g(?y)"))),
                new Literal(false, "P", List.of(Term.parse("g(f(a))"))),
                Map.of(
                        "?y", Term.parse("f(a)")
                )
        );
    }

    @Test
    public void testUnifyComplexFunction3() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("g(?x, ?y)"))),
                new Literal(false, "P", List.of(Term.parse("g(f(a), b)"))),
                Map.of(
                        "?x", Term.parse("f(a)"),
                        "?y", Term.parse("b")
                )
        );
    }

    @Test
    public void testUnifyComplexFunction4() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("f(g(?x, ?y), h(?z))"))),
                new Literal(false, "P", List.of(Term.parse("f(g(a, b), h(c))"))),
                Map.of(
                        "?x", Term.parse("a"),
                        "?y", Term.parse("b"),
                        "?z", Term.parse("c")
                )
        );
    }

    @Test
    public void testUnifyComplexFunction5() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("g(?y)"), Term.parse("f(?x, h(?x), ?y)"))),
                new Literal(false, "P", List.of(Term.parse("?x"), Term.parse("f(g(?z), ?w, ?z)"))),
                Map.of(
                        "?x", Term.parse("g(?z)"),
                        "?y", Term.parse("?z"),
                        "?w", Term.parse("h(g(?z))")
                )
        );
    }

    @Test
    public void testUnifyComplexFunction6() {
        testUnify(
                new Literal(false, "P", List.of(Term.parse("f(?x, g(?y))"), Term.parse("h(?z)"))),
                new Literal(false, "P", List.of(Term.parse("f(a, g(b))"), Term.parse("h(c)"))),
                Map.of(
                        "?x", Term.parse("a"),
                        "?y", Term.parse("b"),
                        "?z", Term.parse("c")
                )
        );
    }

    private void testUnify(Literal l1, Literal l2, Map<String, Term> expected) {
        Map<String, Term> substitution = Unifier.unify(l1, l2);

        if (expected == null) {
            assertNull(substitution);
        } else {
            assertNotNull(substitution);
            assertEquals(expected.size(), substitution.size());
            for (Map.Entry<String, Term> entry : expected.entrySet()) {
                String key = entry.getKey();
                Term expectedTerm = entry.getValue();
                Term actualTerm = substitution.get(key);

                assertNotNull(actualTerm);
                assertEquals(expectedTerm, actualTerm);
            }



        }
    }
}