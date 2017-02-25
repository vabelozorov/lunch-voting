package ua.belozorov.lunchvoting;

/**
 * <h2></h2>
 *
 * Created on 04.02.17.
 */
public interface EqualsComparator<T> {
    boolean compare(T obj, T another);
}
