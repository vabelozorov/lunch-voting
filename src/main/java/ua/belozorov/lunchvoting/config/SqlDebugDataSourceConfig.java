package ua.belozorov.lunchvoting.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ua.belozorov.lunchvoting.SPRING_PROFILES;

import javax.sql.DataSource;

/**
 * Created on 27.02.17.
 */
@Configuration
@Profile(SPRING_PROFILES.SQL_DEBUG)
public class SqlDebugDataSourceConfig {

    @Bean(name = "dataSource")
    public DataSource proxyDataSource(@Qualifier("p6DataSource") DataSource p6DataSource) {
        return ProxyDataSourceBuilder
                .create(p6DataSource)
                .countQuery()
                .build();
    }
}
