package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
public interface ApplicationException {
    String getStringErrorCode();
    ErrorCode getErrorCode();
    Object[] getArgs();
}
