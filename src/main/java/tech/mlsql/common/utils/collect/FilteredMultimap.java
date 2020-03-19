package tech.mlsql.common.utils.collect;

import tech.mlsql.common.utils.base.Predicate;

import java.util.Map.Entry;

/**
 * An interface for all filtered multimap types.
 *
 * @author Louis Wasserman
 */
interface FilteredMultimap<K, V> extends Multimap<K, V> {
    Multimap<K, V> unfiltered();

    Predicate<? super Entry<K, V>> entryPredicate();
}
