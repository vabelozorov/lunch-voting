package ua.belozorov.lunchvoting.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.*;
import ua.belozorov.lunchvoting.util.SecurityEnforcerBeanFactoryPostProcessor;


/**
 * Created by vabelozorov on 14.11.16.
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.belozorov.lunchvoting.service",
        "ua.belozorov.lunchvoting.repository"
})
@Import({JpaConfig.class, InitDatabaseConfig.class, DataSourceConfig.class, WebSecurityConfig.class})
public class RootConfig {

    @Bean
    public BeanFactoryPostProcessor securityEnforcer() {
        return new SecurityEnforcerBeanFactoryPostProcessor();
    }
}
