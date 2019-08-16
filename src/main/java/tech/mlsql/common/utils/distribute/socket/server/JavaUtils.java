package tech.mlsql.common.utils.distribute.socket.server;

import java.io.Closeable;
import java.io.IOException;

/**
 * 2019-08-16 WilliamZhu(allwefantasy@gmail.com)
 */
public class JavaUtils {
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException should not have been thrown.", e);
        }
    }
}
