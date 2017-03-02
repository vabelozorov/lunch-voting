package ua.belozorov.lunchvoting.web.exceptionhandling;

import ua.belozorov.lunchvoting.exceptions.ApplicationException;

/**

 *
 * Created on 07.02.17.
 */
public class ErrorInfo {
    private final CharSequence url;
    private final ErrorCode code;
    private final String message;

    public ErrorInfo(CharSequence url, ErrorCode code) {
        this.url = url;
        this.code = code;
        this.message = code.name().toLowerCase();
    }

    public ErrorInfo(CharSequence url, ErrorCode code, String message) {
        this.url = url;
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "url=" + url +
                "\n, code=" + code +
                "\n, message='" + message + '\'' +
                '}';
    }
}
