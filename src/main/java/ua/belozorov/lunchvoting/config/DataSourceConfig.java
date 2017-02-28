package ua.belozorov.lunchvoting.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import ua.belozorov.lunchvoting.SPRING_PROFILES;

import javax.sql.DataSource;

/**
 *
 * Created on 08.02.17.
 */
@Import({PostgresDataSourceConfig.class, HsqlDataSource.class, SqlDebugDataSourceConfig.class})
public class DataSourceConfig {
}
