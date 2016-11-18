package ua.belozorov.lunchvoting;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import ua.belozorov.lunchvoting.config.RootConfig;
import ua.belozorov.lunchvoting.config.WebConfig;

/**
 * Created by vabelozorov on 14.11.16.
 */

public class LunchVotingApplication extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
}
