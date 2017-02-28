package ua.belozorov.lunchvoting;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import ua.belozorov.lunchvoting.config.RootConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Base class for all tests that require Spring framework components
 *
 * Created on 14.01.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({SPRING_PROFILES.DB.HSQL, SPRING_PROFILES.SQL_DEBUG})
@ContextConfiguration(classes = {RootConfig.class})
public abstract class AbstractSpringTest extends AbstractTest {

    @Autowired
    protected PlatformTransactionManager ptm;

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected DataSource dataSource;

}
