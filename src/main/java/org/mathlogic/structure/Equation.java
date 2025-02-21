package org.mathlogic.structure;

import java.util.Objects;

/**
 * Identify an equation {@code t ?= h}, where {@code t} and {@code h} are terms.
 */
public class Equation {
    private Term left;
    private Term right;

    public Equation(Term left, Term right) {
        this.left = left;
        this.right = right;
    }

    public Term getLeft() {
        return left;
    }

    public Term getRight() {
        return right;
    }

    public void swap() {
        Term temp = this.left;
        this.left = this.right;
        this.right = temp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Equation pair)) return false;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
