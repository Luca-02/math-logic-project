package comparator;

import structure.Term;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MultisetComparator implements Comparator<Map<Term , Integer>> {
    private static final Comparator<Term> comparator = new LpoComparator();

    /**
     * Let define {@code M} and {@code N} two multisets. We say that {@code M > N} iff we can
     * transform {@code M} into {@code N} through a finite sequence of steps
     * {@code M = M[0] -> M[1] -> M[2] -> ... -> M[n] = N} where each {@code M[i+1]} is obtained from {@code M[i]}
     * replacing an occurrence {@code x} of {@code M[i]} with a multiset of elements all strictly smaller
     * than {@code x}.
     */
    @Override
    public int compare(
            @NotNull Map<Term , Integer> m,
            @NotNull Map<Term , Integer> n
    ) {
        if (m.equals(n)) return 0;

        if (canTransformReduction(m, n)) return 1;
        if (canTransformReduction(n, m)) return -1;
        return 0;
    }

    /**
     * A reduction is applied to the problem from multisets of terms to multisets of integers.
     */
    public boolean canTransformReduction(
            @NotNull Map<Term, Integer> m,
            @NotNull Map<Term, Integer> n
    ) {
        Map<Term, Integer> integerMapping = createIntegerMapping(m, n);
        Map<Integer, Integer> reducedM = reduceToIntegerMultiset(m, integerMapping);
        Map<Integer, Integer> reducedN = reduceToIntegerMultiset(n, integerMapping);
        return canTransform(reducedM, reducedN);
    }

    /**
     * Check whether, starting from the multiset {@code M}, the multiset {@code N} can be obtained through
     * transformations that replace an element {@code x} in {@code M} with a multiset of elements
     * all strictly minor than {@code x}.
     */
    public boolean canTransform(
            @NotNull Map<Integer, Integer> m,
            @NotNull Map<Integer, Integer> n
    ) {
        // Let's determine the maximum value present in the two mappings
        int maxVal = 0;
        for (int key : m.keySet()) {
            if (key > maxVal) maxVal = key;
        }
        for (int key : n.keySet()) {
            if (key > maxVal) maxVal = key;
        }

        // Flag indicating whether there is at least one "excess" (i.e., transformable) element
        // that can produce a multiset of elements of lesser value
        boolean availableForSplit = false;

        // We scroll the values from maximum to 1, because the transformation is
        // applicable only to elements greater than 0
        for (int v = maxVal; v >= 1; v--) {
            int countM = m.getOrDefault(v, 0);
            int countN = n.getOrDefault(v, 0);

            // If we need more occurrences of v in N than we have in M, and we don't
            // yet have any transformations from a larger value, then we can't get N
            if (countN > countM && !availableForSplit) {
                return false;
            }

            // We enable the possibility of transformation for the smaller values.
            // If there are more occurrences of v in M than are needed in N and v > 1,
            // then that excess allows us to transform an element v into a multiset
            // composed of strictly minor elements
            if (countM > countN && v > 1) {
                availableForSplit = true;
            }
        }

        // If the cycle ends without finding any deficits,
        // transformation is possible.
        return true;
    }

    /**
     * Create a term to integer mapping from two given multisets of terms.
     */
    private Map<Term, Integer> createIntegerMapping(
            Map<Term, Integer> m,
            Map<Term, Integer> n
    ) {
        Set<Term> elements = new TreeSet<>(comparator);
        elements.addAll(m.keySet());
        elements.addAll(n.keySet());

        Map<Term, Integer> integerMapping = new HashMap<>();
        Term last = Term.MINIMAL;
        int count = 0;
        for (Term term : elements) {
            integerMapping.put(term, count);
            if (term.equals(Term.MINIMAL) || comparator.compare(last, term) < 0) {
                count++;
            }
            last = term;
        }

        return integerMapping;
    }

    /**
     * Reduces a multiset of terms to a multiset of integers with a given term to integer mapping.
     * The terms are sorted using {@link LpoComparator}, and each term is mapped to an integer key.
     */
    private Map<Integer, Integer> reduceToIntegerMultiset(
            Map<Term , Integer> multiset,
            Map<Term , Integer> integerMapping
    ) {
        Map<Integer, Integer> reduction = new HashMap<>();
        for (Map.Entry<Term, Integer> entry : multiset.entrySet()) {
            reduction.put(integerMapping.get(entry.getKey()), entry.getValue());
        }
        return reduction;
    }
}
