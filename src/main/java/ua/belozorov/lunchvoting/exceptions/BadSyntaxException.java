package ua.belozorov.lunchvoting.exceptions;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 25.11.16.
 */
public class BadSyntaxException extends RuntimeException {
    private static final String ID_EXISTS = "Must not specify ID when creating an node";
    private static final String LUNCHPLACE_ID_MISSING = "LunchPlace ID cannot be null";

    public BadSyntaxException(ErrorCode code) {
        super(code.getMessage());
    }

    public BadSyntaxException() {
    }

    public enum ErrorCode {
        ID_EXISTS(BadSyntaxException.ID_EXISTS),
        LUNCHPLACE_ID_MISSING(BadSyntaxException.LUNCHPLACE_ID_MISSING);

        private final String message;

        ErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
