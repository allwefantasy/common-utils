package tech.mlsql.common.utils.io;

import java.io.IOException;

/**
 * A factory for readable streams of bytes or characters.
 *
 * @author Chris Nokleberg
 * @since 1.0
 * @deprecated For {@code InputSupplier<? extends InputStream>}, use
 *     {@link ByteSource} instead. For {@code InputSupplier<? extends Reader>},
 *     use {@link CharSource}. Implementations of {@code InputSupplier} that
 *     don't fall into one of those categories do not benefit from any of the
 *     methods in {@code common.io} and should use a different interface. This
 *     interface is scheduled for removal in June 2015.
 */
@Deprecated
public interface InputSupplier<T> {

    /**
     * Returns an object that encapsulates a readable resource.
     * <p>
     * Like {@link Iterable#iterator}, this method may be called repeatedly to
     * get independent channels to the same underlying resource.
     * <p>
     * Where the channel maintains a position within the resource, moving that
     * cursor within one channel should not affect the starting position of
     * channels returned by other calls.
     */
    T getInput() throws IOException;
}
