package tech.mlsql.common.utils.collect;

import tech.mlsql.common.utils.base.Objects;
import tech.mlsql.common.utils.base.Predicate;
import tech.mlsql.common.utils.base.Predicates;

import javax.annotation.Nullable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;

/**
 * Implementation for {@link FilteredMultimap#values()}.
 *
 * @author Louis Wasserman
 */
final class FilteredMultimapValues<K, V> extends AbstractCollection<V> {
    private final FilteredMultimap<K, V> multimap;

    FilteredMultimapValues(FilteredMultimap<K, V> multimap) {
        this.multimap = checkNotNull(multimap);
    }

    @Override
    public Iterator<V> iterator() {
        return tech.mlsql.common.utils.collect.Maps.valueIterator(multimap.entries().iterator());
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return multimap.containsValue(o);
    }

    @Override
    public int size() {
        return multimap.size();
    }

    @Override
    public boolean remove(@Nullable Object o) {
        Predicate<? super Entry<K, V>> entryPredicate = multimap.entryPredicate();
        for (Iterator<Entry<K, V>> unfilteredItr = multimap.unfiltered().entries().iterator();
             unfilteredItr.hasNext();) {
            Map.Entry<K, V> entry = unfilteredItr.next();
            if (entryPredicate.apply(entry) && Objects.equal(entry.getValue(), o)) {
                unfilteredItr.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return tech.mlsql.common.utils.collect.Iterables.removeIf(multimap.unfiltered().entries(),
                // explicit <Entry<K, V>> is required to build with JDK6
                Predicates.<Entry<K, V>>and(multimap.entryPredicate(),
                        tech.mlsql.common.utils.collect.Maps.<V>valuePredicateOnEntries(Predicates.in(c))));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return Iterables.removeIf(multimap.unfiltered().entries(),
                // explicit <Entry<K, V>> is required to build with JDK6
                Predicates.<Entry<K, V>>and(multimap.entryPredicate(),
                        Maps.<V>valuePredicateOnEntries(Predicates.not(Predicates.in(c)))));
    }

    @Override
    public void clear() {
        multimap.clear();
    }
}
