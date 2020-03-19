package tech.mlsql.common.utils.cache;

import tech.mlsql.common.utils.annotations.GwtCompatible;

/**
 * Abstract interface for objects that can concurrently add longs.
 *
 * @author Louis Wasserman
 */
@GwtCompatible
interface LongAddable {
    void increment();

    void add(long x);

    long sum();
}

