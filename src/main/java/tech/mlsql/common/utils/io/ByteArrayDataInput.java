package tech.mlsql.common.utils.io;

import java.io.DataInput;
import java.io.IOException;

/**
 * An extension of {@code DataInput} for reading from in-memory byte arrays; its
 * methods offer identical functionality but do not throw {@link IOException}.
 *
 * <p><b>Warning:<b> The caller is responsible for not attempting to read past
 * the end of the array. If any method encounters the end of the array
 * prematurely, it throws {@link IllegalStateException} to signify <i>programmer
 * error</i>. This behavior is a technical violation of the supertype's
 * contract, which specifies a checked exception.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public interface ByteArrayDataInput extends DataInput {
    @Override void readFully(byte b[]);

    @Override void readFully(byte b[], int off, int len);

    @Override int skipBytes(int n);

    @Override boolean readBoolean();

    @Override byte readByte();

    @Override int readUnsignedByte();

    @Override short readShort();

    @Override int readUnsignedShort();

    @Override char readChar();

    @Override int readInt();

    @Override long readLong();

    @Override float readFloat();

    @Override double readDouble();

    @Override String readLine();

    @Override String readUTF();
}
