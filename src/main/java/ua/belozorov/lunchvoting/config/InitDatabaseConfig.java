package ua.belozorov.lunchvoting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import ua.belozorov.lunchvoting.SPRING_PROFILES;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Objects;

/**

 *
 * Created on 16.11.16.
 */
@Configuration
public class InitDatabaseConfig {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        String initLocation = env.getProperty("database.initLocation");
        if ( ! Objects.equals(initLocation, null)) {
            databasePopulator.addScript(new ClassPathResource(initLocation));
        }
        String dataLocation = env.getProperty("database.dataLocation");
        if ( ! Objects.equals(dataLocation, null)) {
            databasePopulator.addScript(new ClassPathResource(dataLocation));
        }
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }
}
