package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.annotations.Beta;

import javax.annotation.Nullable;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;

/**
 * Abstract {@link ListeningExecutorService} implementation that creates
 * {@link ListenableFutureTask} instances for each {@link Runnable} and {@link Callable} submitted
 * to it. These tasks are run with the abstract {@link #execute execute(Runnable)} method.
 *
 * <p>In addition to {@link #execute}, subclasses must implement all methods related to shutdown and
 * termination.
 *
 * @author Chris Povirk
 * @since 14.0
 */
@Beta
public abstract class AbstractListeningExecutorService
        extends AbstractExecutorService implements ListeningExecutorService {

    @Override protected final <T> ListenableFutureTask<T> newTaskFor(Runnable runnable, T value) {
        return ListenableFutureTask.create(runnable, value);
    }

    @Override protected final <T> ListenableFutureTask<T> newTaskFor(Callable<T> callable) {
        return ListenableFutureTask.create(callable);
    }

    @Override public ListenableFuture<?> submit(Runnable task) {
        return (ListenableFuture<?>) super.submit(task);
    }

    @Override public <T> ListenableFuture<T> submit(Runnable task, @Nullable T result) {
        return (ListenableFuture<T>) super.submit(task, result);
    }

    @Override public <T> ListenableFuture<T> submit(Callable<T> task) {
        return (ListenableFuture<T>) super.submit(task);
    }
}

