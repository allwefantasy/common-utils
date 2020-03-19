package tech.mlsql.common.utils.cache;

import tech.mlsql.common.utils.annotations.Beta;
import tech.mlsql.common.utils.annotations.GwtCompatible;

/**
 * An object that can receive a notification when an entry is removed from a cache. The removal
 * resulting in notification could have occured to an entry being manually removed or replaced, or
 * due to eviction resulting from timed expiration, exceeding a maximum size, or garbage
 * collection.
 *
 * <p>An instance may be called concurrently by multiple threads to process different entries.
 * Implementations of this interface should avoid performing blocking calls or synchronizing on
 * shared resources.
 *
 * @param <K> the most general type of keys this listener can listen for; for
 *     example {@code Object} if any key is acceptable
 * @param <V> the most general type of values this listener can listen for; for
 *     example {@code Object} if any key is acceptable
 * @author Charles Fry
 * @since 10.0
 */
@Beta
@GwtCompatible
public interface RemovalListener<K, V> {
    /**
     * Notifies the listener that a removal occurred at some point in the past.
     */
    // Technically should accept RemovalNotification<? extends K, ? extends V>, but because
    // RemovalNotification is guaranteed covariant, let's make users' lives simpler.
    void onRemoval(RemovalNotification<K, V> notification);
}
