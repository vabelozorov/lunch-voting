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
    public static <T> Collection<T> requireNonNullNotEmpty(Collection<T> collection) {
        return requireNonNullNotEmpty(
                collection,
                () -> new NullPointerException("Collection must not be null or empty")
        );
    }

    public static <T> Collection<T> requireNonNullNotEmpty(Collection<T> collection,
                                                           Supplier<? extends RuntimeException> ex) {
        return ofNullable(collection)
                .filter(c -> ! c.isEmpty())
                .orElseThrow(ex);
    }

    public static <T> Collection<T> requireNonNullNotEmpty(T[] array) {
        return requireNonNullNotEmpty(Arrays.asList(array));
    }

    public static void checkAllNotNull(Object... objects) {
        requireNonNullNotEmpty(objects);
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                throw new NullPointerException("Null param is not allowed, but found at position " + i);
            }
        }
    }

    private ExceptionUtils() {
    }
}
