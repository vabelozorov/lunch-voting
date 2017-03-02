package ua.belozorov.lunchvoting.config;

import org.springframework.context.annotation.*;

/**
 *
 * Created on 08.02.17.
 */
@Import({PostgresDataSourceConfig.class, HsqlDataSourceConfig.class, SqlDebugDataSourceConfig.class})
public class DataSourceConfig {
}
