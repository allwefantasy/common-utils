package tech.mlsql.common.utils.collect;

import com.google.common.primitives.Ints;

import javax.annotation.Nullable;

/**
 * Static methods for implementing hash-based collections.
 *
 * @author Kevin Bourrillion
 * @author Jesse Wilson
 * @author Austin Appleby
 */
final class Hashing {
    private Hashing() {}

    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;

    /*
     * This method was rewritten in Java from an intermediate step of the Murmur hash function in
     * http://code.google.com/p/smhasher/source/browse/trunk/MurmurHash3.cpp, which contained the
     * following header:
     *
     * MurmurHash3 was written by Austin Appleby, and is placed in the public domain. The author
     * hereby disclaims copyright to this source code.
     */
    static int smear(int hashCode) {
        return C2 * Integer.rotateLeft(hashCode * C1, 15);
    }

    static int smearedHash(@Nullable Object o) {
        return smear((o == null) ? 0 : o.hashCode());
    }

    private static int MAX_TABLE_SIZE = Ints.MAX_POWER_OF_TWO;

    static int closedTableSize(int expectedEntries, double loadFactor) {
        // Get the recommended table size.
        // Round down to the nearest power of 2.
        expectedEntries = Math.max(expectedEntries, 2);
        int tableSize = Integer.highestOneBit(expectedEntries);
        // Check to make sure that we will not exceed the maximum load factor.
        if (expectedEntries > (int) (loadFactor * tableSize)) {
            tableSize <<= 1;
            return (tableSize > 0) ? tableSize : MAX_TABLE_SIZE;
        }
        return tableSize;
    }

    static boolean needsResizing(int size, int tableSize, double loadFactor) {
        return size > loadFactor * tableSize && tableSize < MAX_TABLE_SIZE;
    }
}
