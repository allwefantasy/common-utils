package tech.mlsql.common.utils.io;

import tech.mlsql.common.utils.annotations.Beta;
import tech.mlsql.common.utils.annotations.VisibleForTesting;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for working with {@link Closeable} objects.
 *
 * @author Michael Lancaster
 * @since 1.0
 */
@Beta
public final class Closeables {
    @VisibleForTesting static final Logger logger
            = Logger.getLogger(Closeables.class.getName());

    private Closeables() {}

    /**
     * Closes a {@link Closeable}, with control over whether an {@code IOException} may be thrown.
     * This is primarily useful in a finally block, where a thrown exception needs to be logged but
     * not propagated (otherwise the original exception will be lost).
     *
     * <p>If {@code swallowIOException} is true then we never throw {@code IOException} but merely log
     * it.
     *
     * <p>Example: <pre>   {@code
     *
     *   public void useStreamNicely() throws IOException {
     *     SomeStream stream = new SomeStream("foo");
     *     boolean threw = true;
     *     try {
     *       // ... code which does something with the stream ...
     *       threw = false;
     *     } finally {
     *       // If an exception occurs, rethrow it only if threw==false:
     *       Closeables.close(stream, threw);
     *     }
     *   }}</pre>
     *
     * @param closeable the {@code Closeable} object to be closed, or null, in which case this method
     *     does nothing
     * @param swallowIOException if true, don't propagate IO exceptions thrown by the {@code close}
     *     methods
     * @throws IOException if {@code swallowIOException} is false and {@code close} throws an
     *     {@code IOException}.
     */
    public static void close(@Nullable Closeable closeable,
                             boolean swallowIOException) throws IOException {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            if (swallowIOException) {
                logger.log(Level.WARNING,
                        "IOException thrown while closing Closeable.", e);
            } else {
                throw e;
            }
        }
    }
}
