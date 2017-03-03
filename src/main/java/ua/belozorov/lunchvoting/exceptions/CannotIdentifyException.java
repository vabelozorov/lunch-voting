package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**
 * Created on 02.03.17.
 */
public class CannotIdentifyException extends RuntimeException implements ApplicationException{
    private final ErrorCode code;

    public CannotIdentifyException(ErrorCode code) {
        this.code = code;
    }

    @Override
    public ErrorCode getErrorCode() {
        return this.code;
    }
}
