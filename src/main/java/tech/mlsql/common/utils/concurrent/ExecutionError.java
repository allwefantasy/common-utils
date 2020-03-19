package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.annotations.GwtCompatible;

import javax.annotation.Nullable;

/**
 * {@link Error} variant of {@link java.util.concurrent.ExecutionException}. As
 * with {@code ExecutionException}, the error's {@linkplain #getCause() cause}
 * comes from a failed task, possibly run in another thread. That cause should
 * itself be an {@code Error}; if not, use {@code ExecutionException} or {@link
 * UncheckedExecutionException}. This allows the client code to continue to
 * distinguish between exceptions and errors, even when they come from other
 * threads.
 *
 * @author Chris Povirk
 * @since 10.0
 */
@GwtCompatible
public class ExecutionError extends Error {
    /**
     * Creates a new instance with {@code null} as its detail message.
     */
    protected ExecutionError() {}

    /**
     * Creates a new instance with the given detail message.
     */
    protected ExecutionError(@Nullable String message) {
        super(message);
    }

    /**
     * Creates a new instance with the given detail message and cause.
     */
    public ExecutionError(@Nullable String message, @Nullable Error cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance with the given cause.
     */
    public ExecutionError(@Nullable Error cause) {
        super(cause);
    }

    private static final long serialVersionUID = 0;
}
