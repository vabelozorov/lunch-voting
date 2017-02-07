package ua.belozorov.lunchvoting.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
public final class ControllerUtils {
    private ControllerUtils() {
    }


    public static Map<String, Object> toMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
