package ua.belozorov.lunchvoting.web;

import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.12.16.
 */
interface RefinedFields {
    Set<String> get();
    boolean contains(String field);
}
