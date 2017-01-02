package ua.belozorov.lunchvoting.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.monitorjbl.json.DefaultView;
import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonViewSupportFactoryBean;
import com.monitorjbl.json.Match;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ua.belozorov.lunchvoting.DateTimeFormatters;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.ConfiguredObjectMapper;

import java.util.List;

/**
 * Created by vabelozorov on 14.11.16.
 */
@Configuration
@EnableWebMvc
@ComponentScan({"ua.belozorov.lunchvoting.web"})
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        return builder
                .indentOutput(true).modulesToInstall(new JavaTimeModule(), new Hibernate5Module())
                .autoDetectGettersSetters(false)
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false).build()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
    }

    @Bean
    public JsonViewSupportFactoryBean views() {
        return new JsonViewSupportFactoryBean(
                objectMapper()
        );
    }

    @Bean
    public JsonResult json() {
        return JsonResult.instance();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateTimeFormatters.LocalDateFormatter());
    }
}
