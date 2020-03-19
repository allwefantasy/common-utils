package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.annotations.Beta;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A {@link ScheduledExecutorService} that returns {@link ListenableFuture}
 * instances from its {@code ExecutorService} methods. To create an instance
 * from an existing {@link ScheduledExecutorService}, call
 * {@link MoreExecutors#listeningDecorator(ScheduledExecutorService)}.
 *
 * @author Chris Povirk
 * @since 10.0
 */
@Beta
public interface ListeningScheduledExecutorService
        extends ScheduledExecutorService, ListeningExecutorService {

    /** @since 15.0 (previously returned ScheduledFuture) */
    @Override
    ListenableScheduledFuture<?> schedule(
            Runnable command, long delay, TimeUnit unit);

    /** @since 15.0 (previously returned ScheduledFuture) */
    @Override
    <V> ListenableScheduledFuture<V> schedule(
            Callable<V> callable, long delay, TimeUnit unit);

    /** @since 15.0 (previously returned ScheduledFuture) */
    @Override
    ListenableScheduledFuture<?> scheduleAtFixedRate(
            Runnable command, long initialDelay, long period, TimeUnit unit);

    /** @since 15.0 (previously returned ScheduledFuture) */
    @Override
    ListenableScheduledFuture<?> scheduleWithFixedDelay(
            Runnable command, long initialDelay, long delay, TimeUnit unit);
}

