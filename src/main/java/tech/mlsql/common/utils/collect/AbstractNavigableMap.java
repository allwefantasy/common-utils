package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Skeletal implementation of {@link NavigableMap}.
 *
 * @author Louis Wasserman
 */
abstract class AbstractNavigableMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V> {

    @Override
    @Nullable
    public abstract V get(@Nullable Object key);

    @Override
    @Nullable
    public Entry<K, V> firstEntry() {
        return Iterators.getNext(entryIterator(), null);
    }

    @Override
    @Nullable
    public Entry<K, V> lastEntry() {
        return Iterators.getNext(descendingEntryIterator(), null);
    }

    @Override
    @Nullable
    public Entry<K, V> pollFirstEntry() {
        return Iterators.pollNext(entryIterator());
    }

    @Override
    @Nullable
    public Entry<K, V> pollLastEntry() {
        return Iterators.pollNext(descendingEntryIterator());
    }

    @Override
    public K firstKey() {
        Entry<K, V> entry = firstEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    @Override
    public K lastKey() {
        Entry<K, V> entry = lastEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    @Override
    @Nullable
    public Entry<K, V> lowerEntry(K key) {
        return headMap(key, false).lastEntry();
    }

    @Override
    @Nullable
    public Entry<K, V> floorEntry(K key) {
        return headMap(key, true).lastEntry();
    }

    @Override
    @Nullable
    public Entry<K, V> ceilingEntry(K key) {
        return tailMap(key, true).firstEntry();
    }

    @Override
    @Nullable
    public Entry<K, V> higherEntry(K key) {
        return tailMap(key, false).firstEntry();
    }

    @Override
    public K lowerKey(K key) {
        return Maps.keyOrNull(lowerEntry(key));
    }

    @Override
    public K floorKey(K key) {
        return Maps.keyOrNull(floorEntry(key));
    }

    @Override
    public K ceilingKey(K key) {
        return Maps.keyOrNull(ceilingEntry(key));
    }

    @Override
    public K higherKey(K key) {
        return Maps.keyOrNull(higherEntry(key));
    }

    abstract Iterator<Entry<K, V>> entryIterator();

    abstract Iterator<Entry<K, V>> descendingEntryIterator();

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return headMap(toKey, false);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return new Maps.NavigableKeySet<K, V>(this);
    }

    @Override
    public Set<K> keySet() {
        return navigableKeySet();
    }

    @Override
    public abstract int size();

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new Maps.EntrySet<K, V>() {
            @Override
            Map<K, V> map() {
                return AbstractNavigableMap.this;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return entryIterator();
            }
        };
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return new DescendingMap();
    }

    private final class DescendingMap extends Maps.DescendingMap<K, V> {
        @Override
        NavigableMap<K, V> forward() {
            return AbstractNavigableMap.this;
        }

        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return descendingEntryIterator();
        }
    }

}
