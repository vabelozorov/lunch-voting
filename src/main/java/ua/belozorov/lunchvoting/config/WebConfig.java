package ua.belozorov.lunchvoting.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonViewSupportFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ua.belozorov.lunchvoting.DateTimeFormatters;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfoFactory;

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
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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
        registry.addFormatter(new DateTimeFormatters.LocalDateTimeFormatter());
    }



    @Bean("messageSource")
    @Primary
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:messages/messages");
        source.setFallbackToSystemLocale(false);
        source.setUseCodeAsDefaultMessage(false);
        return source;
    }

    @Bean("validator")
    @Primary
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public ErrorInfoFactory errorInfoFactory() {
        return new ErrorInfoFactory(messageSource());
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
