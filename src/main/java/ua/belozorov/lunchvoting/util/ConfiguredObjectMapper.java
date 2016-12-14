package ua.belozorov.lunchvoting.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 18.11.16.
 */
public class ConfiguredObjectMapper extends ObjectMapper {
    private static final ObjectMapper mapper = new ConfiguredObjectMapper();

    private ConfiguredObjectMapper() {
        registerModule(new JavaTimeModule());
        registerModule(new Hibernate5Module());
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
