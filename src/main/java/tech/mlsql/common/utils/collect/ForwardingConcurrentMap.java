package tech.mlsql.common.utils.collect;

import java.util.concurrent.ConcurrentMap;

/**
 * A concurrent map which forwards all its method calls to another concurrent
 * map. Subclasses should override one or more methods to modify the behavior of
 * the backing map as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * @author Charles Fry
 * @since 2.0 (imported from Google Collections Library)
 */
public abstract class ForwardingConcurrentMap<K, V> extends ForwardingMap<K, V>
        implements ConcurrentMap<K, V> {

    /** Constructor for use by subclasses. */
    protected ForwardingConcurrentMap() {}

    @Override protected abstract ConcurrentMap<K, V> delegate();

    @Override
    public V putIfAbsent(K key, V value) {
        return delegate().putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate().remove(key, value);
    }

    @Override
    public V replace(K key, V value) {
        return delegate().replace(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return delegate().replace(key, oldValue, newValue);
    }

}
