package tech.mlsql.common.utils.collect;


import javax.annotation.Nullable;
import java.util.Comparator;

import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;

/**
 * An empty immutable sorted map.
 *
 * @author Louis Wasserman
 */
@SuppressWarnings("serial") // uses writeReplace, not default serialization
final class EmptyImmutableSortedMap<K, V> extends ImmutableSortedMap<K, V> {
    private final transient ImmutableSortedSet<K> keySet;

    EmptyImmutableSortedMap(Comparator<? super K> comparator) {
        this.keySet = ImmutableSortedSet.emptySet(comparator);
    }

    EmptyImmutableSortedMap(
            Comparator<? super K> comparator, ImmutableSortedMap<K, V> descendingMap) {
        super(descendingMap);
        this.keySet = ImmutableSortedSet.emptySet(comparator);
    }

    @Override
    public V get(@Nullable Object key) {
        return null;
    }

    @Override
    public ImmutableSortedSet<K> keySet() {
        return keySet;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ImmutableCollection<V> values() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "{}";
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {
        return ImmutableSet.of();
    }

    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        throw new AssertionError("should never be called");
    }

    @Override
    public ImmutableSetMultimap<K, V> asMultimap() {
        return ImmutableSetMultimap.of();
    }

    @Override
    public ImmutableSortedMap<K, V> headMap(K toKey, boolean inclusive) {
        checkNotNull(toKey);
        return this;
    }

    @Override
    public ImmutableSortedMap<K, V> tailMap(K fromKey, boolean inclusive) {
        checkNotNull(fromKey);
        return this;
    }

    @Override
    ImmutableSortedMap<K, V> createDescendingMap() {
        return new EmptyImmutableSortedMap<K, V>(Ordering.from(comparator()).reverse(), this);
    }
}
