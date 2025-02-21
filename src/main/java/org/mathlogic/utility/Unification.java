package org.mathlogic.utility;

import org.mathlogic.structure.Equation;
import org.mathlogic.structure.Literal;
import org.mathlogic.structure.Term;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Unification {
    /**
     * Control whether substitutions applied to two literals make them unify correctly.
     */
    public static boolean unificationCorrectness(
            @NotNull Literal lit1,
            @NotNull Literal lit2,
            @NotNull Map<String, Term> substitutions
    ) {
        Literal subLit1 = Substitution.applySubstitution(lit1, substitutions);
        Literal subLit2 = Substitution.applySubstitution(lit2, substitutions);

        if (subLit1.isNegated() != subLit2.isNegated()) {
            subLit1.negate();
        }

        return Objects.equals(subLit1, subLit2);
    }

    /**
     * Control whether substitutions applied to one literals produce the other literal.
     */
    public static boolean matchingCorrectness(
            @NotNull Literal lit1,
            @NotNull Literal lit2,
            @NotNull Map<String, Term> substitutions
    ) {
        Literal subLit1 = Substitution.applySubstitution(lit1, substitutions);

        if (subLit1.isNegated() != lit2.isNegated()) {
            subLit1.negate();
        }

        return Objects.equals(subLit1, lit2);
    }

    /**
     * Unify two literals. Returns a substitution map if unification succeeds,
     * otherwise returns {@code null}.
     */
    public static Map<String, Term> unify(@NotNull Literal l1, @NotNull Literal l2) {
        return unifyOrMatch(l1, l2, true);
    }

    /**
     * Matching two literals. Returns a substitution map if the match is successful,
     * otherwise returns {@code null}.
     */
    public static Map<String, Term> match(@NotNull Literal l1, @NotNull Literal l2) {
        return unifyOrMatch(l1, l2, false);
    }

    /**
     * <b>org.mathlogic.utility.Unification rule 5:</b> fail if {@code f(t1, ..., tn) ?= g(u1, ..., um)} with {@code f != g (or n != m)}.
     */
    public static boolean isFailing(@NotNull Term t1, @NotNull Term t2) {
        return !t1.getName().equals(t2.getName()) ||
                t1.getArguments().size() != t2.getArguments().size();
    }

    /**
     * <b>org.mathlogic.utility.Unification rule 6:</b> fail if {@code x ?= t with x != t} but {@code x} occurring in {@code t}.
     */
    public static boolean occurCheck(@NotNull Term t1, @NotNull Term t2) {
        return !t1.equals(t2) && t1.occurIn(t2);
    }

    /**
     * General method for unification and matching.
     */
    private static Map<String, Term> unifyOrMatch(
            Literal l1,
            Literal l2,
            boolean isUnification
    ) {
        if (hasDifferentStructure(l1, l2)) {
            return null;
        }

        List<Equation> equations = new ArrayList<>();
        for (int i = 0; i < l1.getTerms().size(); i++) {
            equations.add(new Equation(l1.getTerms().get(i), l2.getTerms().get(i)));
        }

        boolean changed;
        do {
            changed = false;
            int equationSize = equations.size();
            List<Equation> equationsToDelete = new ArrayList<>();

            for (int i = 0; i < equationSize; i++) {
                Equation equation = equations.get(i);
                Term t1 = equation.getLeft();
                Term t2 = equation.getRight();

                // Rule 1: delete t ?= t
                if (t1.equals(t2)) {
                    equationsToDelete.add(equation);
                }

                // Rule 2: overwrite f(t1, ..., tn) ?= g(u1, ..., um)
                // with n equation of t1 ?= u1, ..., tn ?= un
                else if (t1.isFunction() && (!isUnification || t2.isFunction())) {
                    if (isFailing(t1, t2)) {
                        return null;
                    }
                    equationsToDelete.add(equation);
                    for (int j = 0; j < t1.getArguments().size(); j++) {
                        equations.add(new Equation(t1.getArguments().get(j), t2.getArguments().get(j)));
                    }
                    changed = true;
                }

                // Rule 3: overwrite t ?= x with x ?= t, if t is not a variable and x is a variable
                else if (isUnification && t1.isFunction() && t2.isVariable()) {
                    equation.swap();
                    changed = true;
                }

                // Rule 4: if x ?= t with x not occurring in t, replace
                // x with t in every other equation of the current list
                else if (t1.isVariable()) {
                    if (occurCheck(t1, t2)) {
                        return null;
                    }
                    List<Equation> substitutedEquations =
                            applySubstitutionToEquations(equations, t1.getName(), t2, equation);
                    if (substitutedEquations != null) {
                        equations = substitutedEquations;
                        changed = true;
                    }
                }
            }

            // Remove the equation founded and marked to delete
            for (Equation toDelete : equationsToDelete) {
                equations.remove(toDelete);
            }
            equationsToDelete.clear();
        } while (changed);

        Map<String, Term> substitutions = new HashMap<>();
        for (Equation equation : equations) {
            substitutions.put(equation.getLeft().getName(), equation.getRight());
        }

        return substitutions.isEmpty() ? null : substitutions;
    }

    /**
     * Checks whether two literals have the same predicate and the same number of terms.
     */
    private static boolean hasDifferentStructure(Literal l1, Literal l2) {
        return !l1.getPredicate().equals(l2.getPredicate()) ||
                l1.getTerms().size() != l2.getTerms().size();
    }

    /**
     * Replaces all occurrences of a variable with one term in equations.
     */
    private static List<Equation> applySubstitutionToEquations(
            List<Equation> equations,
            String target,
            Term substitute,
            Equation equationToAvoid
    ) {
        Map<String, Term> substitution = Map.of(target, substitute);
        List<Equation> updatedEquations = new ArrayList<>();
        boolean modified = false;

        for (Equation eq : equations) {
            if (eq.equals(equationToAvoid)) {
                updatedEquations.add(eq);
                continue;
            }

            Term newFirst = Substitution.applySubstitution(eq.getLeft(), substitution);
            Term newSecond = Substitution.applySubstitution(eq.getRight(), substitution);

            if (!newFirst.equals(eq.getLeft()) || !newSecond.equals(eq.getRight())) {
                modified = true;
            }

            updatedEquations.add(new Equation(newFirst, newSecond));
        }

        if (modified) {
            return updatedEquations;
        } else {
            return null;
        }
    }
}