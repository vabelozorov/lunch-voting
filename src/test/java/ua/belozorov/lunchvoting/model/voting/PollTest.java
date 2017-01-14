package ua.belozorov.lunchvoting.model.voting;

import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.exceptions.MultipleVoteException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.exceptions.PollNotActiveException;
import ua.belozorov.lunchvoting.exceptions.VoteChangeNotAllowedException;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
public class PollTest extends AbstractTest {
    private final List<LunchPlace> places = placeTestData.getPlaces();

    @Test(expected = IllegalStateException.class)
    public void testVerifyWrongPollId() throws Exception {
        LunchPlacePoll poll = new LunchPlacePoll(this.places, LocalDate.now());
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        String firstPollItemId = iterator.next().getId();
        VoteIntention intention = new VoteIntention(VOTER_ID, "NOT_EXISTS_ID", firstPollItemId, null);
        poll.verify(intention);
    }

    @Test(expected = NotFoundException.class)
    public void testVerifyWrongPollItemId() throws Exception {
        LunchPlacePoll poll = new LunchPlacePoll(this.places, LocalDate.now());
        VoteIntention intention = new VoteIntention(VOTER_ID, poll.getId(), "NOT_EXISTS_ID", null);
        poll.verify(intention);
    }

    @Test
    public void testVerifyVotingForInactivePoll() throws Exception {
        List<RuntimeException> exceptions = new ArrayList<>();

        LocalDateTime start = null;
        LocalDateTime end = null;
        LocalDate menuDate = LocalDate.now();
        LunchPlacePoll poll = null;
        String firstPollItemId = null;
        VoteIntention intention = null;
        // voting before poll start time
        try {
            start = LocalDateTime.now().plusHours(1);
            end = start.plusHours(2);
            poll = new LunchPlacePoll(start, end, start, this.places, menuDate);
            Iterator<PollItem> iterator = poll.getPollItems().iterator();
            firstPollItemId = iterator.next().getId();
            intention = new VoteIntention(VOTER_ID, poll.getId(), firstPollItemId, null);
            poll.verify(intention);
        } catch (PollNotActiveException e) {
            exceptions.add(e);
        }

        // voting after poll start time
       try {
            start = LocalDateTime.now().minusDays(1);
            end = start.plusHours(2);
            poll = new LunchPlacePoll(start, end, start, this.places, menuDate);
           Iterator<PollItem> iterator = poll.getPollItems().iterator();
           firstPollItemId = iterator.next().getId();
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
        LocalDate menuDate = LocalDate.now();
        LunchPlacePoll poll = new LunchPlacePoll(start, end, start, this.places, menuDate);
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        String firstPollItemId = iterator.next().getId();
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
        LocalDate menuDate = LocalDate.now();
        LunchPlacePoll poll = new LunchPlacePoll(start, end, start.plusHours(1), this.places, menuDate);
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        String firstPollItemId = iterator.next().getId();
        String secondPollItemId = iterator.next().getId();
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
        LocalDate menuDate = LocalDate.now();
        LunchPlacePoll poll = new LunchPlacePoll(start, end, start.plusHours(1), this.places, menuDate);
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        String firstPollItemId = iterator.next().getId();
        String secondPollItemId = iterator.next().getId();
        VoteIntention intention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, null);
        Vote vote = poll.verify(intention).getVote();
        VoteIntention secondVoteIntention = new VoteIntention(VOTER.getId(), poll.getId(), secondPollItemId, vote);
        poll.verify(secondVoteIntention);
    }

    @Test(expected = MultipleVoteException.class)
    public void testVerifyOnlyOneVoteAllowed() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        LocalDate menuDate = LocalDate.now();

        LunchPlacePoll poll = new LunchPlacePoll(start, end, start.plusHours(1), this.places, menuDate);
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        String firstPollItemId = iterator.next().getId();

        VoteIntention intention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, null);
        Vote vote = poll.verify(intention).getVote();
        VoteIntention secondVoteIntention = new VoteIntention(VOTER.getId(), poll.getId(), firstPollItemId, vote);
        poll.verify(secondVoteIntention);
    }

    @Test(expected = IllegalStateException.class)
    public void lunchPlaceWithEmptyMenusGetsRejected() throws Exception {
        LunchPlace place = new LunchPlace("ID", "Name", "Address", "Description", new ArrayList<String>(), new ArrayList<Menu>(), "AdminId");
        new LunchPlacePoll(Arrays.asList(place), LocalDate.now());
    }

    @Test(expected = IllegalStateException.class)
    public void lunchPlaceWithWrongMenuDateGetsRejected() throws Exception {
        LunchPlace place = new LunchPlace("ID", "Name", "Address", "Description", new ArrayList<String>(), new ArrayList<Menu>(), "AdminId");
        Menu menu = new Menu(LocalDate.now().minusDays(1), new ArrayList<Dish>(), place);
        place = place.setMenus(Arrays.asList(menu));
        new LunchPlacePoll(Arrays.asList(place), LocalDate.now());
    }
}