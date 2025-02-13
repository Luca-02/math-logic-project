import java.util.*;

public class Unifier {
    public static Map<String, Term> unify(Literal l1, Literal l2) {
        if (!l1.getPredicate().equals(l2.getPredicate()) ||
                l1.getTerms().size() != l2.getTerms().size()) {
            return null;
        }

        List<Pair<Term>> equations = new ArrayList<>();
        for (int i = 0; i < l1.getTerms().size(); i++) {
            equations.add(new Pair<>(l1.getTerms().get(i), l2.getTerms().get(i)));
        }

        // printEquations(equations);

        boolean changed;
        do {
            changed = false;
            int equationSize = equations.size();

            for (int i = 0; i < equationSize; i++) {
                Pair<Term> equation = equations.get(i);
                Term t1 = equation.getFirst();
                Term t2 = equation.getSecond();

                // Rule 1: delete t ?= t
                if (t1.equals(t2)) {
                    equation.markToDelete();
                    // System.out.println("Rule 1: " + equation);
                }

                // Rule 2: overwrite f(t1, ..., tn) ?= g(u1, ..., um)
                // with n equation of t1 ?= u1, ..., tn ?= un
                else if (t1.isFunction() && t2.isFunction()) {
                    if (isFailing(t1, t2)) {
                        return null;
                    }
                    equation.markToDelete();
                    for (int j = 0; j < t1.getArguments().size(); j++) {
                        equations.add(new Pair<>(t1.getArguments().get(j), t2.getArguments().get(j)));
                    }
                    changed = true;
                    // System.out.println("Rule 2: " + equation);
                }

                // Rule 3: overwrite t ?= x with x ?= t, if t is not a variable and x is a variable
                else if (t1.isFunction() && t2.isVariable()) {
                    equation.swap();
                    changed = true;
                    // System.out.println("Rule 3: " + equation);
                }

                // Rule 4: if x ?= t with x not occurring in t, replace
                // x with t in every other equation of the current list
                else if (t1.isVariable()) {
                    if (occurCheck(t1, t2)) {
                        return null;
                    }
                    List<Pair<Term>> substitutedEquations =
                            applySubstitutionToEquations(equations, t1.getName(), t2, equation);
                    if (substitutedEquations != null) {
                        equations = substitutedEquations;
                        changed = true;
                    }
                    // System.out.println("Rule 4: " + equation);
                }
                // printEquations(equations);
            }

            // Remove the equation founded and marked to delete
            equations.removeIf(Pair::toDelete);
        } while (changed);

        if (equations.isEmpty()) {
            return null;
        }

        Map<String, Term> substitutions = new HashMap<>();
        for (Pair<Term> equation : equations) {
            substitutions.put(equation.getFirst().getName(), equation.getSecond());
        }

        return substitutions;
    }

    private static List<Pair<Term>> applySubstitutionToEquations(
            List<Pair<Term>> equations, String target, Term substitute, Pair<Term> equationToAvoid) {
        Map<String, Term> substitution = Map.of(target, substitute);
        List<Pair<Term>> result = new ArrayList<>();
        int substitutionCount = 0;

        for (Pair<Term> pair : equations) {
            if (pair.equals(equationToAvoid)) {
                result.add(pair);
            } else {
                Term firstSubstituted = null;
                Term secondSubstituted = null;
                if (Term.parse(target).occurIn(pair.getFirst())) {
                    firstSubstituted = Substitution.applySubstitution(pair.getFirst(), substitution);
                    substitutionCount++;
                }
                if (Term.parse(target).occurIn(pair.getSecond())) {
                    secondSubstituted = Substitution.applySubstitution(pair.getSecond(), substitution);
                    substitutionCount++;
                }
                result.add(new Pair<>(
                        firstSubstituted != null ? firstSubstituted : pair.getFirst(),
                        secondSubstituted != null ? secondSubstituted : pair.getSecond()
                ));
            }
        }

        if (substitutionCount > 0) {
            return result;
        } else {
            return null;
        }
    }

    // Rule 5: fail if f(t1, ..., tn) ?= g(u1, ..., um) with f != g (or n != m)
    private static boolean isFailing(Term t1, Term t2) {
        return !t1.getName().equals(t2.getName()) ||
                t1.getArguments().size() != t2.getArguments().size();
    }

    // Rule 6: fail if x ?= t with x != t but x occurring in t
    private static boolean occurCheck(Term t1, Term t2) {
        return !t1.equals(t2) && t1.occurIn(t2);
    }

    private static void printEquations(List<Pair<Term>> equations) {
        StringBuilder sb = new StringBuilder();
        for (Pair<Term> equation : equations) {
            sb.append(equation.toString()).append(", ");
        }
        System.out.println(sb);
    }
}