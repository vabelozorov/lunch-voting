package ua.belozorov.lunchvoting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.web.exceptionhandling.Code;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotFoundException extends RuntimeException implements ApplicationException{
    private static final String NOT_FOUND = "%s with id %s not found";
    private final Code code;
    private final Object id;

    public NotFoundException(AbstractPersistableObject obj) {
        this(obj.getId(), obj.getClass());
    }

    public NotFoundException(String id, Class<?> clazz) {
        super(String.format(NOT_FOUND, clazz, id));
        this.code = Code.ENTITY_NOT_FOUND;
        this.id = id;
    }

    public NotFoundException(Collection<String> ids, Class<?> clazz) {
        super(String.format(NOT_FOUND, clazz, ids));
        this.code = Code.ENTITY_NOT_FOUND;
        this.id = ids;
    }

    @Override
    public Code getCode() {
        return code;
    }

    @Override
    public String getErrorMessage() {
        return "Entity(-ies) not found: " + this.id;
    }
}
