package ua.belozorov.lunchvoting;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 04.02.17.
 */
public interface EqualsComparator<T> {
    boolean compare(T obj, T another);
}
