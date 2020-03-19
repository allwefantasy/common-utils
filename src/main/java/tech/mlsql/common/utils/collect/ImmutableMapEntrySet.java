package tech.mlsql.common.utils.collect;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map.Entry;

/**
 * {@code entrySet()} implementation for {@link ImmutableMap}.
 *
 * @author Jesse Wilson
 * @author Kevin Bourrillion
 */
abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Entry<K, V>> {
    ImmutableMapEntrySet() {}

    abstract ImmutableMap<K, V> map();

    @Override
    public int size() {
        return map().size();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (object instanceof Entry) {
            Entry<?, ?> entry = (Entry<?, ?>) object;
            V value = map().get(entry.getKey());
            return value != null && value.equals(entry.getValue());
        }
        return false;
    }

    @Override
    boolean isPartialView() {
        return map().isPartialView();
    }
    
    @Override
    Object writeReplace() {
        return new EntrySetSerializedForm<K, V>(map());
    }
    
    private static class EntrySetSerializedForm<K, V> implements Serializable {
        final ImmutableMap<K, V> map;
        EntrySetSerializedForm(ImmutableMap<K, V> map) {
            this.map = map;
        }
        Object readResolve() {
            return map.entrySet();
        }
        private static final long serialVersionUID = 0;
    }
}
