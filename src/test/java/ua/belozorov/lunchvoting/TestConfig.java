package ua.belozorov.lunchvoting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.belozorov.lunchvoting.misc.JsonUtils;

/**

 *
 * Created on 14.12.16.
 */
@Configuration
public class TestConfig {
    @Bean
    public JsonUtils jsonUtils(ObjectMapper objectMapper) {
        return new JsonUtils(objectMapper);
    }
}
