package ua.belozorov.lunchvoting.exceptions;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.01.17.
 */
public class NoPollItemsException extends RuntimeException {
    public NoPollItemsException() {
        super("Poll without PollItems is not allowed");
    }
}
