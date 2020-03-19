package tech.mlsql.common.utils.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

 /**
 * An {@link ExecutorService} that returns {@link ListenableFuture} instances. To create an instance
 * from an existing {@link ExecutorService}, call
 * {@link MoreExecutors#listeningDecorator(ExecutorService)}.
 *
 * @author Chris Povirk
 * @since 10.0
 */
public interface ListeningExecutorService extends ExecutorService {
    /**
     * @return a {@code ListenableFuture} representing pending completion of the task
     * @throws RejectedExecutionException {@inheritDoc}
     */
    @Override
    <T> ListenableFuture<T> submit(Callable<T> task);

    /**
     * @return a {@code ListenableFuture} representing pending completion of the task
     * @throws RejectedExecutionException {@inheritDoc}
     */
    @Override
    ListenableFuture<?> submit(Runnable task);

    /**
     * @return a {@code ListenableFuture} representing pending completion of the task
     * @throws RejectedExecutionException {@inheritDoc}
     */
    @Override
    <T> ListenableFuture<T> submit(Runnable task, T result);

    /**
     * {@inheritDoc}
     *
     * <p>All elements in the returned list must be {@link ListenableFuture} instances. The easiest
     * way to obtain a {@code List<ListenableFuture<T>>} from this method is an unchecked (but safe)
     * cast:<pre>
     *   {@code @SuppressWarnings("unchecked") // guaranteed by invokeAll contract}
     *   {@code List<ListenableFuture<T>> futures = (List) executor.invokeAll(tasks);}
     * </pre>
     *
     * @return A list of {@code ListenableFuture} instances representing the tasks, in the same
     *         sequential order as produced by the iterator for the given task list, each of which has
     *         completed.
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException if any task is null
     */
    @Override
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException;

    /**
     * {@inheritDoc}
     *
     * <p>All elements in the returned list must be {@link ListenableFuture} instances. The easiest
     * way to obtain a {@code List<ListenableFuture<T>>} from this method is an unchecked (but safe)
     * cast:<pre>
     *   {@code @SuppressWarnings("unchecked") // guaranteed by invokeAll contract}
     *   {@code List<ListenableFuture<T>> futures = (List) executor.invokeAll(tasks, timeout, unit);}
     * </pre>
     *
     * @return a list of {@code ListenableFuture} instances representing the tasks, in the same
     *         sequential order as produced by the iterator for the given task list. If the operation
     *         did not time out, each task will have completed. If it did time out, some of these
     *         tasks will not have completed.
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException if any task is null
     */
    @Override
    <T> List<Future<T>> invokeAll(
            Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException;
}
