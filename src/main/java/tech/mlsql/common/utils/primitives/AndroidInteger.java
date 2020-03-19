package tech.mlsql.common.utils.primitives;

import javax.annotation.CheckForNull;

import static tech.mlsql.common.utils.base.Preconditions.checkArgument;
import static tech.mlsql.common.utils.base.Preconditions.checkNotNull;

/**
 * Static utility methods derived from Android's {@code Integer.java}.
 */
final class AndroidInteger {
    /**
     * See {@link Ints#tryParse(String)} for the public interface.
     */
    @CheckForNull
    static Integer tryParse(String string) {
        return tryParse(string, 10);
    }

    /**
     * See {@link Ints#tryParse(String, int)} for the public interface.
     */
    @CheckForNull
    static Integer tryParse(String string, int radix) {
        checkNotNull(string);
        checkArgument(radix >= Character.MIN_RADIX,
                "Invalid radix %s, min radix is %s", radix, Character.MIN_RADIX);
        checkArgument(radix <= Character.MAX_RADIX,
                "Invalid radix %s, max radix is %s", radix, Character.MAX_RADIX);
        int length = string.length(), i = 0;
        if (length == 0) {
            return null;
        }
        boolean negative = string.charAt(i) == '-';
        if (negative && ++i == length) {
            return null;
        }
        return tryParse(string, i, radix, negative);
    }

    @CheckForNull
    private static Integer tryParse(String string, int offset, int radix,
                                    boolean negative) {
        int max = Integer.MIN_VALUE / radix;
        int result = 0, length = string.length();
        while (offset < length) {
            int digit = Character.digit(string.charAt(offset++), radix);
            if (digit == -1) {
                return null;
            }
            if (max > result) {
                return null;
            }
            int next = result * radix - digit;
            if (next > result) {
                return null;
            }
            result = next;
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                return null;
            }
        }
        // For GWT where ints do not overflow
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            return null;
        }
        return result;
    }

    private AndroidInteger() {}
}
