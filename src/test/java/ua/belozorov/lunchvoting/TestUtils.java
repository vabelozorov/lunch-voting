package ua.belozorov.lunchvoting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.util.ConfiguredObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 18.11.16.
 */
public class TestUtils {
    private static final ObjectMapper mapper = ConfiguredObjectMapper.getMapper();
//    private static final ObjectMapper mapper = new ObjectMapper();

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

    public static <T> T mvcResultToObject(MvcResult result, Class<T> responseType) throws IOException {
        return mapper.readValue(result.getResponse().getContentAsString(), responseType);
    }
}
