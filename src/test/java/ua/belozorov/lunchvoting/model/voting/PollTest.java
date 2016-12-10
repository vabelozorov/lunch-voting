package ua.belozorov.lunchvoting.model.voting;

import org.junit.Test;
import ua.belozorov.lunchvoting.exceptions.MultipleVoteException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.exceptions.PollNotActiveException;
import ua.belozorov.lunchvoting.exceptions.VoteChangeNotAllowedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACES;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
public class PollTest {

    @Test(expected = IllegalStateException.class)
    public void testVerifyWrongPollId() throws Exception {
        Poll poll = new Poll(PLACES);
        String firstPollItemId = poll.getPollItems().get(0).getId();
        VoteIntention intention = new VoteIntention(VOTER_ID, "NOT_EXISTS_ID", firstPollItemId, null);
        poll.verify(intention);
    }

    @Test(expected = NotFoundException.class)
    public void testVerifyWrongPollItemId() throws Exception {
        Poll poll = new Poll(PLACES);
        VoteIntention intention = new VoteIntention(VOTER_ID, poll.getId(), "NOT_EXISTS_ID", null);
        poll.verify(intention);
    }

    @Test
    public void testVerifyVotingForInactivePoll() throws Exception {
        List<RuntimeException> exceptions = new ArrayList<>();

        LocalDateTime start = null;
        LocalDateTime end = null;
        Poll poll = null;
        String firstPollItemId = null;
        VoteIntention intention = null;
        // voting before poll start time
        try {
            start = LocalDateTime.now().plusHours(1);
            end = start.plusHours(2);
            poll = new Poll(start, end, start, PLACES);
            firstPollItemId = poll.getPollItems().get(0).getId();
            intention = new VoteIntention(VOTER_ID, poll.getId(), firstPollItemId, null);
            poll.verify(intention);
        } catch (PollNotActiveException e) {
            exceptions.add(e);
        }

        // voting after poll start time
       try {
            start = LocalDateTime.now().minusDays(1);
            end = start.plusHours(2);
            poll = new Poll(start, end, start, PLACES);
            firstPollItemId = poll.getPollItems().get(0).getId();
            intention = new VoteIntention(VOTER_ID, poll.getId(), firstPollItemId, null);
            poll.verify(intention);
        } catch (PollNotActiveException e) {
            exceptions.add(e);
        }

        assertTrue(exceptions.size() == 2);
    }

    @Test
    public void testVerifyVoteAccepted() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        Poll poll = new Poll(start, end, start, PLACES);
        String firstPollItemId = poll.getPollItems().get(0).getId();
        VoteIntention intention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, null);
        VoteDecision decision = poll.verify(intention);
        Vote vote = decision.getVote();

        assertTrue(decision.isAccept());
        assertTrue(vote.getPoll().equals(poll));
        assertTrue(vote.getPollItem().equals(poll.pollItemById(firstPollItemId)));
        assertTrue(vote.getVoterId().equals(VOTER_ID));
        assertTrue(vote.getVoteTime().equals(intention.getMadeTime()));
    }

    @Test
    public void testVerifyVoteUpdated() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        Poll poll = new Poll(start, end, start.plusHours(1), PLACES);
        String firstPollItemId = poll.getPollItems().get(0).getId();
        String secondPollItemId = poll.getPollItems().get(1).getId();
        VoteIntention intention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, null);
        Vote vote = poll.verify(intention).getVote();
        VoteIntention secondVoteIntention = new VoteIntention(VOTER.getId(), poll.getId(), secondPollItemId, vote);
        VoteDecision decision = poll.verify(secondVoteIntention);
        Vote voteForUpdate = decision.getVote();

        assertTrue(decision.isUpdate());
        assertTrue(voteForUpdate.getPoll().getId().equals(poll.getId()));
        assertTrue(voteForUpdate.getPollItem().getId().equals(secondPollItemId));
        assertTrue(voteForUpdate.getVoterId().equals(VOTER_ID));
        assertTrue(voteForUpdate.getVoteTime().equals(secondVoteIntention.getMadeTime()));
    }

    @Test(expected = VoteChangeNotAllowedException.class)
    public void testVerifyVoteUpdateTooLate() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = start.plusHours(3);
        Poll poll = new Poll(start, end, start.plusHours(1), PLACES);
        String firstPollItemId = poll.getPollItems().get(0).getId();
        String secondPollItemId = poll.getPollItems().get(1).getId();
        VoteIntention intention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, null);
        Vote vote = poll.verify(intention).getVote();
        VoteIntention secondVoteIntention = new VoteIntention(VOTER.getId(), poll.getId(), secondPollItemId, vote);
        poll.verify(secondVoteIntention);
    }

    @Test(expected = MultipleVoteException.class)
    public void testVerifyOnlyOneVoteAllowed() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        Poll poll = new Poll(start, end, start.plusHours(1), PLACES);
        String firstPollItemId = poll.getPollItems().get(0).getId();
        VoteIntention intention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, null);
        Vote vote = poll.verify(intention).getVote();
        VoteIntention secondVoteIntention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, vote);
        poll.verify(secondVoteIntention);
    }
}