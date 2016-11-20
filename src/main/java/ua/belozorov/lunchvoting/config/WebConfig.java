package ua.belozorov.lunchvoting.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ua.belozorov.lunchvoting.util.ConfiguredObjectMapper;

import java.util.List;

/**
 * Created by vabelozorov on 14.11.16.
 */
@Configuration
@EnableWebMvc
@ComponentScan({"ua.belozorov.lunchvoting.web"})
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(ConfiguredObjectMapper.getMapper()));
    }

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
////        Jackson2ObjectMapperBuilder builder = jackson2ObjectMapperBuilder();
//        converters.add(new MappingJackson2HttpMessageConverter(ConfiguredObjectMapper.getMapper()));
//    }
}
