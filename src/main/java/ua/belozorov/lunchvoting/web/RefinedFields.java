package ua.belozorov.lunchvoting.web;

import java.util.Set;

/**
 * Given a set of object fields that a user wishes to include in a JSON response,
 * an instance makes mandatory removal, addition or replacement of predefined fields
 *
 * Created on 31.12.16.
 */
public interface RefinedFields {

    /**
     * @return Returns a set of object fields as String objects after refining
     */
    Set<String> get();

    /**
     *
     * @param field
     * @return True if a given field was present in the set of fields initially passed to the instance,
     * otherwise false
     */
    boolean containsOriginal(String field);
}
