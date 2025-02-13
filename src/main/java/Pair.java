import java.util.Objects;

/**
 * Identify an equation t ?= h, where t and h are terms.
 */
public class Pair<T> {
    private T first;
    private T second;
    private boolean toDelete;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
        this.toDelete = false;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public boolean toDelete() {
        return toDelete;
    }

    public void markToDelete() {
        toDelete = true;
    }

    public void swap() {
        T temp = this.first;
        this.first = this.second;
        this.second = temp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair<?> pair))
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
