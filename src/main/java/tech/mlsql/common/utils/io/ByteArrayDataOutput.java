package tech.mlsql.common.utils.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * An extension of {@code DataOutput} for writing to in-memory byte arrays; its
 * methods offer identical functionality but do not throw {@link IOException}.
 *
 * @author Jayaprabhakar Kadarkarai
 * @since 1.0
 */
public interface ByteArrayDataOutput extends DataOutput {
    @Override void write(int b);
    @Override void write(byte b[]);
    @Override void write(byte b[], int off, int len);
    @Override void writeBoolean(boolean v);
    @Override void writeByte(int v);
    @Override void writeShort(int v);
    @Override void writeChar(int v);
    @Override void writeInt(int v);
    @Override void writeLong(long v);
    @Override void writeFloat(float v);
    @Override void writeDouble(double v);
    @Override void writeChars(String s);
    @Override void writeUTF(String s);

    /**
     * @deprecated This method is dangerous as it discards the high byte of
     * every character. For UTF-8, use {@code write(s.getBytes(Charsets.UTF_8))}.
     */
    @Deprecated @Override void writeBytes(String s);

    /**
     * Returns the contents that have been written to this instance,
     * as a byte array.
     */
    byte[] toByteArray();
}

