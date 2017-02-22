package ua.belozorov.lunchvoting.service.voting;

import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.WithMockVoter;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.voting.VotingResult;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.util.*;
import java.util.stream.Collectors;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.*;
import static ua.belozorov.lunchvoting.model.UserTestData.*;
import static ua.belozorov.lunchvoting.model.voting.polling.VoteTestData.VOTE_COMPARATOR;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 04.01.17.
 */
@WithMockVoter
public class VotingServiceImplTest extends AbstractServiceTest {

    @Autowired
    private VotingService votingService;

    @Autowired
    private PollRepository repository;

    private final String areaId = testAreas.getFirstAreaId();

    @Test
    public void voteFirstTime() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        PollItem item = testPolls.getActivePollPollItem1();

        reset();
        Vote returned = votingService.vote(GOD, poll.getId(), item.getId());
        assertSql(1,1, 0, 0);

        Vote actual = repository.getFullVoteInPoll(areaId, GOD_ID, poll.getId());

        assertThat(actual, matchSingle(returned, VOTE_COMPARATOR));
    }

    @Test
    public void updatesVoteBeforeTimeThreshold() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        PollItem item1 = testPolls.getActivePollPollItem1();
        PollItem item2 = testPolls.getActivePollPollItem2();

        reset();
        Vote firstVote = votingService.vote(GOD, poll.getId(), item1.getId());
        assertSql(1,1, 0, 0);

        Vote actual = repository.getFullVoteInPoll(areaId, GOD_ID, poll.getId());
        assertTrue(firstVote.getId().equals(actual.getId()));

        Thread.sleep(100);
        Vote secondVote = votingService.vote(GOD, poll.getId(), item2.getId());
        assertTrue(secondVote.getId().equals(
                repository.getFullVoteInPoll(areaId, GOD_ID, poll.getId())
                                .getId()
        ));
    }

    @Test
    public void getVoteByUserAndVoteId() throws Exception {
        Vote expected = testPolls.getActivePoll().getVotes().iterator().next();

        reset();
        Vote actual = votingService.getVote(expected.getVoterId() , expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, VOTE_COMPARATOR.noAssoc()));
    }

    @Test
    public void getFullVoteByUserAndVoteId() throws Exception {
        Vote expected = testPolls.getActivePoll().getVotes().iterator().next();

        reset();
        Vote actual = votingService.getFullVote(expected.getVoterId() , expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, VOTE_COMPARATOR));
    }

    @Test
    public void getVotesForPoll() throws Exception {
        LunchPlacePoll pastPoll = testPolls.getPastPoll();

        reset();
        List<Vote> actual = votingService.getFullVotesForPoll(areaId, pastPoll.getId());
        assertSelect(1);

        List<Vote> expected = pastPoll.getVotes().stream().sorted().collect(Collectors.toList());
        assertThat(actual, contains(matchCollection(expected, VOTE_COMPARATOR)));
    }

    @Test
    public void getVotedForPollByVoterId() {
        reset();
        Collection<String> ids = votingService.getVotedForPollByVoter(VOTER, testPolls.getActivePoll().getId());
        assertSelect(1);

        List<String> expected = Arrays.asList(testPolls.getActivePoll().getPollItems().get(0).getId());

        assertTrue(ids.equals(expected));
    }

    @Test
    public void voterRevokesHisVote() throws Exception {
        Vote existingVote = testPolls.getActivePoll().getVotes().iterator().next();
        assertNotNull(votingService.getVote(existingVote.getVoterId() , existingVote.getId()));

        reset();
        votingService.revokeVote(existingVote.getVoterId(), existingVote.getId());
        assertSql(1, 0, 0, 1);

        thrown.expect(NotFoundException.class);
        votingService.getVote(existingVote.getVoterId() , existingVote.getId());
    }

    @Test
    public void getPollResult() throws Exception {
        reset();
        VotingResult<PollItem> pollResult = votingService.getPollResult(areaId, testPolls.getActivePoll().getId());
        assertSelect(1);

        PollItem pollItem1 = testPolls.getActivePollPollItem1();

        assertTrue(pollResult.getWinners().equals(Arrays.asList(pollItem1)));
        assertTrue(pollResult.countPerItem().get(pollItem1) == 3);
        assertTrue(pollResult.countPerItem().get(testPolls.getActivePollPollItem2()) == 2);
    }
}