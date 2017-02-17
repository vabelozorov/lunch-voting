package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 10.02.17.
 */

//NOT USED!!!!!!!!
public class MultipleAreaMembershipForbiddenException extends RuntimeException  implements ApplicationException{
    @Override
    public String getStringErrorCode() {
        return null;
    }

    @Override
    public ErrorCode getErrorCode() {
        return null;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }
//    private final ErrorCode code;
//
//    public MultipleAreaMembershipForbiddenException() {
//        super("Multiple membership is not allowed");
//        this.code = ErrorCode.MULTIPLE_AREA_MEMBERSHIP;
//    }
}
