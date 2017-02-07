package ua.belozorov.lunchvoting.web;

import java.util.Map;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 01.02.17.
 */
public interface JsonFilter {
    void includingFilter(Object object, RefinedFields fields);

    void excludingFilter(Object object, Map<Class<?>, Set<String>> filterMap);
}