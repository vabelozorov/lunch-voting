package ua.belozorov.lunchvoting.service;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.config.RootConfig;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
@ContextConfiguration(classes = {RootConfig.class})
public abstract class AbstractServiceTest extends AbstractTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
}