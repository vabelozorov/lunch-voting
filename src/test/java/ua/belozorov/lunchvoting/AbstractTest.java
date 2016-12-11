package ua.belozorov.lunchvoting;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.belozorov.lunchvoting.model.voting.PollTestData;
import ua.belozorov.lunchvoting.testdata.MenuTestData;

import javax.sql.DataSource;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 02.12.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(SPRING_PROFILES.JUNIT_TEST)
@Sql(scripts = "classpath:db/populate_postgres.sql", config = @SqlConfig(encoding = "UTF-8"))
public abstract class AbstractTest {

    @Autowired
    protected DataSource dataSource;

    private ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            MenuTestData.MENU_SQL_RESOURCE,
            PollTestData.POLL_SQL_RESOURCE
    );

    @Before
    public void beforeTest() {
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
