package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.annotations.Beta;

import java.util.concurrent.ScheduledFuture;

/**
 * Helper interface to implement both {@link ListenableFuture} and
 * {@link ScheduledFuture}.
 *
 * @author Anthony Zana
 *
 * @since 15.0
 */
@Beta
public interface ListenableScheduledFuture<V>
        extends ScheduledFuture<V>, ListenableFuture<V> {}
