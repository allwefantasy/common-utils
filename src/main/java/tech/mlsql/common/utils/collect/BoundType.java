package tech.mlsql.common.utils.collect;

/**
 * Indicates whether an endpoint of some range is contained in the range itself ("closed") or not
 * ("open"). If a range is unbounded on a side, it is neither open nor closed on that side; the
 * bound simply does not exist.
 *
 * @since 10.0
 */
public enum BoundType {
    /**
     * The endpoint value <i>is not</i> considered part of the set ("exclusive").
     */
    OPEN {
        @Override
        BoundType flip() {
            return CLOSED;
        }
    },
    /**
     * The endpoint value <i>is</i> considered part of the set ("inclusive").
     */
    CLOSED {
        @Override
        BoundType flip() {
            return OPEN;
        }
    };

    /**
     * Returns the bound type corresponding to a boolean value for inclusivity.
     */
    static BoundType forBoolean(boolean inclusive) {
        return inclusive ? CLOSED : OPEN;
    }

    abstract BoundType flip();
}
