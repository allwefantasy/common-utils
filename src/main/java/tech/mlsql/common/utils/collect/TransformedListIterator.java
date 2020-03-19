package tech.mlsql.common.utils.collect;

import tech.mlsql.common.utils.base.Function;

import java.util.ListIterator;

/**
 * An iterator that transforms a backing list iterator; for internal use. This
 * avoids the object overhead of constructing a {@link Function} for internal
 * methods.
 *
 * @author Louis Wasserman
 */
abstract class TransformedListIterator<F, T> extends TransformedIterator<F, T>
        implements ListIterator<T> {
    TransformedListIterator(ListIterator<? extends F> backingIterator) {
        super(backingIterator);
    }

    private ListIterator<? extends F> backingIterator() {
        return Iterators.cast(backingIterator);
    }

    @Override
    public final boolean hasPrevious() {
        return backingIterator().hasPrevious();
    }

    @Override
    public final T previous() {
        return transform(backingIterator().previous());
    }

    @Override
    public final int nextIndex() {
        return backingIterator().nextIndex();
    }

    @Override
    public final int previousIndex() {
        return backingIterator().previousIndex();
    }

    @Override
    public void set(T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T element) {
        throw new UnsupportedOperationException();
    }
}
