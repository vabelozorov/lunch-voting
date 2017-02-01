package ua.belozorov.lunchvoting.model.voting.polling;

import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.exceptions.*;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD_ID;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
public class PollTest extends AbstractTest {
    private final List<LunchPlace> places = LunchPlaceTestData.getWithFilteredMenu(NOW_DATE, testPlaces.getPlace3(), testPlaces.getPlace4());

    @Test
    public void failsOnNonExistingPollItemId() throws Exception {
        LunchPlacePoll poll = new LunchPlacePoll(this.places, NOW_DATE);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(String.format("Poll %s does not contain item %s", poll.getId(), "NOT_EXISTS_ITEM_ID"));
        poll.registerVote(VOTER_ID, "NOT_EXISTS_ITEM_ID");
    }

    @Test
    public void failsWhenVotingForInactivePoll() throws Exception {
        List<RuntimeException> exceptions = new ArrayList<>();

        // voting after poll start time
        try {
            LunchPlacePoll pastPoll = testPolls.getPastPoll();
            pastPoll
                    .registerVote(VOTER_ID, pastPoll.getPollItems().get(0).getId());
        } catch (PollNotActiveException e) {
            exceptions.add(e);
        }

        // voting before poll start time
       try {
           LunchPlacePoll futurePoll = testPolls.getFuturePoll();
           futurePoll.registerVote(VOTER_ID, futurePoll.getPollItems().get(0).getId());
        } catch (PollNotActiveException e) {
            exceptions.add(e);
        }

        assertTrue(exceptions.size() == 2);
    }

    @Test
    public void acceptsVote() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        Vote vote = poll.registerVote(GOD_ID, testPolls.getActivePollPollItem1().getId()).getAcceptedVote();

        assertTrue(vote.getPoll().equals(poll));
        assertTrue(vote.getPollItem().equals( testPolls.getActivePollPollItem1()));
        assertTrue(vote.getVoterId().equals(GOD_ID));
    }

    @Test
    public void updatesVote() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        VotePolicyDecision decision = poll.registerVote(VOTER_ID, testPolls.getActivePollPollItem2().getId());

        assertTrue(decision.isUpdate());

        Vote vote = decision.getAcceptedVote();

        assertTrue(vote.getPoll().equals(poll));
        assertTrue(vote.getPollItem().equals( testPolls.getActivePollPollItem2()));
        assertTrue(vote.getVoterId().equals(VOTER_ID));
    }

    @Test(expected = VoteChangeNotAllowedException.class)
    public void failsUpdateWhenItIsAfterTimeThreshold() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePollNoUpdate();
        poll.registerVote(VOTER_ID, poll.getPollItems().get(1).getId());
    }

    @Test(expected = MultipleVotePerItemException.class)
    public void failsOnSecondVoteAttempt() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        poll.registerVote(VOTER_ID, testPolls.getActivePollPollItem1().getId());
    }

    @Test(expected = LunchPlaceWithoutMenuException.class)
    public void lunchPlaceWithEmptyMenusGetsRejected() throws Exception {
        LunchPlace place = new LunchPlace("ID", "Name", "Address", "Description", new ArrayList<String>(), new ArrayList<Menu>(), "AdminId");
        new LunchPlacePoll(Arrays.asList(place), LocalDate.now());
    }

    @Test(expected = MenuDateMismatchException.class)
    public void lunchPlaceWithWrongMenuDateGetsRejected() throws Exception {
        LunchPlace place = new LunchPlace("ID", "Name", "Address", "Description", new ArrayList<String>(), new ArrayList<Menu>(), "AdminId");
        Menu menu = new Menu(LocalDate.now().minusDays(1), new ArrayList<Dish>(), place);
        place = place.setMenus(Arrays.asList(menu));
        new LunchPlacePoll(Arrays.asList(place), LocalDate.now());
    }
}