package tech.mlsql.common.utils.collect;

import java.util.ListIterator;

/**
 * A list iterator that does not support {@link #remove}, {@link #add}, or
 * {@link #set}.
 *
 * @since 7.0
 * @author Louis Wasserman
 */

public abstract class UnmodifiableListIterator<E>
        extends UnmodifiableIterator<E> implements ListIterator<E> {
    /** Constructor for use by subclasses. */
    protected UnmodifiableListIterator() {}

    /**
     * Guaranteed to throw an exception and leave the underlying data unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated @Override public final void add(E e) {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the underlying data unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated @Override public final void set(E e) {
        throw new UnsupportedOperationException();
    }
}