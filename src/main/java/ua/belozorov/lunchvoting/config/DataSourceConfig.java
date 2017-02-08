package ua.belozorov.lunchvoting.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ua.belozorov.lunchvoting.SPRING_PROFILES;

import javax.sql.DataSource;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
@PropertySource("classpath:db/db-connection.properties")
public class DataSourceConfig {
    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    @Profile({SPRING_PROFILES.DB_PRODUCTION})
    public DataSource postgresDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(env.getProperty("database.postgres.driverClassName"));
        dataSource.setUrl(env.getProperty("database.postgres.url"));
        dataSource.setUsername(env.getProperty("database.username"));
        dataSource.setPassword(env.getProperty("database.password"));
        return dataSource;
    }

    @Bean(name = "dataSource")
    @Profile(SPRING_PROFILES.DB_P6SPY)
    public DataSource p6spyDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(env.getProperty("database.p6spy.driverClassName"));
        dataSource.setUrl(env.getProperty("database.p6spy.url"));
        dataSource.setUsername(env.getProperty("database.username"));
        dataSource.setPassword(env.getProperty("database.password"));
        return dataSource;
    }

    @Bean(name = "dataSource")
    @Primary
    @Profile(SPRING_PROFILES.DB_PROXY)
    public DataSource proxyDataSource() {
        return ProxyDataSourceBuilder
                .create(p6spyDataSource())
//                .logQueryToSysOut()
                .countQuery()
                .build();
    }
}
