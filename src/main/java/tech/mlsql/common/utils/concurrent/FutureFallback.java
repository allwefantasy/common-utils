package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.annotations.Beta;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Provides a backup {@code Future} to replace an earlier failed {@code Future}.
 * An implementation of this interface can be applied to an input {@code Future}
 * with {@link Futures#withFallback}.
 *
 * @param <V> the result type of the provided backup {@code Future}
 *
 * @author Bruno Diniz
 * @since 14.0
 */
@Beta
public interface FutureFallback<V> {
    /**
     * Returns a {@code Future} to be used in place of the {@code Future} that
     * failed with the given exception. The exception is provided so that the
     * {@code Fallback} implementation can conditionally determine whether to
     * propagate the exception or to attempt to recover.
     *
     * @param t the exception that made the future fail. If the future's {@link
     *     Future#get() get} method throws an {@link ExecutionException}, then the
     *     cause is passed to this method. Any other thrown object is passed
     *     unaltered.
     */
    ListenableFuture<V> create(Throwable t) throws Exception;
}

