package tech.mlsql.common.utils.collect;


import java.util.Iterator;

/**
 * An iterator which forwards all its method calls to another iterator.
 * Subclasses should override one or more methods to modify the behavior of the
 * backing iterator as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * @author Kevin Bourrillion
 * @since 2.0 (imported from Google Collections Library)
 */
public abstract class ForwardingIterator<T>
        extends ForwardingObject implements Iterator<T> {

    /** Constructor for use by subclasses. */
    protected ForwardingIterator() {}

    @Override protected abstract Iterator<T> delegate();

    @Override
    public boolean hasNext() {
        return delegate().hasNext();
    }

    @Override
    public T next() {
        return delegate().next();
    }

    @Override
    public void remove() {
        delegate().remove();
    }
}
