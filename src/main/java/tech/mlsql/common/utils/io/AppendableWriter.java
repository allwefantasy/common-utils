package tech.mlsql.common.utils.io;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;

/**
 * Writer that places all output on an {@link Appendable} target. If the target
 * is {@link Flushable} or {@link Closeable}, flush()es and close()s will also
 * be delegated to the target.
 *
 * @author Alan Green
 * @author Sebastian Kanthak
 * @since 1.0
 */
class AppendableWriter extends Writer {
    private final Appendable target;
    private boolean closed;

    /**
     * Creates a new writer that appends everything it writes to {@code target}.
     *
     * @param target target to which to append output
     */
    AppendableWriter(Appendable target) {
        this.target = checkNotNull(target);
    }

    /*
     * Abstract methods from Writer
     */

    @Override public void write(char cbuf[], int off, int len)
            throws IOException {
        checkNotClosed();
        // It turns out that creating a new String is usually as fast, or faster
        // than wrapping cbuf in a light-weight CharSequence.
        target.append(new String(cbuf, off, len));
    }

    @Override public void flush() throws IOException {
        checkNotClosed();
        if (target instanceof Flushable) {
            ((Flushable) target).flush();
        }
    }

    @Override public void close() throws IOException {
        this.closed = true;
        if (target instanceof Closeable) {
            ((Closeable) target).close();
        }
    }

    /*
     * Override a few functions for performance reasons to avoid creating
     * unnecessary strings.
     */

    @Override public void write(int c) throws IOException {
        checkNotClosed();
        target.append((char) c);
    }

    @Override public void write(@Nullable String str) throws IOException {
        checkNotClosed();
        target.append(str);
    }

    @Override public void write(@Nullable String str, int off, int len) throws IOException {
        checkNotClosed();
        // tricky: append takes start, end pair...
        target.append(str, off, off + len);
    }

    @Override public Writer append(char c) throws IOException {
        checkNotClosed();
        target.append(c);
        return this;
    }

    @Override public Writer append(@Nullable CharSequence charSeq) throws IOException {
        checkNotClosed();
        target.append(charSeq);
        return this;
    }

    @Override public Writer append(@Nullable CharSequence charSeq, int start, int end)
            throws IOException {
        checkNotClosed();
        target.append(charSeq, start, end);
        return this;
    }

    private void checkNotClosed() throws IOException {
        if (closed) {
            throw new IOException("Cannot write to a closed writer.");
        }
    }
}
