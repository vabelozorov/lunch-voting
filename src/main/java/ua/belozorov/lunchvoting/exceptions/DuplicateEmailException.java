package ua.belozorov.lunchvoting.exceptions;

import org.jetbrains.annotations.NonNls;
import ua.belozorov.lunchvoting.web.exceptionhandling.Code;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
public class DuplicateEmailException extends RuntimeException implements ApplicationException{
    private final Code code;
    public DuplicateEmailException(@NonNls String message) {
        super(message);
        this.code = Code.DUPLICATE_EMAIL;
    }

    public Code getCode() {
        return code;
    }
}
