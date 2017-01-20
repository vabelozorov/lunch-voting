package ua.belozorov.lunchvoting.model.voting;

import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
//TODO Looks a little complicated
public class PollingResultTest extends AbstractTest {
    private final List<LunchPlace> places = getWithFilteredMenu(LocalDate.now(), testPlaces.getPlace3(), testPlaces.getPlace4());

    @Test
    public void testPollingResult() throws Exception {

        VoteCollector collector = new PollVoteCollector(testPolls.getActivePoll());
        collector.collect(testVotes.getVotesForActivePoll());

        VoteStatistics<PollItem> result1 = collector.result(VoteCollector.pollItemClassifier());
        assertTrue(result1.countPerItem().get(testPolls.getActivePollPollItem1()) == 3);
        assertTrue(result1.countPerItem().get(testPolls.getActivePollPollItem2()) == 2);

        VoteStatistics<String> result2 = collector.result(VoteCollector.voterIdClassifier());
        assertTrue(result2.countPerItem().get(VOTER_ID) == 1);
        assertTrue(result2.countPerItem().get(VOTER1_ID) == 1);
    }

    @Test
    public void testInstanceOfNull() throws Exception {
        Object possiblyNull = null;
        System.out.println(null instanceof Object);
    }
}