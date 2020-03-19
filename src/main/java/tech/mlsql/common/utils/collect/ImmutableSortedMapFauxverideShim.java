package tech.mlsql.common.utils.collect;

/**
 * "Overrides" the {@link ImmutableMap} static methods that lack
 * {@link ImmutableSortedMap} equivalents with deprecated, exception-throwing
 * versions. See {@link ImmutableSortedSetFauxverideShim} for details.
 *
 * @author Chris Povirk
 */
abstract class ImmutableSortedMapFauxverideShim<K, V>
        extends ImmutableMap<K, V> {
    /**
     * Not supported. Use {@link ImmutableSortedMap#naturalOrder}, which offers
     * better type-safety, instead. This method exists only to hide
     * {@link ImmutableMap#builder} from consumers of {@code ImmutableSortedMap}.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Use {@link ImmutableSortedMap#naturalOrder}, which offers
     *     better type-safety.
     */
    @Deprecated public static <K, V> ImmutableSortedMap.Builder<K, V> builder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported. <b>You are attempting to create a map that may contain a
     * non-{@code Comparable} key.</b> Proper calls will resolve to the version in
     * {@code ImmutableSortedMap}, not this dummy version.
     *
     * @throws UnsupportedOperationException always
     * @deprecated <b>Pass a key of type {@code Comparable} to use {@link
     *     ImmutableSortedMap#of(Comparable, Object)}.</b>
     */
    @Deprecated public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported. <b>You are attempting to create a map that may contain
     * non-{@code Comparable} keys.</b> Proper calls will resolve to the version
     * in {@code ImmutableSortedMap}, not this dummy version.
     *
     * @throws UnsupportedOperationException always
     * @deprecated <b>Pass keys of type {@code Comparable} to use {@link
     *     ImmutableSortedMap#of(Comparable, Object, Comparable, Object)}.</b>
     */
    @Deprecated public static <K, V> ImmutableSortedMap<K, V> of(
            K k1, V v1, K k2, V v2) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported. <b>You are attempting to create a map that may contain
     * non-{@code Comparable} keys.</b> Proper calls to will resolve to the
     * version in {@code ImmutableSortedMap}, not this dummy version.
     *
     * @throws UnsupportedOperationException always
     * @deprecated <b>Pass keys of type {@code Comparable} to use {@link
     *     ImmutableSortedMap#of(Comparable, Object, Comparable, Object,
     *     Comparable, Object)}.</b>
     */
    @Deprecated public static <K, V> ImmutableSortedMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported. <b>You are attempting to create a map that may contain
     * non-{@code Comparable} keys.</b> Proper calls will resolve to the version
     * in {@code ImmutableSortedMap}, not this dummy version.
     *
     * @throws UnsupportedOperationException always
     * @deprecated <b>Pass keys of type {@code Comparable} to use {@link
     *     ImmutableSortedMap#of(Comparable, Object, Comparable, Object,
     *     Comparable, Object, Comparable, Object)}.</b>
     */
    @Deprecated public static <K, V> ImmutableSortedMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported. <b>You are attempting to create a map that may contain
     * non-{@code Comparable} keys.</b> Proper calls will resolve to the version
     * in {@code ImmutableSortedMap}, not this dummy version.
     *
     * @throws UnsupportedOperationException always
     * @deprecated <b>Pass keys of type {@code Comparable} to use {@link
     *     ImmutableSortedMap#of(Comparable, Object, Comparable, Object,
     *     Comparable, Object, Comparable, Object, Comparable, Object)}.</b>
     */
    @Deprecated public static <K, V> ImmutableSortedMap<K, V> of(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        throw new UnsupportedOperationException();
    }

    // No copyOf() fauxveride; see ImmutableSortedSetFauxverideShim.
}
