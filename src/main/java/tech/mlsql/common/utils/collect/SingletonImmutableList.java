package tech.mlsql.common.utils.collect;

import tech.mlsql.common.utils.base.Preconditions;

import javax.annotation.Nullable;
import java.util.List;

import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;

/**
 * Implementation of {@link ImmutableList} with exactly one element.
 *
 * @author Hayward Chan
 */
@SuppressWarnings("serial") // uses writeReplace(), not default serialization
final class SingletonImmutableList<E> extends ImmutableList<E> {

    final transient E element;

    SingletonImmutableList(E element) {
        this.element = checkNotNull(element);
    }

    @Override
    public E get(int index) {
        Preconditions.checkElementIndex(index, 1);
        return element;
    }

    @Override public int indexOf(@Nullable Object object) {
        return element.equals(object) ? 0 : -1;
    }

    @Override public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(element);
    }

    @Override public int lastIndexOf(@Nullable Object object) {
        return indexOf(object);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override public ImmutableList<E> subList(int fromIndex, int toIndex) {
        Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
        return (fromIndex == toIndex) ? ImmutableList.<E>of() : this;
    }

    @Override public ImmutableList<E> reverse() {
        return this;
    }

    @Override public boolean contains(@Nullable Object object) {
        return element.equals(object);
    }

    @Override public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof List) {
            List<?> that = (List<?>) object;
            return that.size() == 1 && element.equals(that.get(0));
        }
        return false;
    }

    @Override public int hashCode() {
        // not caching hash code since it could change if the element is mutable
        // in a way that modifies its hash code.
        return 31 + element.hashCode();
    }

    @Override public String toString() {
        String elementToString = element.toString();
        return new StringBuilder(elementToString.length() + 2)
                .append('[')
                .append(elementToString)
                .append(']')
                .toString();
    }

    @Override public boolean isEmpty() {
        return false;
    }

    @Override boolean isPartialView() {
        return false;
    }

    @Override
    int copyIntoArray(Object[] dst, int offset) {
        dst[offset] = element;
        return offset + 1;
    }
}
