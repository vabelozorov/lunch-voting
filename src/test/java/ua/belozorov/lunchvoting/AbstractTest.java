package ua.belozorov.lunchvoting;

import org.junit.runner.RunWith;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.lunchvoting.model.voting.PollTestData;



/**
 * <h2>Base class for all tests. Provides access to a test data. Intended subclasses are model classes</h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public abstract class AbstractTest {

    protected final LunchPlaceTestData placeTestData = new LunchPlaceTestData();
    protected final PollTestData pollTestData = new PollTestData(placeTestData);

    protected final ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            placeTestData.getMenuSqlResource(),
            pollTestData.getPollSqlResource()
    );

}
