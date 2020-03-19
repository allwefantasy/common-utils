package tech.mlsql.common.utils.collect;


/**
 * An {@link ImmutableAsList} implementation specialized for when the delegate collection is
 * already backed by an {@code ImmutableList} or array.
 *
 * @author Louis Wasserman
 */
@SuppressWarnings("serial") // uses writeReplace, not default serialization
class RegularImmutableAsList<E> extends ImmutableAsList<E> {
    private final ImmutableCollection<E> delegate;
    private final ImmutableList<? extends E> delegateList;

    RegularImmutableAsList(ImmutableCollection<E> delegate, ImmutableList<? extends E> delegateList) {
        this.delegate = delegate;
        this.delegateList = delegateList;
    }

    RegularImmutableAsList(ImmutableCollection<E> delegate, Object[] array) {
        this(delegate, ImmutableList.<E>asImmutableList(array));
    }

    @Override
    ImmutableCollection<E> delegateCollection() {
        return delegate;
    }

    ImmutableList<? extends E> delegateList() {
        return delegateList;
    }

    @SuppressWarnings("unchecked")  // safe covariant cast!
    @Override
    public UnmodifiableListIterator<E> listIterator(int index) {
        return (UnmodifiableListIterator<E>) delegateList.listIterator(index);
    }
    
    @Override
    int copyIntoArray(Object[] dst, int offset) {
        return delegateList.copyIntoArray(dst, offset);
    }

    @Override
    public E get(int index) {
        return delegateList.get(index);
    }
}
