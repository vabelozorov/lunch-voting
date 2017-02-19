package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 18.02.17.
 */
public class VotePolicyException extends RuntimeException implements ApplicationException {
    private final ErrorCode code;

    public VotePolicyException(ErrorCode code) {
        this.code = code;
    }

    @Override
    public ErrorCode getErrorCode() {
        return code;
    }
}
