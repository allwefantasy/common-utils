package tech.mlsql.common.utils.collect;

/**
 * Implementation of {@link ImmutableSet} with two or more elements.
 *
 * @author Kevin Bourrillion
 */
@SuppressWarnings("serial") // uses writeReplace(), not default serialization
final class RegularImmutableSet<E> extends ImmutableSet<E> {
    private final Object[] elements;
    // the same elements in hashed positions (plus nulls)
    final transient Object[] table;
    // 'and' with an int to get a valid table index.
    private final transient int mask;
    private final transient int hashCode;

    RegularImmutableSet(
            Object[] elements, int hashCode, Object[] table, int mask) {
        this.elements = elements;
        this.table = table;
        this.mask = mask;
        this.hashCode = hashCode;
    }

    @Override public boolean contains(Object target) {
        if (target == null) {
            return false;
        }
        for (int i = Hashing.smear(target.hashCode()); true; i++) {
            Object candidate = table[i & mask];
            if (candidate == null) {
                return false;
            }
            if (candidate.equals(target)) {
                return true;
            }
        }
    }

    @Override
    public int size() {
        return elements.length;
    }

    @SuppressWarnings("unchecked") // all elements are E's
    @Override
    public UnmodifiableIterator<E> iterator() {
        return (UnmodifiableIterator<E>) Iterators.forArray(elements);
    }

    @Override
    int copyIntoArray(Object[] dst, int offset) {
        System.arraycopy(elements, 0, dst, offset, elements.length);
        return offset + elements.length;
    }

    @Override
    ImmutableList<E> createAsList() {
        return new RegularImmutableAsList<E>(this, elements);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override boolean isHashCodeFast() {
        return true;
    }
}
