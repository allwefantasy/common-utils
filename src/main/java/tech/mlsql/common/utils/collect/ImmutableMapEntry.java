package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;

import static tech.mlsql.common.utils.collect.CollectPreconditions.checkEntryNotNull;

/**
 * Implementation of {@code Map.Entry} for {@link ImmutableMap} that adds extra methods to traverse
 * hash buckets for the key and the value. This allows reuse in {@link RegularImmutableMap} and
 * {@link RegularImmutableBiMap}, which don't have to recopy the entries created by their
 * {@code Builder} implementations.
 *
 * @author Louis Wasserman
 */

abstract class ImmutableMapEntry<K, V> extends ImmutableEntry<K, V> {
    ImmutableMapEntry(K key, V value) {
        super(key, value);
        checkEntryNotNull(key, value);
    }

    ImmutableMapEntry(ImmutableMapEntry<K, V> contents) {
        super(contents.getKey(), contents.getValue());
        // null check would be redundant
    }

    @Nullable
    abstract ImmutableMapEntry<K, V> getNextInKeyBucket();

    @Nullable
    abstract ImmutableMapEntry<K, V> getNextInValueBucket();

    static final class TerminalEntry<K, V> extends ImmutableMapEntry<K, V> {
        TerminalEntry(ImmutableMapEntry<K, V> contents) {
            super(contents);
        }

        TerminalEntry(K key, V value) {
            super(key, value);
        }

        @Override
        @Nullable
        ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return null;
        }

        @Override
        @Nullable
        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return null;
        }
    }
}
