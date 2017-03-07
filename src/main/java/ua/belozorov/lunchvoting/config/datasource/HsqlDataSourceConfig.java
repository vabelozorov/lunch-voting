package ua.belozorov.lunchvoting.config.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ua.belozorov.lunchvoting.SPRING_PROFILES;

import javax.sql.DataSource;

/**
 * Created on 27.02.17.
 */
@Configuration
@PropertySource("classpath:db/hsql.properties")
@Profile({SPRING_PROFILES.DB.HSQL})
public class HsqlDataSourceConfig {

    @Autowired
    private Environment env;

    @Bean("dataSource")
    @Profile({"!" + SPRING_PROFILES.SQL_DEBUG})
    public DataSource hsqldbDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(env.getProperty("database.driverClassName"));
        dataSource.setUrl(env.getProperty("database.url"));
        dataSource.setUsername(env.getProperty("database.username"));
        dataSource.setPassword(env.getProperty("database.password"));
        return dataSource;
    }

    @Bean("p6DataSource")
    @Profile({SPRING_PROFILES.SQL_DEBUG})
    public DataSource p6spyDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(env.getProperty("database.p6spy.driverClassName"));
        dataSource.setUrl(env.getProperty("database.p6spy.url"));
        dataSource.setUsername(env.getProperty("database.username"));
        dataSource.setPassword(env.getProperty("database.password"));
        return dataSource;
    }

}
