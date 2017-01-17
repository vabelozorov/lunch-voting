package ua.belozorov.lunchvoting;

import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.lunchvoting.model.voting.PollTestData;
import ua.belozorov.lunchvoting.model.voting.VoteTestData;
import ua.belozorov.lunchvoting.testdata.UserTestData;

import java.time.format.DateTimeFormatter;


/**
 * <h2>Base class for all tests. Provides access to a test data. Intended subclasses are model classes</h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public abstract class AbstractTest {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected final UserTestData testUsers = new UserTestData();
    protected final LunchPlaceTestData testPlaces = new LunchPlaceTestData();
    protected final PollTestData testPolls = new PollTestData(testPlaces);
    protected final VoteTestData testVotes = new VoteTestData(testPolls);

    protected final ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            testUsers.getUserSqlResource(),
            testPlaces.getLunchPlaceSqlResource(),
            testPlaces.getMenuSqlResource(),
            testPolls.getPollSqlResource(),
            testPolls.getPollItemSqlResource(),
            testVotes.getVoteSqlResource()
    );

}
