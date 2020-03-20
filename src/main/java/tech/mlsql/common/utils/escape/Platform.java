package tech.mlsql.common.utils.escape;

import tech.mlsql.common.utils.annotations.GwtCompatible;

/**
 * Methods factored out so that they can be emulated differently in GWT.
 *
 * @author Jesse Wilson
 */
@GwtCompatible(emulated = true)
final class Platform {
    private Platform() {}

    /** Returns a thread-local 1024-char array. */
    static char[] charBufferFromThreadLocal() {
        return DEST_TL.get();
    }

    /**
     * A thread-local destination buffer to keep us from creating new buffers.
     * The starting size is 1024 characters.  If we grow past this we don't
     * put it back in the threadlocal, we just keep going and grow as needed.
     */
    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() {
        @Override
        protected char[] initialValue() {
            return new char[1024];
        }
    };
}
