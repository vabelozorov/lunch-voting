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

/**
 * <h2></h2>
 *
 * @author vabelozorov on 16.11.16.
 */
@Configuration
@Profile(SPRING_PROFILES.DEVELOPMENT)
public class InitDatabaseConfig {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        if (env.getProperty("database.init").equals("true")) {
            databasePopulator.addScript(new ClassPathResource(env.getProperty("database.initLocation")));
        }
        if (env.getProperty("database.populate").equals("true")) {
            databasePopulator.addScript(new ClassPathResource(env.getProperty("database.dataLocation")));
        }
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }
}
