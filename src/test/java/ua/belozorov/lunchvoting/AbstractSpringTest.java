package ua.belozorov.lunchvoting;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import ua.belozorov.lunchvoting.config.RootConfig;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.model.UserTestData.GOD_ID;

/**
 * Base class for all tests that require Spring framework components
 *
 * Created on 14.01.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(SPRING_PROFILES.DB_PROXY)
@ContextConfiguration(classes = {RootConfig.class})
public abstract class AbstractSpringTest extends AbstractTest {

    @Autowired
    protected PlatformTransactionManager ptm;

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected DataSource dataSource;

}
