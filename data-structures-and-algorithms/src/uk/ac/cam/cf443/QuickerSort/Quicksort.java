/**
 * A class to show the various optimisations which can be made to the textbook definition of quick sort in order to
 * improve its perfomance on
 */
public class Quicksort {

    public static void sort(int[] vals, int begin, int end) {
        if (vals.length <= 1) return;

        // Careful selection of the pivot -- median of 3 etc.
        int pivotIndex = end - 1;
        int midIndex = 0;

        // Use a counter to track the recursion depth. If the depth is greater than some maximum value, perform
        // a standard stable sort such as heapsort.


        // Sort small ranges with insertion sort. In theory, local insertion sorting should be more optimal than global
        // insertion sorting, since the values should be in the cache. Using local insertion sorting sacrifices
        // instruction cache misses for data cache misses


        // Could tail recurse into the larger of the two sections and make a normal recursive call on the smaller
        // section. This


        // The partitioning algorithm could be improved (sentinal partition core), but not for floating point numbers.
        // since < operator does not have a strict order on NaN - Not implemented by the standard C++ library


        // Many of the final ranges are small. Not worth the overhead of running an insertion sort. Instead, could
        // switch on the value of (end - start):
        //   - if 0 or 1, range is sorted
        //   - if 2, 3, or 4, sort manually using if statements


        // Count the number of swaps we perform when we do an insertion sort on a range
    }

    private static void smallSort(int a, int b, int c) {

    }
}