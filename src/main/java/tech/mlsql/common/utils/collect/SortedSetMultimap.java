package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A {@code SetMultimap} whose set of values for a given key are kept sorted;
 * that is, they comprise a {@link SortedSet}. It cannot hold duplicate
 * key-value pairs; adding a key-value pair that's already in the multimap has
 * no effect. This interface does not specify the ordering of the multimap's
 * keys. See the {@link Multimap} documentation for information common to all
 * multimaps.
 *
 * <p>The {@link #get}, {@link #removeAll}, and {@link #replaceValues} methods
 * each return a {@link SortedSet} of values, while {@link Multimap#entries()}
 * returns a {@link Set} of map entries. Though the method signature doesn't say
 * so explicitly, the map returned by {@link #asMap} has {@code SortedSet}
 * values.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/NewCollectionTypesExplained#Multimap">
 * {@code Multimap}</a>.
 *
 * @author Jared Levy
 * @since 2.0 (imported from Google Collections Library)
 */
public interface SortedSetMultimap<K, V> extends SetMultimap<K, V> {
    // Following Javadoc copied from Multimap.

    /**
     * Returns a collection view of all values associated with a key. If no
     * mappings in the multimap have the provided key, an empty collection is
     * returned.
     *
     * <p>Changes to the returned collection will update the underlying multimap,
     * and vice versa.
     *
     * <p>Because a {@code SortedSetMultimap} has unique sorted values for a given
     * key, this method returns a {@link SortedSet}, instead of the
     * {@link java.util.Collection} specified in the {@link Multimap} interface.
     */
    @Override
    SortedSet<V> get(@Nullable K key);

    /**
     * Removes all values associated with a given key.
     *
     * <p>Because a {@code SortedSetMultimap} has unique sorted values for a given
     * key, this method returns a {@link SortedSet}, instead of the
     * {@link java.util.Collection} specified in the {@link Multimap} interface.
     */
    @Override
    SortedSet<V> removeAll(@Nullable Object key);

    /**
     * Stores a collection of values with the same key, replacing any existing
     * values for that key.
     *
     * <p>Because a {@code SortedSetMultimap} has unique sorted values for a given
     * key, this method returns a {@link SortedSet}, instead of the
     * {@link java.util.Collection} specified in the {@link Multimap} interface.
     *
     * <p>Any duplicates in {@code values} will be stored in the multimap once.
     */
    @Override
    SortedSet<V> replaceValues(K key, Iterable<? extends V> values);

    /**
     * Returns a map view that associates each key with the corresponding values
     * in the multimap. Changes to the returned map, such as element removal, will
     * update the underlying multimap. The map does not support {@code setValue()}
     * on its entries, {@code put}, or {@code putAll}.
     *
     * <p>When passed a key that is present in the map, {@code
     * asMap().get(Object)} has the same behavior as {@link #get}, returning a
     * live collection. When passed a key that is not present, however, {@code
     * asMap().get(Object)} returns {@code null} instead of an empty collection.
     *
     * <p><b>Note:</b> The returned map's values are guaranteed to be of type
     * {@link SortedSet}. To obtain this map with the more specific generic type
     * {@code Map<K, SortedSet<V>>}, call
     * {@link Multimaps#asMap(SortedSetMultimap)} instead.
     */
    @Override Map<K, Collection<V>> asMap();

    /**
     * Returns the comparator that orders the multimap values, with {@code null}
     * indicating that natural ordering is used.
     */
    Comparator<? super V> valueComparator();
}
