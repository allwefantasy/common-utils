package tech.mlsql.common.utils.collect;



import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map.Entry;

/**
 * {@code keySet()} implementation for {@link ImmutableMap}.
 *
 * @author Jesse Wilson
 * @author Kevin Bourrillion
 */
final class ImmutableMapKeySet<K, V> extends ImmutableSet<K> {
    private final ImmutableMap<K, V> map;

    ImmutableMapKeySet(ImmutableMap<K, V> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public UnmodifiableIterator<K> iterator() {
        return asList().iterator();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return map.containsKey(object);
    }

    @Override
    ImmutableList<K> createAsList() {
        final ImmutableList<Entry<K, V>> entryList = map.entrySet().asList();
        return new ImmutableAsList<K>() {

            @Override
            public K get(int index) {
                return entryList.get(index).getKey();
            }

            @Override
            ImmutableCollection<K> delegateCollection() {
                return ImmutableMapKeySet.this;
            }

        };
    }

    @Override
    boolean isPartialView() {
        return true;
    }
    
    @Override Object writeReplace() {
        return new KeySetSerializedForm<K>(map);
    }

    private static class KeySetSerializedForm<K> implements Serializable {
        final ImmutableMap<K, ?> map;
        KeySetSerializedForm(ImmutableMap<K, ?> map) {
            this.map = map;
        }
        Object readResolve() {
            return map.keySet();
        }
        private static final long serialVersionUID = 0;
    }
}
