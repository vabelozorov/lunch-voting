package ua.belozorov.lunchvoting.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ua.belozorov.lunchvoting.util.ConfiguredObjectMapper;

/**
 * Created by vabelozorov on 14.11.16.
 */
@Configuration
@EnableWebMvc
@ComponentScan({"ua.belozorov.lunchvoting.web"})
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
        return ConfiguredObjectMapper.getMapper();
    }
}
