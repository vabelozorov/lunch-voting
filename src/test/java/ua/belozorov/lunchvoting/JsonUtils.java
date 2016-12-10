package ua.belozorov.lunchvoting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.util.ConfiguredObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 18.11.16.
 */
public class JsonUtils {
    private static ObjectMapper mapper = ConfiguredObjectMapper.getMapper();

    public static String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public static String toJson(Object object, Map<String, String> additionalProperties) throws IOException {
        String jsonString = mapper.writeValueAsString(object);
        ObjectNode jsonNode = (ObjectNode) mapper.readTree(jsonString);
        for (Map.Entry<String, String> me : additionalProperties.entrySet()) {
            jsonNode.put(me.getKey(), me.getValue());
        }
        return jsonNode.toString();
    }

    public static String toJson(Object object, String key, String value) throws IOException {
        Map<String, String> properties = new HashMap<>();
        properties.put(key, value);
        return toJson(object, properties);
    }

    public static String toJson(String key, String value) throws IOException {
        return toJson(new Object(), key, value);
    }

    public static <T> T mvcResultToObject(MvcResult result, Class<T> responseType) throws IOException {
        return strToObject(result.getResponse().getContentAsString(), responseType);
    }

    public static <T> T mvcResultToObject(MvcResult result, TypeReference<T> ref) throws IOException {
        return mapper.readValue(result.getResponse().getContentAsString(), ref);
    }

    public static <T> T strToObject(String jsonString, Class<T> responseType) throws IOException {
        return mapper.readValue(jsonString, responseType);
    }
}
