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
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * <h2>Base class for all tests that require Spring framework components</h2>
 *
 * @author vabelozorov on 14.01.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(SPRING_PROFILES.JUNIT_TEST)
@Sql(scripts = "classpath:db/populate_postgres.sql", config = @SqlConfig(encoding = "UTF-8"))
public abstract class AbstractSpringTest extends AbstractTest {
    @Autowired
    protected PlatformTransactionManager ptm;

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private DataSource dataSource;

    private final ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            testUsers.getUserSqlResource(),
            testPlaces.getLunchPlaceSqlResource(),
            testPlaces.getMenuSqlResource(),
            testPolls.getPollSqlResource(),
            testPolls.getPollItemSqlResource(),
            testVotes.getVoteSqlResource()
    );

    @Before
    public void beforeTest() {
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
