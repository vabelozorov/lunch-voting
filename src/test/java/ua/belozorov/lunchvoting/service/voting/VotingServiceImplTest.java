package ua.belozorov.lunchvoting.service.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.voting.VotingResult;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.util.*;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.voting.polling.VoteTestData.VOTE_COMPARATOR;
import static ua.belozorov.lunchvoting.model.UserTestData.GOD_ID;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 04.01.17.
 */
public class VotingServiceImplTest extends AbstractServiceTest {

    @Autowired
    private VotingService votingService;

    @Autowired
    private PollRepository repository;


    @Test
    public void votesFirstTime() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        PollItem item = testPolls.getActivePollPollItem1();
        Vote returned = votingService.vote(GOD_ID, poll.getId(), item.getId());
        Vote actual = repository.getVoteInPoll(GOD_ID, poll.getId());
        assertThat(actual, matchSingle(returned, VOTE_COMPARATOR));
    }

    @Test
    public void updatesVoteBeforeTimeThreshold() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        PollItem item1 = testPolls.getActivePollPollItem1();
        PollItem item2 = testPolls.getActivePollPollItem2();

        Vote firstVote = votingService.vote(GOD_ID, poll.getId(), item1.getId());
        Vote actual = repository.getVoteInPoll(GOD_ID, poll.getId());
        assertTrue(firstVote.getId().equals(actual.getId()));

        Thread.sleep(100);
        Vote secondVote = votingService.vote(GOD_ID, poll.getId(), item2.getId());
        assertTrue(secondVote.getId().equals(
                repository.getVoteInPoll(GOD_ID, poll.getId())
                                .getId()
        ));
    }

    @Test
    public void getVotedByVoterId() {
        Collection<String> ids = votingService.getVotedByVoter(testPolls.getActivePoll().getId(), VOTER_ID);
        List<String> expected = Arrays.asList(testPolls.getActivePoll().getPollItems().get(0).getId());

        assertTrue(ids.equals(expected));
    }

    @Test
    public void getPollResult() throws Exception {
        VotingResult<PollItem> pollResult = votingService.getPollResult(testPolls.getActivePoll().getId());
        PollItem pollItem1 = testPolls.getActivePollPollItem1();

        assertTrue(pollResult.getWinners().equals(Arrays.asList(pollItem1)));
        assertTrue(pollResult.countPerItem().get(pollItem1) == 3);
        assertTrue(pollResult.countPerItem().get(testPolls.getActivePollPollItem2()) == 2);
    }
}