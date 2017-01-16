package ua.belozorov.lunchvoting.exceptions;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.01.17.
 */
public class LunchPlaceWithoutMenuException extends RuntimeException {
    public LunchPlaceWithoutMenuException() {
        super("LunchPlace without Menu submitted to Poll");
    }
}
