package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.Code;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
public interface ApplicationException {
    Code getCode();
    String getErrorMessage();
}
