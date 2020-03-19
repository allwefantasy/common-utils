package tech.mlsql.common.utils.cache;

import tech.mlsql.common.utils.annotations.Beta;
import tech.mlsql.common.utils.annotations.GwtCompatible;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * The reason why a cached entry was removed.
 *
 * @author Charles Fry
 * @since 10.0
 */
@Beta
@GwtCompatible
public enum RemovalCause {
    /**
     * The entry was manually removed by the user. This can result from the user invoking
     * {@link Cache#invalidate}, {@link Cache#invalidateAll(Iterable)}, {@link Cache#invalidateAll()},
     * {@link Map#remove}, {@link ConcurrentMap#remove}, or {@link Iterator#remove}.
     */
    EXPLICIT {
        @Override
        boolean wasEvicted() {
            return false;
        }
    },

    /**
     * The entry itself was not actually removed, but its value was replaced by the user. This can
     * result from the user invoking {@link Cache#put}, {@link LoadingCache#refresh}, {@link Map#put},
     * {@link Map#putAll}, {@link ConcurrentMap#replace(Object, Object)}, or
     * {@link ConcurrentMap#replace(Object, Object, Object)}.
     */
    REPLACED {
        @Override
        boolean wasEvicted() {
            return false;
        }
    },

    /**
     * The entry was removed automatically because its key or value was garbage-collected. This
     * can occur when using {@link CacheBuilder#weakKeys}, {@link CacheBuilder#weakValues}, or
     * {@link CacheBuilder#softValues}.
     */
    COLLECTED {
        @Override
        boolean wasEvicted() {
            return true;
        }
    },

    /**
     * The entry's expiration timestamp has passed. This can occur when using
     * {@link CacheBuilder#expireAfterWrite} or {@link CacheBuilder#expireAfterAccess}.
     */
    EXPIRED {
        @Override
        boolean wasEvicted() {
            return true;
        }
    },

    /**
     * The entry was evicted due to size constraints. This can occur when using
     * {@link CacheBuilder#maximumSize} or {@link CacheBuilder#maximumWeight}.
     */
    SIZE {
        @Override
        boolean wasEvicted() {
            return true;
        }
    };

    /**
     * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
     * {@link #EXPLICIT} nor {@link #REPLACED}).
     */
    abstract boolean wasEvicted();
}
