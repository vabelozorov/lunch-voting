package ua.belozorov.lunchvoting.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.01.17.
 */
public final class ExceptionUtils {

    public static <T> Collection<T> requireNonNullNotEmpty(Collection<T> collection,
                                                           Supplier<? extends RuntimeException> ex) {
        return ofNullable(collection)
                .filter(c -> ! c.isEmpty())
                .orElseThrow(ex);
    }

    public static void checkParamsNotNull(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                throw new NullPointerException("Null param is not allowed, but found at position " + i);
            }
        }
    }

    public static <T> T executeAndUnwrapException(Supplier<T> supplier, Class<? extends RuntimeException> expect, RuntimeException throwOnMatch) {
        checkParamsNotNull(supplier, expect, throwOnMatch);
        try {
            return supplier.get();
        } catch (RuntimeException original) {
            Throwable cause = original;
            while (cause != null) {
                if (expect.isInstance(cause)) {
                    throw throwOnMatch;
                }
                cause = cause.getCause();
            }
            throw original;
        }
    }

    private ExceptionUtils() {
    }
}
