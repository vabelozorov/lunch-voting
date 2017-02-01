package ua.belozorov.lunchvoting.web;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 01.02.17.
 */
public interface JsonFilter {
    void includingFilter(Object object, RefinedFields fields);

    void excludingFilter(Object object, List<String> excludeFields);
}