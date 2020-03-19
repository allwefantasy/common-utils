package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @see com.google.common.collect.Maps#immutableEntry(Object, Object)
 */
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V>
        implements Serializable {
    final K key;
    final V value;

    ImmutableEntry(@Nullable K key, @Nullable V value) {
        this.key = key;
        this.value = value;
    }

    @Nullable @Override public final K getKey() {
        return key;
    }

    @Nullable @Override public final V getValue() {
        return value;
    }

    @Override public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    private static final long serialVersionUID = 0;
}
