package tech.mlsql.common.utils.concurrent;

import tech.mlsql.common.utils.annotations.GwtCompatible;

import javax.annotation.Nullable;

/**
 * Unchecked variant of {@link java.util.concurrent.ExecutionException}. As with
 * {@code ExecutionException}, the exception's {@linkplain #getCause() cause}
 * comes from a failed task, possibly run in another thread.
 *
 * <p>{@code UncheckedExecutionException} is intended as an alternative to
 * {@code ExecutionException} when the exception thrown by a task is an
 * unchecked exception. However, it may also wrap a checked exception in some
 * cases.
 *
 * <p>When wrapping an {@code Error} from another thread, prefer {@link
 * ExecutionError}. When wrapping a checked exception, prefer {@code
 * ExecutionException}.
 *
 * @author Charles Fry
 * @since 10.0
 */
@GwtCompatible
public class UncheckedExecutionException extends RuntimeException {
    /**
     * Creates a new instance with {@code null} as its detail message.
     */
    protected UncheckedExecutionException() {}

    /**
     * Creates a new instance with the given detail message.
     */
    protected UncheckedExecutionException(@Nullable String message) {
        super(message);
    }

    /**
     * Creates a new instance with the given detail message and cause.
     */
    public UncheckedExecutionException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance with the given cause.
     */
    public UncheckedExecutionException(@Nullable Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 0;
}

