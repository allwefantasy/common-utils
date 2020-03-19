package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * List returned by {@code ImmutableSortedSet.asList()} when the set isn't empty.
 *
 * @author Jared Levy
 * @author Louis Wasserman
 */
@SuppressWarnings("serial")
final class ImmutableSortedAsList<E> extends RegularImmutableAsList<E>
        implements SortedIterable<E> {
    ImmutableSortedAsList(
            ImmutableSortedSet<E> backingSet, ImmutableList<E> backingList) {
        super(backingSet, backingList);
    }

    @Override
    ImmutableSortedSet<E> delegateCollection() {
        return (ImmutableSortedSet<E>) super.delegateCollection();
    }

    @Override public Comparator<? super E> comparator() {
        return delegateCollection().comparator();
    }

    // Override indexOf() and lastIndexOf() to be O(log N) instead of O(N).
    
    // TODO(cpovirk): consider manual binary search under GWT to preserve O(log N) lookup
    @Override public int indexOf(@Nullable Object target) {
        int index = delegateCollection().indexOf(target);

        // TODO(kevinb): reconsider if it's really worth making feeble attempts at
        // sanity for inconsistent comparators.

        // The equals() check is needed when the comparator isn't compatible with
        // equals().
        return (index >= 0 && get(index).equals(target)) ? index : -1;
    }
    
    @Override public int lastIndexOf(@Nullable Object target) {
        return indexOf(target);
    }

    @Override
    public boolean contains(Object target) {
        // Necessary for ISS's with comparators inconsistent with equals.
        return indexOf(target) >= 0;
    }
    
    /*
     * TODO(cpovirk): if we start to override indexOf/lastIndexOf under GWT, we'll want some way to
     * override subList to return an ImmutableSortedAsList for better performance. Right now, I'm not
     * sure there's any performance hit from our failure to override subListUnchecked under GWT
     */
    @Override
    ImmutableList<E> subListUnchecked(int fromIndex, int toIndex) {
        return new RegularImmutableSortedSet<E>(
                super.subListUnchecked(fromIndex, toIndex), comparator())
                .asList();
    }
}
