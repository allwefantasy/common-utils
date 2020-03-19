package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.util.*;

import static tech.mlsql.common.utils.base.Preconditions.checkArgument;
import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;
import static tech.mlsql.common.utils.collect.SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX;
import static tech.mlsql.common.utils.collect.SortedLists.KeyAbsentBehavior.NEXT_HIGHER;
import static tech.mlsql.common.utils.collect.SortedLists.KeyPresentBehavior.*;

/**
 * An immutable sorted set with one or more elements. TODO(jlevy): Consider
 * separate class for a single-element sorted set.
 *
 * @author Jared Levy
 * @author Louis Wasserman
 */

@SuppressWarnings("serial")
final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E> {

    private transient final ImmutableList<E> elements;

    RegularImmutableSortedSet(
            ImmutableList<E> elements, Comparator<? super E> comparator) {
        super(comparator);
        this.elements = elements;
        checkArgument(!elements.isEmpty());
    }

    @Override public UnmodifiableIterator<E> iterator() {
        return elements.iterator();
    }

    
    @Override public UnmodifiableIterator<E> descendingIterator() {
        return elements.reverse().iterator();
    }

    @Override public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override public boolean contains(Object o) {
        try {
            return o != null && unsafeBinarySearch(o) >= 0;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override public boolean containsAll(Collection<?> targets) {
        // TODO(jlevy): For optimal performance, use a binary search when
        // targets.size() < size() / log(size())
        // TODO(kevinb): see if we can share code with OrderedIterator after it
        // graduates from labs.
        if (targets instanceof Multiset) {
            targets = ((Multiset<?>) targets).elementSet();
        }
        if (!SortedIterables.hasSameComparator(comparator(), targets)
                || (targets.size() <= 1)) {
            return super.containsAll(targets);
        }

        /*
         * If targets is a sorted set with the same comparator, containsAll can run
         * in O(n) time stepping through the two collections.
         */
        PeekingIterator<E> thisIterator = Iterators.peekingIterator(iterator());
        Iterator<?> thatIterator = targets.iterator();
        Object target = thatIterator.next();

        try {

            while (thisIterator.hasNext()) {

                int cmp = unsafeCompare(thisIterator.peek(), target);

                if (cmp < 0) {
                    thisIterator.next();
                } else if (cmp == 0) {

                    if (!thatIterator.hasNext()) {

                        return true;
                    }

                    target = thatIterator.next();

                } else if (cmp > 0) {
                    return false;
                }
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException e) {
            return false;
        }

        return false;
    }

    private int unsafeBinarySearch(Object key) throws ClassCastException {
        return Collections.binarySearch(elements, key, unsafeComparator());
    }

    @Override boolean isPartialView() {
        return elements.isPartialView();
    }

    @Override
    int copyIntoArray(Object[] dst, int offset) {
        return elements.copyIntoArray(dst, offset);
    }

    @Override public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Set)) {
            return false;
        }

        Set<?> that = (Set<?>) object;
        if (size() != that.size()) {
            return false;
        }

        if (SortedIterables.hasSameComparator(comparator, that)) {
            Iterator<?> otherIterator = that.iterator();
            try {
                Iterator<E> iterator = iterator();
                while (iterator.hasNext()) {
                    Object element = iterator.next();
                    Object otherElement = otherIterator.next();
                    if (otherElement == null
                            || unsafeCompare(element, otherElement) != 0) {
                        return false;
                    }
                }
                return true;
            } catch (ClassCastException e) {
                return false;
            } catch (NoSuchElementException e) {
                return false; // concurrent change to other set
            }
        }
        return this.containsAll(that);
    }

    @Override
    public E first() {
        return elements.get(0);
    }

    @Override
    public E last() {
        return elements.get(size() - 1);
    }

    @Override
    public E lower(E element) {
        int index = headIndex(element, false) - 1;
        return (index == -1) ? null : elements.get(index);
    }

    @Override
    public E floor(E element) {
        int index = headIndex(element, true) - 1;
        return (index == -1) ? null : elements.get(index);
    }

    @Override
    public E ceiling(E element) {
        int index = tailIndex(element, true);
        return (index == size()) ? null : elements.get(index);
    }

    @Override
    public E higher(E element) {
        int index = tailIndex(element, false);
        return (index == size()) ? null : elements.get(index);
    }

    @Override
    ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive) {
        return getSubSet(0, headIndex(toElement, inclusive));
    }

    int headIndex(E toElement, boolean inclusive) {
        return SortedLists.binarySearch(
                elements, checkNotNull(toElement), comparator(),
                inclusive ? FIRST_AFTER : FIRST_PRESENT, NEXT_HIGHER);
    }

    @Override
    ImmutableSortedSet<E> subSetImpl(
            E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return tailSetImpl(fromElement, fromInclusive)
                .headSetImpl(toElement, toInclusive);
    }

    @Override
    ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive) {
        return getSubSet(tailIndex(fromElement, inclusive), size());
    }

    int tailIndex(E fromElement, boolean inclusive) {
        return SortedLists.binarySearch(
                elements,
                checkNotNull(fromElement),
                comparator(),
                inclusive ? FIRST_PRESENT : FIRST_AFTER, NEXT_HIGHER);
    }

    // Pretend the comparator can compare anything. If it turns out it can't
    // compare two elements, it'll throw a CCE. Only methods that are specified to
    // throw CCE should call this.
    @SuppressWarnings("unchecked")
    Comparator<Object> unsafeComparator() {
        return (Comparator<Object>) comparator;
    }

    ImmutableSortedSet<E> getSubSet(int newFromIndex, int newToIndex) {
        if (newFromIndex == 0 && newToIndex == size()) {
            return this;
        } else if (newFromIndex < newToIndex) {
            return new RegularImmutableSortedSet<E>(
                    elements.subList(newFromIndex, newToIndex), comparator);
        } else {
            return emptySet(comparator);
        }
    }

    @Override int indexOf(@Nullable Object target) {
        if (target == null) {
            return -1;
        }
        int position;
        try {
            position = SortedLists.binarySearch(elements, target, unsafeComparator(),
                    ANY_PRESENT, INVERTED_INSERTION_INDEX);
        } catch (ClassCastException e) {
            return -1;
        }
        return (position >= 0) ? position : -1;
    }

    @Override ImmutableList<E> createAsList() {
        return new ImmutableSortedAsList<E>(this, elements);
    }

    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        return new RegularImmutableSortedSet<E>(elements.reverse(),
                Ordering.from(comparator).reverse());
    }
}

