package tech.mlsql.common.utils.collect;
import tech.mlsql.common.utils.base.Equivalence;
import tech.mlsql.common.utils.base.Function;
import tech.mlsql.common.utils.base.Objects;
import tech.mlsql.common.utils.collect.MapMaker.RemovalListener;
import tech.mlsql.common.utils.collect.MapMaker.RemovalNotification;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

abstract class GenericMapMaker<K0, V0> {
    enum NullListener implements RemovalListener<Object, Object> {
        INSTANCE;

        @Override
        public void onRemoval(RemovalNotification<Object, Object> notification) {}
    }

    // Set by MapMaker, but sits in this class to preserve the type relationship
    RemovalListener<K0, V0> removalListener;

    // No subclasses but our own
    GenericMapMaker() {}

    /**
     * See {@link MapMaker#keyEquivalence}.
     */
    abstract GenericMapMaker<K0, V0> keyEquivalence(Equivalence<Object> equivalence);

    /**
     * See {@link MapMaker#initialCapacity}.
     */
    public abstract GenericMapMaker<K0, V0> initialCapacity(int initialCapacity);

    /**
     * See {@link MapMaker#maximumSize}.
     */
    abstract GenericMapMaker<K0, V0> maximumSize(int maximumSize);

    /**
     * See {@link MapMaker#concurrencyLevel}.
     */
    public abstract GenericMapMaker<K0, V0> concurrencyLevel(int concurrencyLevel);

    /**
     * See {@link MapMaker#weakKeys}.
     */
    public abstract GenericMapMaker<K0, V0> weakKeys();

    /**
     * See {@link MapMaker#weakValues}.
     */
    public abstract GenericMapMaker<K0, V0> weakValues();

    /**
     * See {@link MapMaker#softValues}.
     *
     * @deprecated Caching functionality in {@code MapMaker} has been moved to {@link
     *     com.google.common.cache.CacheBuilder}, with {@link #softValues} being replaced by {@link
     *     com.google.common.cache.CacheBuilder#softValues}. Note that {@code CacheBuilder} is simply
     *     an enhanced API for an implementation which was branched from {@code MapMaker}. <b>This
     *     method is scheduled for deletion in August 2014.</b>
     */
    @Deprecated
    public abstract GenericMapMaker<K0, V0> softValues();

    /**
     * See {@link MapMaker#expireAfterWrite}.
     */
    abstract GenericMapMaker<K0, V0> expireAfterWrite(long duration, TimeUnit unit);

    /**
     * See {@link MapMaker#expireAfterAccess}.
     */
    abstract GenericMapMaker<K0, V0> expireAfterAccess(long duration, TimeUnit unit);

    /*
     * Note that MapMaker's removalListener() is not here, because once you're interacting with a
     * GenericMapMaker you've already called that, and shouldn't be calling it again.
     */

    @SuppressWarnings("unchecked") // safe covariant cast
    <K extends K0, V extends V0> RemovalListener<K, V> getRemovalListener() {
        return (RemovalListener<K, V>) Objects.firstNonNull(removalListener, NullListener.INSTANCE);
    }

    /**
     * See {@link MapMaker#makeMap}.
     */
    public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();

    /**
     * See {@link MapMaker#makeCustomMap}.
     */
    abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();

    /**
     * See {@link MapMaker#makeComputingMap}.
     */
    @Deprecated
    abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(
            Function<? super K, ? extends V> computingFunction);
}
