package tech.mlsql.common.utils.collect;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * A mutable value of type {@code int}, for multisets to use in tracking counts of values.
 *
 * @author Louis Wasserman
 */
final class Count implements Serializable {
    private int value;

    Count(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public int getAndAdd(int delta) {
        int result = value;
        value = result + delta;
        return result;
    }

    public int addAndGet(int delta) {
        return value += delta;
    }

    public void set(int newValue) {
        value = newValue;
    }

    public int getAndSet(int newValue) {
        int result = value;
        value = newValue;
        return result;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Count && ((Count) obj).value == value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
