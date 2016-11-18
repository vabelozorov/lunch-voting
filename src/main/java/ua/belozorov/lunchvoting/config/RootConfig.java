package ua.belozorov.lunchvoting.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;

/**
 * Created by vabelozorov on 14.11.16.
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.belozorov.lunchvoting.service",
        "ua.belozorov.lunchvoting.repository"
})
@PropertySource("classpath:db/postgres.properties")
@Import({DbConfig.class, InitDatabaseConfig.class})
public class RootConfig {

}
