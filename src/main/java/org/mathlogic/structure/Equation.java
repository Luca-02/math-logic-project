package org.mathlogic.structure;

import java.util.Objects;

/**
 * Identify an equation {@code t ?= h}, where {@code t} and {@code h} are terms.
 */
public class Equation {
    private Term first;
    private Term second;

    public Equation(Term first, Term second) {
        this.first = first;
        this.second = second;
    }

    public Term getFirst() {
        return first;
    }

    public Term getSecond() {
        return second;
    }

    public void swap() {
        Term temp = this.first;
        this.first = this.second;
        this.second = temp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Equation pair)) return false;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return first.toString() + " ?= " + second.toString();
    }
}
