package org.mathlogic;

import org.mathlogic.structure.Clause;

import java.util.Set;

public class Main {
    /**
     * Example of usage.
     */
    public static void main(String[] args) {
        AutomaticCalculus resolver = new CalculusR();
        Set<Clause> clauses =                         Set.of(
                Clause.parse("Q(f(?y)) => R(c, ?y)"),
                Clause.parse("=>")
        );
        System.out.println(resolver.refute(clauses));
    }
}
