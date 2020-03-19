package tech.mlsql.common.utils.collect;

import static tech.mlsql.common.utils.base.Preconditions.checkState;

final class CollectPreconditions {

    static void checkEntryNotNull(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("null key in entry: null=" + value);
        } else if (value == null) {
            throw new NullPointerException("null value in entry: " + key + "=null");
        }
    }

    static int checkNonnegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    /**
     * Precondition tester for {@code Iterator.remove()} that throws an exception with a consistent
     * error message.
     */
    static void checkRemove(boolean canRemove) {
        checkState(canRemove, "no calls to next() since the last call to remove()");
    }
}
