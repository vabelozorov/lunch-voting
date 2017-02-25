package ua.belozorov.lunchvoting.model.voting;

import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.model.UserTestData.*;

/**
 * <h2></h2>
 *
 * Created on 09.12.16.
 */
public class PollResultTest extends AbstractTest {

    private final VotingResult<PollItem> resultPerItem = new PollVoteResult<>(testPolls.getActivePoll(), Vote::getPollItem);

    @Test
    public void getNumberOfVotesByPollItem() throws Exception {
        assertTrue(resultPerItem.countPerItem().get(testPolls.getActivePollPollItem1()) == 3);
        assertTrue(resultPerItem.countPerItem().get(testPolls.getActivePollPollItem2()) == 2);
    }

    @Test
    public void getNumberOfVotesPerVoterId() throws Exception {
        VotingResult<String> resultPerVoterId = new PollVoteResult<>(testPolls.getActivePoll(), Vote::getVoterId);

        assertTrue(resultPerVoterId.countPerItem().get(VOTER_ID) == 1);
        assertTrue(resultPerVoterId.countPerItem().get(VOTER1_ID) == 1);
    }

    @Test
    public void getWinners() throws Exception {
        List<PollItem> winners = Arrays.asList(testPolls.getActivePollPollItem1());

        assertTrue(resultPerItem.getWinners().equals(winners));
    }

    @Test
    public void getVotesMadeForItem() throws Exception {
        Set<Vote> expected = testVotes.getVotesForActivePoll();
        List<Vote> actual = resultPerItem.votesForItem(testPolls.getActivePollPollItem1());

        assertTrue(expected.equals(new HashSet<>(expected)));
    }
}