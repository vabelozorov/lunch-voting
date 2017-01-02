package ua.belozorov.lunchvoting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotFoundException extends RuntimeException {
    private static final String NOT_FOUND = "%s with id %s not found";
    public NotFoundException(AbstractPersistableObject obj) {
        this(obj.getId(), obj.getClass());
    }

    public NotFoundException(String id, Class<?> clazz) {
        super(String.format(NOT_FOUND, clazz, id));
    }

    public NotFoundException(Collection<String> ids, Class<?> clazz) {
        super(String.format(NOT_FOUND, clazz, ids));
    }
}
