package ua.belozorov.lunchvoting.exceptions;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
public class NotFoundException extends RuntimeException {
    private static final String NOT_FOUND = "%s with id %s not found";
    public NotFoundException(Object id, Class<?> clazz) {
        super(String.format(NOT_FOUND, clazz.getName(), id));
    }
}
