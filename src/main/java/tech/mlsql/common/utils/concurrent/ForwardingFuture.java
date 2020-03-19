package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.base.Preconditions;
import tech.mlsql.common.utils.collect.ForwardingObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A {@link Future} which forwards all its method calls to another future.
 * Subclasses should override one or more methods to modify the behavior of
 * the backing future as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p>Most subclasses can just use {@link SimpleForwardingFuture}.
 *
 * @author Sven Mawson
 * @since 1.0
 */
public abstract class ForwardingFuture<V> extends ForwardingObject
        implements Future<V> {

    /** Constructor for use by subclasses. */
    protected ForwardingFuture() {}

    @Override protected abstract Future<V> delegate();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate().cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate().isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate().isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate().get();
    }

    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return delegate().get(timeout, unit);
    }

    /*
     * TODO(cpovirk): Use standard Javadoc form for SimpleForwarding* class and
     * constructor
     */
    /**
     * A simplified version of {@link ForwardingFuture} where subclasses
     * can pass in an already constructed {@link Future} as the delegate.
     *
     * @since 9.0
     */
    public abstract static class SimpleForwardingFuture<V>
            extends ForwardingFuture<V> {
        private final Future<V> delegate;

        protected SimpleForwardingFuture(Future<V> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }

        @Override
        protected final Future<V> delegate() {
            return delegate;
        }

    }
}
