package tech.mlsql.common.utils.collect;


/**
 * A supertype for filtered {@link SetMultimap} implementations.
 *
 * @author Louis Wasserman
 */
interface FilteredSetMultimap<K, V> extends FilteredMultimap<K, V>, SetMultimap<K, V> {
    @Override
    SetMultimap<K, V> unfiltered();
}
