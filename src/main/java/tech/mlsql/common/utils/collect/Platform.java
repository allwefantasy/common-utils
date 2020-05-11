package tech.mlsql.common.utils.collect;

import tech.mlsql.common.utils.base.Function;
import tech.mlsql.common.utils.base.Predicate;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Methods factored out so that they can be emulated differently in GWT.
 *
 * @author Hayward Chan
 */

final class Platform {

    static <K, V> Map<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return CompactHashMap.createWithExpectedSize(expectedSize);
    }

    static <E> Set<E> newHashSetWithExpectedSize(int expectedSize) {
        return Sets.newHashSetWithExpectedSize(expectedSize);
    }

    static <E> Set<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
        return Sets.newLinkedHashSetWithExpectedSize(expectedSize);
    }

    static <K, V> Map<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
        return Maps.newLinkedHashMapWithExpectedSize(expectedSize);
    }

    /**
     * Returns a new array of the given length with the same type as a reference
     * array.
     *
     * @param reference any array of the desired type
     * @param length the length of the new array
     */
    static <T> T[] newArray(T[] reference, int length) {
        Class<?> type = reference.getClass().getComponentType();

        // the cast is safe because
        // result.getClass() == reference.getClass().getComponentType()
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(type, length);
        return result;
    }

    static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Collections.newSetFromMap(map);
    }

    /**
     * Configures the given map maker to use weak keys, if possible; does nothing
     * otherwise (i.e., in GWT). This is sometimes acceptable, when only
     * server-side code could generate enough volume that reclamation becomes
     * important.
     */
    static MapMaker tryWeakKeys(MapMaker mapMaker) {
        return mapMaker.weakKeys();
    }

    static <K, V1, V2> SortedMap<K, V2> mapsTransformEntriesSortedMap(
            SortedMap<K, V1> fromMap,
            Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
        return (fromMap instanceof NavigableMap)
                ? Maps.transformEntries((NavigableMap<K, V1>) fromMap, transformer)
                : Maps.transformEntriesIgnoreNavigable(fromMap, transformer);
    }

    static <K, V> SortedMap<K, V> mapsAsMapSortedSet(SortedSet<K> set,
                                                     Function<? super K, V> function) {
        return (set instanceof NavigableSet)
                ? Maps.asMap((NavigableSet<K>) set, function)
                : Maps.asMapSortedIgnoreNavigable(set, function);
    }

    static <E> SortedSet<E> setsFilterSortedSet(SortedSet<E> set,
                                                Predicate<? super E> predicate) {
        return (set instanceof NavigableSet)
                ? Sets.filter((NavigableSet<E>) set, predicate)
                : Sets.filterSortedIgnoreNavigable(set, predicate);
    }

    static <K, V> SortedMap<K, V> mapsFilterSortedMap(SortedMap<K, V> map,
                                                      Predicate<? super Map.Entry<K, V>> predicate) {
        return (map instanceof NavigableMap)
                ? Maps.filterEntries((NavigableMap<K, V>) map, predicate)
                : Maps.filterSortedIgnoreNavigable(map, predicate);
    }

    private Platform() {}
}

