package ua.belozorov.lunchvoting;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
//TODO Implement validator
public @interface LengthEach {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
}
