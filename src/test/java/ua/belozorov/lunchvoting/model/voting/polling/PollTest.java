package ua.belozorov.lunchvoting.model.voting.polling;

import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.exceptions.*;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.model.UserTestData.GOD_ID;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;

/**

 *
 * Created on 09.12.16.
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
                    .registerVote(GOD_ID, pastPoll.getPollItems().get(0).getId());
        } catch (VotePolicyException e) {
            exceptions.add(e);
        }

        // voting before poll start time
       try {
           LunchPlacePoll futurePoll = testPolls.getFuturePoll();
           futurePoll.registerVote(GOD_ID, futurePoll.getPollItems().get(0).getId());
        } catch (VotePolicyException e) {
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

    @Test(expected = VotePolicyException.class)
    public void failsUpdateWhenItIsAfterTimeThreshold() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePollNoUpdate();
        poll.registerVote(VOTER_ID, poll.getPollItems().get(1).getId());
    }

    @Test(expected = VotePolicyException.class)
    public void failsOnSecondVoteAttempt() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        poll.registerVote(VOTER_ID, testPolls.getActivePollPollItem1().getId());
    }

    @Test(expected = PollException.class)
    public void failsWhenLunchPlaceDoesNotHaveMenuWithMenuDate() throws Exception {
        LunchPlace place = new LunchPlace("ID", "Name", "Address", "Description", new HashSet<>());
        new LunchPlacePoll(Arrays.asList(place), NOW_DATE);
    }

    @Test(expected = PollException.class)
    public void failsWhenLunchPlacesAreEmpty() throws Exception {
        new LunchPlacePoll(new ArrayList<>(), NOW_DATE);
    }
}