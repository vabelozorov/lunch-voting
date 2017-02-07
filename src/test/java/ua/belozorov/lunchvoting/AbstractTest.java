package ua.belozorov.lunchvoting;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.lunchvoting.model.voting.polling.PollTestData;
import ua.belozorov.lunchvoting.model.voting.polling.VoteTestData;
import ua.belozorov.lunchvoting.testdata.UserTestData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * <h2>Base class for all tests. Provides access to a test data. Intended subclasses are model classes</h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public abstract class AbstractTest {
    public static final LocalDateTime NOW_DATE_TIME = LocalDateTime.now();
    public static final LocalDate NOW_DATE = LocalDate.now();

    protected final UserTestData testUsers = new UserTestData();
    protected final LunchPlaceTestData testPlaces = new LunchPlaceTestData();
    protected final PollTestData testPolls = new PollTestData(testPlaces);
    protected final VoteTestData testVotes = new VoteTestData(testPolls);

    @Rule
    public ExpectedException thrown = ExpectedException.none();


}
