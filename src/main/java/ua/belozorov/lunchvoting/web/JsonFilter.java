package ua.belozorov.lunchvoting.web;

import java.util.Map;
import java.util.Set;

/**
 * An interface for classes that support filtering object properties
 * before encoding such object into a JSON format
 *
 * Created on 01.02.17.
 */
interface JsonFilter {
    void includingFilter(Object object, RefinedFields fields);

    void excludingFilter(Object object, Map<Class<?>, Set<String>> filterMap);
}