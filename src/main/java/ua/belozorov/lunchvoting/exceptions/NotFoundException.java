package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
public class NotFoundException extends RuntimeException {
    private static final String NOT_FOUND = "%s with id %s not found";
    public NotFoundException(AbstractPersistableObject obj) {
        this(obj.getId(), obj.getClass());
    }

    public NotFoundException(String id, Class<?> clazz) {
        super(String.format(NOT_FOUND, id, clazz));
    }
}
