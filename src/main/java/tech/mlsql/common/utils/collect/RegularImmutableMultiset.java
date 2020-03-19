package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Implementation of {@link ImmutableMultiset} with zero or more elements.
 *
 * @author Jared Levy
 * @author Louis Wasserman
 */
@SuppressWarnings("serial")
// uses writeReplace(), not default serialization
class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
    private final transient ImmutableMap<E, Integer> map;
    private final transient int size;

    RegularImmutableMultiset(ImmutableMap<E, Integer> map, int size) {
        this.map = map;
        this.size = size;
    }

    @Override
    boolean isPartialView() {
        return map.isPartialView();
    }

    @Override
    public int count(@Nullable Object element) {
        Integer value = map.get(element);
        return (value == null) ? 0 : value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(@Nullable Object element) {
        return map.containsKey(element);
    }

    @Override
    public ImmutableSet<E> elementSet() {
        return map.keySet();
    }

    @Override
    Entry<E> getEntry(int index) {
        Map.Entry<E, Integer> mapEntry = map.entrySet().asList().get(index);
        return Multisets.immutableEntry(mapEntry.getKey(), mapEntry.getValue());
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}

