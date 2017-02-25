package ua.belozorov.lunchvoting.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * Provides several helper methods to deal with exceptions.
 *
 * @author vabelozorov on 12.01.17.
 */
public final class ExceptionUtils {
    public static final Object NOT_CHECK = new Object();

    /*
        Checks that a given collection is not null and not empty and throws RuntimeException 'ex' if otherwise.
     */
    public static <T> Collection<T> requireNonNullNotEmpty(Collection<T> collection,
                                                           Supplier<? extends RuntimeException> ex) {
        return ofNullable(collection)
                .filter(c -> ! c.isEmpty())
                .orElseThrow(ex);
    }

    /*
        Checks a supplied array of objects and if one of them is null, throws a NullPointerException
        specifying the position of the first found null object.
     */
    public static void checkParamsNotNull(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                throw new NullPointerException("Null param is not allowed, but found at position " + i);
            }
        }
    }

    /*
        Executes a 'supplier' and tries to catch a RuntimeException.
        If catches, recursively examines it and its cause, trying to find an instance of 'expect'.
        If such instance were found, thrown a supplied 'thrownOnMatch' exception, if not - rethrows original exception.
        If no RuntimeException was thrown during 'supplier' execution, just returns the result of its invocation.
     */
    public static <T> T executeAndUnwrapException(Supplier<T> supplier, Class<? extends RuntimeException> expect, RuntimeException throwOnMatch) {
        checkParamsNotNull(supplier, expect, throwOnMatch);
        try {
            return supplier.get();
        } catch (RuntimeException original) {
            int maxCycles = 50; // to prevent an infinite loop, just in case
            Throwable cause = original;
            while (cause != null && maxCycles > 0) {
                if (expect.isInstance(cause)) {
                    throw throwOnMatch;
                }
                cause = cause.getCause();
                maxCycles--;
            }
            throw original;
        }
    }

    private ExceptionUtils() {
    }
}
