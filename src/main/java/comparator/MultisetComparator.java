package comparator;

import structure.Term;

import java.util.*;

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
    public int compare(Map<Term , Integer> m, Map<Term , Integer> n) {
        if (m.equals(n)) return 0;
        if (canTransform(m, n)) return 1;
        if (canTransform(n, m)) return -1;
        return 0;
    }

    /**
     * Returns {@code true} if multiset {@code M} can be transformed into multiset {@code N}.
     */
    private static boolean canTransform(Map<Term, Integer> m, Map<Term, Integer> n) {
        // Base case: if the multisets are equal
        if (m.equals(n)) return true;
        // Base case: if M is empty
        if (m.isEmpty()) return false;
        // Base case: if N is empty
        if (n.isEmpty()) return true;

        // Find the maximum in M and in N
        Term maxM = m.keySet().stream().max(comparator).orElse(Term.MINIMAL);
        Term maxN = n.keySet().stream().max(comparator).orElse(Term.MINIMAL);

        // If an element greater than maxM appears in N, the
        // transformation from M to N is impossible
        if(comparator.compare(maxN, maxM) > 0) return false;

        // If the maximum is the same, we check the occurrences of it in M and N
        if (comparator.compare(maxN, maxM) == 0) {
            int countMaxM = m.getOrDefault(maxM, 0);
            int countMaxN = n.getOrDefault(maxN, 0);

            if (countMaxM == countMaxN) {
                // We remove the maximum term from both multisets and continue
                Map<Term, Integer> newM = cloneMultiset(m);
                Map<Term, Integer> newN = cloneMultiset(n);
                newM.remove(maxM);
                newN.remove(maxN);
                return canTransform(newM, newN);
            } else if (countMaxM > countMaxN) {
                // Occurrences in excess must be transformed
                return tryTransform(m, n, maxM);
            } else { // countM < countN
                // If the maximum term appears fewer times in M than required in N, we cannot add
                // occurrences via transformation, so it fails.
                return false;
            }
        }

        // If the maximum in M is greater than the one in N, all occurrences of maxM must be transformed
        return tryTransform(m, n, maxM);
    }

    /**
     * Helper function that attempts to transform an occurrence of {@code x} in {@code M} into some
     * multiset {@code L = {y1, ..., yn}}, such that every element of {@code L} is strictly less than {@code x}.
     */
    private static boolean tryTransform(Map<Term, Integer> m, Map<Term, Integer> n, Term x) {
        // Let's look at the possible replacement candidates.
        // For example, let's try all the terms that appear in N and that are strictly less than x.
        List<Term> candidates = new ArrayList<>(n.keySet());
        candidates.removeIf(candidate -> !(comparator.compare(candidate, x) < 0));

        // If there is no candidates it cannot be transformed
        if (candidates.isEmpty()) return false;

        // We define an upper bound for the length of the combinations
        int upperBound = sumOccurrences(n) + 1;

        // For each possible dimension from 1 to upperBound, we generate all combinations of multisets
        for (int size = 1; size <= upperBound; size++) {
            List<Map<Term, Integer>> allCombinations = new ArrayList<>();
            populateAllCombinations(allCombinations, new HashMap<>(), candidates, size, 0);

            for (Map<Term, Integer> combination : allCombinations) {
                // Creates a copy of M, removes one occurrence of x and adds the resulting multiset L
                Map<Term, Integer> newM = cloneMultiset(m);
                decrement(newM, x);

                for (Map.Entry<Term, Integer> entry : combination.entrySet()) {
                    add(newM, entry.getKey(), entry.getValue());
                }

                // If the transformation leads to N, the function returns true
                if (canTransform(newM, n)) return true;
            }
        }
        return false;
    }

    /**
     * Create a clone of a multiset.
     */
    private static Map<Term, Integer> cloneMultiset(Map<Term, Integer> multiset) {
        return new HashMap<>(multiset);
    }

    /**
     * Returns the total sum of occurrences in a multiset.
     */
    private static int sumOccurrences(Map<Term, Integer> multiset) {
        int sum = 0;
        for (int count : multiset.values()) {
            sum += count;
        }
        return sum;
    }

    /**
     * Recursive function to generate all combinations (multisets) of {@code size}
     * elements from candidate {@code terms} (combinations with repetition, without considering the order).
     */
    private static void populateAllCombinations(
            List<Map<Term, Integer>> result,
            Map<Term, Integer> current,
            List<Term> candidates,
            int size,
            int startIndex
    ) {
        if (size == 0) {
            result.add(new HashMap<>(current));
            return;
        }
        for (int i = startIndex; i < candidates.size(); i++) {
            Term candidate = candidates.get(i);
            current.put(candidate, current.getOrDefault(candidate, 0) + 1);
            populateAllCombinations(result, current, candidates, size - 1, i);
            int count = current.get(candidate);
            if (count == 1) {
                current.remove(candidate);
            } else {
                current.put(candidate, count - 1);
            }
        }
    }

    /**
     * Decrements the count for term {@code t}, and if the count becomes 0, removes {@code t} from the map.
     */
    private static void decrement(Map<Term, Integer> multiset, Term t) {
        int count = multiset.getOrDefault(t, 0);
        if (count <= 1) multiset.remove(t);
        else multiset.put(t, count - 1);
    }

    /**
     * Adds {@code n} occurrences of term {@code t} into the multiset.
     */
    private static void add(Map<Term, Integer> multiset, Term t, int n) {
        int count = multiset.getOrDefault(t, 0);
        multiset.put(t, count + n);
    }
}
