package tech.mlsql.common.utils.base;

import com.google.common.primitives.Longs;

import java.util.BitSet;

/**
 * 18/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
public class WowBitSet {
    private BitSet bitSet;

    public WowBitSet(BitSet _bitSet) {
        this.bitSet = _bitSet;
    }

    public BitSet raw() {
        return bitSet;
    }

    public static WowBitSet fromLong(long i) {
        return new WowBitSet(BitSet.valueOf(Longs.toByteArray(Long.reverseBytes(i))));
    }

    public long toLong() {
        byte[] by = bitSet.toByteArray();
        long longValue = 0;
        for (int i = 0; i < by.length; i++) {
            longValue += ((long) by[i] & 0xffL) << (8 * i);
        }
        return longValue;
    }

}
