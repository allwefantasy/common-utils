package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * An ordering that treats all references as equals, even nulls.
 *
 * @author Emily Soldal
 */
final class AllEqualOrdering extends Ordering<Object> implements Serializable {
    static final AllEqualOrdering INSTANCE = new AllEqualOrdering();

    @Override
    public int compare(@Nullable Object left, @Nullable Object right) {
        return 0;
    }

    @Override
    public <E> List<E> sortedCopy(Iterable<E> iterable) {
        return Lists.newArrayList(iterable);
    }

    @Override
    public <E> ImmutableList<E> immutableSortedCopy(Iterable<E> iterable) {
        return ImmutableList.copyOf(iterable);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> Ordering<S> reverse() {
        return (Ordering<S>) this;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "Ordering.allEqual()";
    }

    private static final long serialVersionUID = 0;
}
