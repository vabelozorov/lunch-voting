package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.web.exceptionhandling.Code;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 10.02.17.
 */

//NOT USED!!!!!!!!
public class MultipleAreaMembershipForbiddenException extends RuntimeException  implements ApplicationException{
    private final Code code;

    public MultipleAreaMembershipForbiddenException() {
        super("Multiple membership is not allowed");
        this.code = Code.MULTIPLE_AREA_MEMBERSHIP;
    }

    @Override
    public Code getCode() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return this.getMessage();
    }
}
