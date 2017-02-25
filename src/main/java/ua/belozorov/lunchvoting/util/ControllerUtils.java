package ua.belozorov.lunchvoting.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <h2></h2>
 *
 * Created on 07.02.17.
 */
public final class ControllerUtils {
    private ControllerUtils() {
    }

    public static Map<String, Object> toMap(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

}
