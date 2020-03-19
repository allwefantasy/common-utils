package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.base.Preconditions;

import java.util.concurrent.Executor;

/**
 * A {@link ListenableFuture} which forwards all its method calls to another
 * future. Subclasses should override one or more methods to modify the behavior
 * of the backing future as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p>Most subclasses can just use {@link SimpleForwardingListenableFuture}.
 *
 * @param <V> The result type returned by this Future's {@code get} method
 *
 * @author Shardul Deo
 * @since 4.0
 */
public abstract class ForwardingListenableFuture<V> extends ForwardingFuture<V>
        implements ListenableFuture<V> {

    /** Constructor for use by subclasses. */
    protected ForwardingListenableFuture() {}

    @Override
    protected abstract ListenableFuture<V> delegate();

    @Override
    public void addListener(Runnable listener, Executor exec) {
        delegate().addListener(listener, exec);
    }

    /*
     * TODO(cpovirk): Use standard Javadoc form for SimpleForwarding* class and
     * constructor
     */
    /**
     * A simplified version of {@link ForwardingListenableFuture} where subclasses
     * can pass in an already constructed {@link ListenableFuture}
     * as the delegate.
     *
     * @since 9.0
     */
    public abstract static class SimpleForwardingListenableFuture<V>
            extends ForwardingListenableFuture<V> {
        private final ListenableFuture<V> delegate;

        protected SimpleForwardingListenableFuture(ListenableFuture<V> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }

        @Override
        protected final ListenableFuture<V> delegate() {
            return delegate;
        }
    }
}

