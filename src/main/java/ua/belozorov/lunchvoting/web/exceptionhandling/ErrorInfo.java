package ua.belozorov.lunchvoting.web.exceptionhandling;

import ua.belozorov.lunchvoting.exceptions.ApplicationException;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
public class ErrorInfo {
    private final CharSequence url;
    private final Code code;
    private final String message;

    public ErrorInfo(CharSequence url, ApplicationException ex) {
        this.url = url;
        this.code = ex.getCode();
        this.message = ex.getMessage();
    }

    public ErrorInfo(CharSequence url, Code code, String message) {
        this.url = url;
        this.code = code;
        this.message = message;
    }
}
