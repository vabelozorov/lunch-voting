package ua.belozorov.lunchvoting.misc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.util.ConfiguredObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**

 *
 * Created on 18.11.16.
 */
public class JsonUtils {
    private ObjectMapper mapper;

    public JsonUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public String toJson(Object object, Map<String, String> additionalProperties) throws IOException {
        String jsonString = mapper.writeValueAsString(object);
        ObjectNode jsonNode = (ObjectNode) mapper.readTree(jsonString);
        for (Map.Entry<String, String> me : additionalProperties.entrySet()) {
            jsonNode.put(me.getKey(), me.getValue());
        }
        return jsonNode.toString();
    }

    public String toJson(Object object, String key, String value) throws IOException {
        Map<String, String> properties = new HashMap<>();
        properties.put(key, value);
        return this.toJson(object, properties);
    }

    public <T> T strToObject(String jsonString, Class<T> responseType) throws IOException {
        return mapper.readValue(jsonString, responseType);
    }

    public String locationFromMvcResult(MvcResult result) {
        return result.getResponse().getHeader("Location");
    }
}
