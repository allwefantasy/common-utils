package tech.mlsql.common.utils.collect;


import java.util.SortedMap;

/**
 * An object representing the differences between two sorted maps.
 *
 * @author Louis Wasserman
 * @since 8.0
 */
public interface SortedMapDifference<K, V> extends MapDifference<K, V> {

    @Override
    SortedMap<K, V> entriesOnlyOnLeft();

    @Override
    SortedMap<K, V> entriesOnlyOnRight();

    @Override
    SortedMap<K, V> entriesInCommon();

    @Override
    SortedMap<K, ValueDifference<V>> entriesDiffering();
}
