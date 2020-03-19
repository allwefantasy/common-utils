package tech.mlsql.common.utils.collect;


import tech.mlsql.common.utils.base.Predicate;

import java.util.Map.Entry;
import java.util.Set;

/**
 * Implementation of {@link Multimaps#filterEntries(SetMultimap, Predicate)}.
 *
 * @author Louis Wasserman
 */

final class FilteredEntrySetMultimap<K, V> extends FilteredEntryMultimap<K, V>
        implements FilteredSetMultimap<K, V> {

    FilteredEntrySetMultimap(SetMultimap<K, V> unfiltered, Predicate<? super Entry<K, V>> predicate) {
        super(unfiltered, predicate);
    }

    @Override
    public SetMultimap<K, V> unfiltered() {
        return (SetMultimap<K, V>) unfiltered;
    }

    @Override
    public Set<V> get(K key) {
        return (Set<V>) super.get(key);
    }

    @Override
    public Set<V> removeAll(Object key) {
        return (Set<V>) super.removeAll(key);
    }

    @Override
    public Set<V> replaceValues(K key, Iterable<? extends V> values) {
        return (Set<V>) super.replaceValues(key, values);
    }

    @Override
    Set<Entry<K, V>> createEntries() {
        return Sets.filter(unfiltered().entries(), entryPredicate());
    }

    @Override
    public Set<Entry<K, V>> entries() {
        return (Set<Entry<K, V>>) super.entries();
    }
}
