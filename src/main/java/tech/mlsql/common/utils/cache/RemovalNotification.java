package tech.mlsql.common.utils.cache;

import tech.mlsql.common.utils.annotations.Beta;
import tech.mlsql.common.utils.annotations.GwtCompatible;
import tech.mlsql.common.utils.base.Objects;

import javax.annotation.Nullable;
import java.util.Map.Entry;

import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;

/**
 * A notification of the removal of a single entry. The key and/or value may be null if they were
 * already garbage collected.
 *
 * <p>Like other {@code Map.Entry} instances associated with {@code CacheBuilder}, this class holds
 * strong references to the key and value, regardless of the type of references the cache may be
 * using.
 *
 * @author Charles Fry
 * @since 10.0
 */
@Beta
@GwtCompatible
public final class RemovalNotification<K, V> implements Entry<K, V> {
    @Nullable private final K key;
    @Nullable private final V value;
    private final RemovalCause cause;

    RemovalNotification(@Nullable K key, @Nullable V value, RemovalCause cause) {
        this.key = key;
        this.value = value;
        this.cause = checkNotNull(cause);
    }

    /**
     * Returns the cause for which the entry was removed.
     */
    public RemovalCause getCause() {
        return cause;
    }

    /**
     * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
     * {@link RemovalCause#EXPLICIT} nor {@link RemovalCause#REPLACED}).
     */
    public boolean wasEvicted() {
        return cause.wasEvicted();
    }

    @Nullable @Override public K getKey() {
        return key;
    }

    @Nullable @Override public V getValue() {
        return value;
    }

    @Override public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean equals(@Nullable Object object) {
        if (object instanceof Entry) {
            Entry<?, ?> that = (Entry<?, ?>) object;
            return Objects.equal(this.getKey(), that.getKey())
                    && Objects.equal(this.getValue(), that.getValue());
        }
        return false;
    }

    @Override public int hashCode() {
        K k = getKey();
        V v = getValue();
        return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
    }

    /**
     * Returns a string representation of the form <code>{key}={value}</code>.
     */
    @Override public String toString() {
        return getKey() + "=" + getValue();
    }
    private static final long serialVersionUID = 0;
}
