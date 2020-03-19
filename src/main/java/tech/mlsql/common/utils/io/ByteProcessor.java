package tech.mlsql.common.utils.io;

import tech.mlsql.common.utils.annotations.Beta;

import java.io.IOException;

/**
 * A callback interface to process bytes from a stream.
 *
 * <p>{@link #processBytes} will be called for each line that is read, and
 * should return {@code false} when you want to stop processing.
 *
 * @author Chris Nokleberg
 * @since 1.0
 */
@Beta
public interface ByteProcessor<T> {
    /**
     * This method will be called for each chunk of bytes in an
     * input stream. The implementation should process the bytes
     * from {@code buf[off]} through {@code buf[off + len - 1]}
     * (inclusive).
     *
     * @param buf the byte array containing the data to process
     * @param off the initial offset into the array
     * @param len the length of data to be processed
     * @return true to continue processing, false to stop
     */
    boolean processBytes(byte[] buf, int off, int len) throws IOException;

    /** Return the result of processing all the bytes. */
    T getResult();
}
