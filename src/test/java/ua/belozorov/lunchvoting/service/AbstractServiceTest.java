package ua.belozorov.lunchvoting.service;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import ua.belozorov.lunchvoting.AbstractSpringTest;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.config.RootConfig;
import ua.belozorov.lunchvoting.config.ServiceBeansConfig;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
@ContextConfiguration(classes = ServiceBeansConfig.class)
public abstract class AbstractServiceTest extends AbstractSpringTest {

    private final ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            testAreas.getAreaSqlResource(),
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