package ua.belozorov.lunchvoting.exceptions;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 25.11.16.
 */
public class BadSyntaxException extends RuntimeException {
    private static final String ID_EXISTS = "Must not specify ID when creating an node";

    public BadSyntaxException(ErrorCode code) {
        super(code.getMessage());
    }

    public enum ErrorCode {
        ID_EXISTS(BadSyntaxException.ID_EXISTS);
        private final String message;

        ErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
