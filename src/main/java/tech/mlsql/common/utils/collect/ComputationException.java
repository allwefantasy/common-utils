package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;

/**
 * Wraps an exception that occurred during a computation.
 *
 * @author Bob Lee
 * @since 2.0 (imported from Google Collections Library)
 */
public class ComputationException extends RuntimeException {
    /**
     * Creates a new instance with the given cause.
     */
    public ComputationException(@Nullable Throwable cause) {
        super(cause);
    }
    private static final long serialVersionUID = 0;
}
