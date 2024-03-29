package tech.mlsql.common.utils.collect;

import tech.mlsql.common.utils.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Implementation of {@link ImmutableList} with one or more elements.
 *
 * @author Kevin Bourrillion
 */
@SuppressWarnings("serial") // uses writeReplace(), not default serialization
class RegularImmutableList<E> extends ImmutableList<E> {
    private final transient int offset;
    private final transient int size;
    private final transient Object[] array;

    RegularImmutableList(Object[] array, int offset, int size) {
        this.offset = offset;
        this.size = size;
        this.array = array;
    }

    RegularImmutableList(Object[] array) {
        this(array, 0, array.length);
    }

    @Override
    public int size() {
        return size;
    }

    @Override boolean isPartialView() {
        return size != array.length;
    }

    @Override
    int copyIntoArray(Object[] dst, int dstOff) {
        System.arraycopy(array, offset, dst, dstOff, size);
        return dstOff + size;
    }

    // The fake cast to E is safe because the creation methods only allow E's
    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        Preconditions.checkElementIndex(index, size);
        return (E) array[index + offset];
    }

    @Override
    public int indexOf(@Nullable Object object) {
        if (object == null) {
            return -1;
        }
        for (int i = 0; i < size; i++) {
            if (array[offset + i].equals(object)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(@Nullable Object object) {
        if (object == null) {
            return -1;
        }
        for (int i = size - 1; i >= 0; i--) {
            if (array[offset + i].equals(object)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    ImmutableList<E> subListUnchecked(int fromIndex, int toIndex) {
        return new RegularImmutableList<E>(
                array, offset + fromIndex, toIndex - fromIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UnmodifiableListIterator<E> listIterator(int index) {
        // for performance
        // The fake cast to E is safe because the creation methods only allow E's
        return (UnmodifiableListIterator<E>)
                Iterators.forArray(array, offset, size, index);
    }

    // TODO(user): benchmark optimizations for equals() and see if they're worthwhile
}
