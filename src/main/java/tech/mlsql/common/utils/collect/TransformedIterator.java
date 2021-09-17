package tech.mlsql.common.utils.collect;


import java.util.Iterator;

import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;

/**
 * An iterator that transforms a backing iterator; for internal use. This avoids
 * the object overhead of constructing a {@link Function} for internal methods.
 *
 * @author Louis Wasserman
 */
abstract class TransformedIterator<F, T> implements Iterator<T> {
    final Iterator<? extends F> backingIterator;

    TransformedIterator(Iterator<? extends F> backingIterator) {
        this.backingIterator = checkNotNull(backingIterator);
    }

    abstract T transform(F from);

    @Override
    public final boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    public final T next() {
        return transform(backingIterator.next());
    }

    @Override
    public final void remove() {
        backingIterator.remove();
    }
}

