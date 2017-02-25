package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.util.Pair;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This exception is thrown when a Spring BeanFactoryPostProcessor finds a class annotated
 * with {@code RestConroller} and its public methods are not marked with
 * {@link org.springframework.security.access.annotation.Secured}
 * annotation/meta-annotation
 *
 * Created on 22.02.17.
 */
public class InSecuredControllerMethodException extends RuntimeException {
    private final List<Pair<Class<?>, Method>> unSecuredMethods;

    public InSecuredControllerMethodException(List<Pair<Class<?>, Method>> unSecuredMethods) {
        this.unSecuredMethods = unSecuredMethods;
    }

    public String toString() {
        return getClass().getSimpleName() + ": " +
                unSecuredMethods.stream()
                        .map(p -> String.format("[%s : %s]", p.getA(), p.getB()))
                        .collect(Collectors.joining(", "));

    }
}
