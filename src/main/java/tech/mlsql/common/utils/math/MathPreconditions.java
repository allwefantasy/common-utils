package tech.mlsql.common.utils.math;


import javax.annotation.Nullable;
import java.math.BigInteger;

/**
 * A collection of preconditions for math functions.
 *
 * @author Louis Wasserman
 */
final class MathPreconditions {
    static int checkPositive(@Nullable String role, int x) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    static long checkPositive(@Nullable String role, long x) {
        if (x <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    static BigInteger checkPositive(@Nullable String role, BigInteger x) {
        if (x.signum() <= 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
        }
        return x;
    }

    static int checkNonNegative(@Nullable String role, int x) {
        if (x < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }

    static long checkNonNegative(@Nullable String role, long x) {
        if (x < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }

    static BigInteger checkNonNegative(@Nullable String role, BigInteger x) {
        if (x.signum() < 0) {
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }

    static double checkNonNegative(@Nullable String role, double x) {
        if (!(x >= 0)) { // not x < 0, to work with NaN.
            throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
        }
        return x;
    }

    static void checkRoundingUnnecessary(boolean condition) {
        if (!condition) {
            throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
        }
    }

    static void checkInRange(boolean condition) {
        if (!condition) {
            throw new ArithmeticException("not in range");
        }
    }

    static void checkNoOverflow(boolean condition) {
        if (!condition) {
            throw new ArithmeticException("overflow");
        }
    }

    private MathPreconditions() {}
}
