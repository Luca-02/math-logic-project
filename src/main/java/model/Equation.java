package model;

import java.util.Objects;

/**
 * Identify an equation t ?= h, where t and h are terms.
 */
public class Equation {
    private Term first;
    private Term second;
    private boolean toDelete;

    public Equation(Term first, Term second) {
        this.first = first;
        this.second = second;
        this.toDelete = false;
    }

    public Term getFirst() {
        return first;
    }

    public Term getSecond() {
        return second;
    }

    public boolean toDelete() {
        return toDelete;
    }

    public void markToDelete() {
        toDelete = true;
    }

    public void swap() {
        Term temp = this.first;
        this.first = this.second;
        this.second = temp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Equation pair))
            return false;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst(), getSecond());
    }

    @Override
    public String toString() {
        return first.toString() + " ?= " + second.toString();
    }
}
