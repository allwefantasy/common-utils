package tech.mlsql.common.utils.collect;


import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map.Entry;

/**
 * {@code values()} implementation for {@link ImmutableMap}.
 *
 * @author Jesse Wilson
 * @author Kevin Bourrillion
 */
final class ImmutableMapValues<K, V> extends ImmutableCollection<V> {
    private final ImmutableMap<K, V> map;

    ImmutableMapValues(ImmutableMap<K, V> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public UnmodifiableIterator<V> iterator() {
        return Maps.valueIterator(map.entrySet().iterator());
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return object != null && Iterators.contains(iterator(), object);
    }

    @Override
    boolean isPartialView() {
        return true;
    }

    @Override
    ImmutableList<V> createAsList() {
        final ImmutableList<Entry<K, V>> entryList = map.entrySet().asList();
        return new ImmutableAsList<V>() {
            @Override
            public V get(int index) {
                return entryList.get(index).getValue();
            }

            @Override
            ImmutableCollection<V> delegateCollection() {
                return ImmutableMapValues.this;
            }
        };
    }

    
    @Override Object writeReplace() {
        return new SerializedForm<V>(map);
    }

    
    private static class SerializedForm<V> implements Serializable {
        final ImmutableMap<?, V> map;
        SerializedForm(ImmutableMap<?, V> map) {
            this.map = map;
        }
        Object readResolve() {
            return map.values();
        }
        private static final long serialVersionUID = 0;
    }
}

