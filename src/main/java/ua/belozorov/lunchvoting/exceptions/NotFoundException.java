package ua.belozorov.lunchvoting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * Created on 17.11.16.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotFoundException extends RuntimeException implements ApplicationException{
    private static final String NOT_FOUND = "%s with id %s not found";
    private final ErrorCode code;
    private Object[] args;

    public NotFoundException(AbstractPersistableObject obj) {
        this(obj.getId(), obj.getClass());
    }

    public NotFoundException(String id, Class<?> clazz) {
        super(String.format(NOT_FOUND, clazz, id));
        this.code = ErrorCode.ENTITY_NOT_FOUND;
        this.args = new Object[]{id};
    }

    public NotFoundException(Collection<String> ids, Class<?> clazz) {
        super(String.format(NOT_FOUND, clazz, ids));
        this.code = ErrorCode.ENTITY_NOT_FOUND;
        this.args = new Object[]{ids};
    }

    @Override
    public String getStringErrorCode() {
        return code.getMessageCode();
    }

    @Override
    public ErrorCode getErrorCode() {
        return code;
    }

    @Override
    public Object[] getArgs() {
        return this.args;
    }
}
