package ua.belozorov.lunchvoting.exceptions;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.01.17.
 */
public class MenuDateMismatchException extends RuntimeException {
    public MenuDateMismatchException() {
        super("Poll found a menu with an unexpected date");
    }
}
