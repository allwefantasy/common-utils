package tech.mlsql.common.utils.concurrent;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A callback for accepting the results of a {@link java.util.concurrent.Future}
 * computation asynchronously.
 *
 * <p>To attach to a {@link ListenableFuture} use {@link Futures#addCallback}.
 *
 * @author Anthony Zana
 * @since 10.0
 */
public interface FutureCallback<V> {
    /**
     * Invoked with the result of the {@code Future} computation when it is
     * successful.
     */
    void onSuccess(@Nullable V result);

    /**
     * Invoked when a {@code Future} computation fails or is canceled.
     *
     * <p>If the future's {@link Future#get() get} method throws an {@link
     * ExecutionException}, then the cause is passed to this method. Any other
     * thrown object is passed unaltered.
     */
    void onFailure(Throwable t);
}
