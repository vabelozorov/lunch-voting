package ua.belozorov.lunchvoting.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import ua.belozorov.lunchvoting.config.datasource.DataSourceConfig;
import ua.belozorov.lunchvoting.util.SecurityEnforcerBeanFactoryPostProcessor;
import ua.belozorov.lunchvoting.web.security.RestAuthenticationEntryPoint;


/**
 * Created by vabelozorov on 14.11.16.
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.belozorov.lunchvoting.repository"
})
@Import({JpaConfig.class, InitDatabaseConfig.class, DataSourceConfig.class})
public class RootConfig {

    @Bean
    public static BeanFactoryPostProcessor securityEnforcer() {
        return new SecurityEnforcerBeanFactoryPostProcessor();
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

//    @Bean
//    public static ApplicationListener printBeans() {
//        return new PrintBeans();
//    }
}
