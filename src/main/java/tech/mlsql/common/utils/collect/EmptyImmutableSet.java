package tech.mlsql.common.utils.collect;


import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * An empty immutable set.
 *
 * @author Kevin Bourrillion
 */
final class EmptyImmutableSet extends ImmutableSet<Object> {
    static final EmptyImmutableSet INSTANCE = new EmptyImmutableSet();

    private EmptyImmutableSet() {}

    @Override
    public int size() {
        return 0;
    }

    @Override public boolean isEmpty() {
        return true;
    }

    @Override public boolean contains(@Nullable Object target) {
        return false;
    }

    @Override public boolean containsAll(Collection<?> targets) {
        return targets.isEmpty();
    }

    @Override public UnmodifiableIterator<Object> iterator() {
        return Iterators.emptyIterator();
    }

    @Override boolean isPartialView() {
        return false;
    }

    @Override
    int copyIntoArray(Object[] dst, int offset) {
        return offset;
    }

    @Override
    public ImmutableList<Object> asList() {
        return ImmutableList.of();
    }

    @Override public boolean equals(@Nullable Object object) {
        if (object instanceof Set) {
            Set<?> that = (Set<?>) object;
            return that.isEmpty();
        }
        return false;
    }

    @Override public final int hashCode() {
        return 0;
    }

    @Override boolean isHashCodeFast() {
        return true;
    }

    @Override public String toString() {
        return "[]";
    }

    Object readResolve() {
        return INSTANCE; // preserve singleton property
    }

    private static final long serialVersionUID = 0;
}

