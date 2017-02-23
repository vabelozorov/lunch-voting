package ua.belozorov.lunchvoting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import ua.belozorov.lunchvoting.repository.user.UserRepository;
import ua.belozorov.lunchvoting.service.user.UserServiceImpl;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.12.16.
 */
@Configuration
public class TestConfig {
    @Bean
    public JsonUtils jsonUtils(ObjectMapper objectMapper) {
        return new JsonUtils(objectMapper);
    }
}
