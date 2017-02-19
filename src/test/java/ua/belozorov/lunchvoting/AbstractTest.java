package ua.belozorov.lunchvoting;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ua.belozorov.lunchvoting.model.lunchplace.AreaTestData;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.lunchvoting.model.voting.polling.PollTestData;
import ua.belozorov.lunchvoting.model.voting.polling.VoteTestData;
import ua.belozorov.lunchvoting.model.UserTestData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;


/**
 * <h2>Base class for all tests. Provides access to a test data. Intended subclasses are model classes</h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public abstract class AbstractTest {
    public static final LocalDateTime NOW_DATE_TIME = LocalDateTime.now().withNano(0);
    public static final LocalDate NOW_DATE = LocalDate.now();

    protected final UserTestData testUsers = new UserTestData();
    protected final LunchPlaceTestData testPlaces = new LunchPlaceTestData();
    protected final PollTestData testPolls = new PollTestData(testPlaces);
    protected final VoteTestData testVotes = new VoteTestData(testPolls);
    protected final AreaTestData testAreas= new AreaTestData(testPolls, testPlaces);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public <T> void  assertExceptionCount(Class<T> exClass, int count, Supplier... suppliers) {
        List<T> exceptions = new ArrayList<>();
        for(Supplier supplier : suppliers) {
            try {
                supplier.get();
            } catch (Exception ex) {
                if (exClass.isInstance(ex)) {
                    exceptions.add((T)ex);
                } else {
                    throw ex;
                }
            }
        }
        assertTrue(exceptions.size() == count);
    }

}
