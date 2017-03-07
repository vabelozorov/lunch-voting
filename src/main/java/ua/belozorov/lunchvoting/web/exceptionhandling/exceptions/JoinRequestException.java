package ua.belozorov.lunchvoting.web.exceptionhandling.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**
 * Created on 01.03.17.
 */
public class JoinRequestException extends RuntimeException implements ApplicationException {
    private final ErrorCode code;

    public JoinRequestException(ErrorCode code) {
        this.code = code;
    }

    @Override
    public ErrorCode getErrorCode() {
        return this.code;
    }
}
