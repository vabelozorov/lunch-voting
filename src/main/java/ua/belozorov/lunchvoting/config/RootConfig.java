package ua.belozorov.lunchvoting.config;

import org.springframework.context.annotation.*;


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

}
