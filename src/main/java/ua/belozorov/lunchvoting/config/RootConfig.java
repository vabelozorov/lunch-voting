package ua.belozorov.lunchvoting.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import ua.belozorov.lunchvoting.util.PrintBeans;
import ua.belozorov.lunchvoting.util.SecurityEnforcerBeanFactoryPostProcessor;


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

//    @Bean
//    public static ApplicationListener printBeans() {
//        return new PrintBeans();
//    }
}
