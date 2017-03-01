package ua.belozorov.lunchvoting;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import ua.belozorov.lunchvoting.config.RootConfig;
import ua.belozorov.lunchvoting.config.ServiceBeansConfig;
import ua.belozorov.lunchvoting.config.WebConfig;
import ua.belozorov.lunchvoting.config.WebSecurityConfig;

import javax.servlet.Filter;

/**
 * Application Initializer class
 *
 * Created by vabelozorov on 14.11.16.
 */

public class LunchVotingApplication extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfig.class, ServiceBeansConfig.class, WebSecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new HttpPutFormContentFilter()};
    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        WebApplicationContext rootApplicationContext = super.createRootApplicationContext();
        ((ConfigurableEnvironment)rootApplicationContext.getEnvironment()).setActiveProfiles(
                SPRING_PROFILES.DB.HSQL);
        return rootApplicationContext;
    }
}
