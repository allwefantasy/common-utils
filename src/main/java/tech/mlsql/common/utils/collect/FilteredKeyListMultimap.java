package tech.mlsql.common.utils.collect;


import tech.mlsql.common.utils.base.Predicate;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation of {@link Multimaps#filterKeys(ListMultimap, Predicate)}.
 *
 * @author Louis Wasserman
 */
final class FilteredKeyListMultimap<K, V> extends FilteredKeyMultimap<K, V>
        implements ListMultimap<K, V> {
    FilteredKeyListMultimap(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        super(unfiltered, keyPredicate);
    }

    @Override
    public ListMultimap<K, V> unfiltered() {
        return (ListMultimap<K, V>) super.unfiltered();
    }

    @Override
    public List<V> get(K key) {
        return (List<V>) super.get(key);
    }

    @Override
    public List<V> removeAll(@Nullable Object key) {
        return (List<V>) super.removeAll(key);
    }

    @Override
    public List<V> replaceValues(K key, Iterable<? extends V> values) {
        return (List<V>) super.replaceValues(key, values);
    }
}
