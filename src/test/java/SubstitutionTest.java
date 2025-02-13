import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SubstitutionTest {
    @Test
    public void testApplySubstitutionToTerm() {
        testSubstitution(
                Term.parse("?x"),
                Term.parse("a"),
                Map.of("?x", new Term("a"))
        );
        testSubstitution(
                Term.parse("?z"),
                Term.parse("?z"),
                Map.of("?x", new Term("a"))
        );
    }

    @Test
    public void testApplySubstitutionToFunction() {
        testSubstitution(
                Term.parse("f(?x, ?y)"),
                Term.parse("f(a, b)"),
                Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
        );
    }

    @Test
    public void testApplySubstitutionToFunctionWithUnsubstitutedVariables() {
        testSubstitution(
                Term.parse("f(?x, ?y, ?z)"),
                Term.parse("f(a, b, ?z)"),
                Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
        );
    }

    @Test
    public void testApplySubstitutionToNestedFunction() {
        testSubstitution(
                Term.parse("f(g(?x), h(?y, ?z))"),
                Term.parse("f(g(a), h(b, ?z))"),
                Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
        );
    }

    @Test
    public void testApplySubstitutionWithFunctionAsValue() {
        testSubstitution(
                Term.parse("f(?x, ?y)"),
                Term.parse("f(g(a), h(b))"),
                Map.of("?x", Term.parse("g(a)"), "?y", Term.parse("h(b)"))
        );
    }

    @Test
    public void testApplySubstitutionToFunctionWithoutVariables() {
        testSubstitution(
                Term.parse("f(a, b)"),
                Term.parse("f(a, b)"),
                Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
        );
    }

    @Test
    public void testApplySubstitutionWithMultipleOccurrences() {
        testSubstitution(
                Term.parse("f(?x, ?x, ?y)"),
                Term.parse("f(a, a, b)"),
                Map.of("?x", Term.parse("a"), "?y", Term.parse("b"))
        );
    }

    private void testSubstitution(Term term, Term expected, Map<String, Term> substitution) {
        Term result = Substitution.applySubstitution(term, substitution);
        assertEquals(expected, result);
    }

    @Test
    public void testApplySubstitutionToLiteral() {
        testSubstitution(
                new Literal(false, "P", List.of(Term.parse("g(?y)"), Term.parse("f(?x, h(?x), ?y)"))),
                new Literal(false, "P", List.of(Term.parse("g(b)"), Term.parse("f(g(a), h(g(a)), b)"))),
                Map.of("?x", Term.parse("g(a)"), "?y", Term.parse("b"))
        );
    }

    private void testSubstitution(Literal lit, Literal expected, Map<String, Term> substitution) {
        Literal result = Substitution.applySubstitution(lit, substitution);
        assertEquals(expected, result);
        System.out.println(lit);
        System.out.println(result);
    }
}