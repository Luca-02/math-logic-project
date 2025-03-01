package org.mathlogic.structure;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Identify an equation {@code t ?= u}, where {@code t} and {@code u} are terms.
 */
public class Equation {
    private Term left;
    private Term right;

    public Equation(@NotNull Term left, @NotNull Term right) {
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
