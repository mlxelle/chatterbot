package org.cis1200;

import java.util.Arrays;
import java.util.List;

/**
 * Produces a deterministic sequence of numbers from a fixed list, cycling back
 * to the start when exhausted. Useful for writing reproducible tests.
 */
public class ListNumberGenerator implements NumberGenerator {

    // INVARIANT: numbers is nonnull and nonempty
    private final int[] numbers;
    // INVARIANT: index is a valid position in numbers
    private int index = 0;

    /**
     * Creates a ListNumberGenerator which generates numbers based off of a list
     * of possible integers.
     *
     * @param list - a list of integers to step through and output in the
     *             generator.
     * @throws IllegalArgumentException - when list is null, of size 0, or
     *                                  when any Integer in the list is null.
     */
    public ListNumberGenerator(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(
                    "list must be non-null and non-empty"
            );
        }
        this.numbers = new int[list.size()];
        int i = 0;
        for (Integer v : list) {
            if (v == null) {
                throw new IllegalArgumentException("Null element in Integer list.");
            }
            numbers[i] = v;
            i++;
        }
    }

    /**
     * Creates a ListNumberGenerator which generates numbers based off of an
     * array of possible integers.
     *
     * @param arr - an array of integers to step through and output in the
     *            generator.
     * @throws IllegalArgumentException - when arr is null or empty
     */
    public ListNumberGenerator(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("array must be non-null and non-empty");
        }
        this.numbers = Arrays.copyOf(arr, arr.length);
    }

    /**
     * Returns the next integer available in the list. Start over if the last number
     * is reached.
     *
     * @return a number from the list.
     */
    public int next() {
        if (index == numbers.length) {
            index = 0;
        }
        int num = numbers[index];
        index++;
        return num;
    }

    /**
     * Returns the next integer available in the list that is less than the
     * specified bound.
     *
     * @param bound - the max value that can be returned by this call to next.
     * @return a number between 0 (inclusive) and bound (exclusive)
     */
    @Override
    public int next(int bound) {
        return Math.min(bound - 1, Math.max(next(), 0));
    }

    @Override
    public String toString() {
        return Arrays.toString(numbers);
    }

}
