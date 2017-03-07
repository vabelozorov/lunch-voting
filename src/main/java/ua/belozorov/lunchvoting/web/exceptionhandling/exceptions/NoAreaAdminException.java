package ua.belozorov.lunchvoting.web.exceptionhandling.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**

 *
 * Created on 15.02.17.
 */
public class NoAreaAdminException extends RuntimeException implements ApplicationException {
    private final ErrorCode code = ErrorCode.AREA_HAS_ONE_ADMIN;

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
        return new Object[0];
    }
}
