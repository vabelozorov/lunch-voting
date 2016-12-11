package ua.belozorov.lunchvoting.model.voting;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACES;
import static ua.belozorov.lunchvoting.testdata.UserTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
//TODO Split
public class PollingResultTest {

    @Test
    public void testPollingResult() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        Poll poll = new Poll(start, end, start, PLACES);
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        String firstPollItemId = iterator.next().getId();
        String secondPollItemId = iterator.next().getId();
        String [] itemIds = new String[] {firstPollItemId, secondPollItemId};

        VoteCollector collector = new PollVoteCollector(poll);
        for (int i = 0; i < VOTERS.size(); i++) {
            VoteIntention intention = new VoteIntention(VOTERS.get(i).getId(), poll.getId(), itemIds[i & 1], null);
            Vote vote = poll.verify(intention).getVote();
            collector.collect(vote);
        }

        VoteStatistics<PollItem> result1 = collector.result(VoteCollector.pollItemClassifier());
        assertTrue(result1.countPerItem().get(poll.pollItemById(firstPollItemId)) == 3);
        assertTrue(result1.countPerItem().get(poll.pollItemById(secondPollItemId)) == 2);

        VoteStatistics<String> result2 = collector.result(VoteCollector.voterIdClassifier());
        assertTrue(result2.countPerItem().get(VOTER_ID) == 1);
        assertTrue(result2.countPerItem().get(VOTER1_ID) == 1);
    }
}