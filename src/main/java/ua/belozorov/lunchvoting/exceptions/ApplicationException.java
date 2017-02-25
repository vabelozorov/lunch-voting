package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**
 * <h2></h2>
 *
 * Created on 07.02.17.
 */
public interface ApplicationException {

    default String getStringErrorCode() {
        return this.getErrorCode().getMessageCode();
    }

    ErrorCode getErrorCode();

    default Object[] getArgs() {
        return new Object[0];
    }
}
