package ua.belozorov.lunchvoting.service.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.Poll;
import ua.belozorov.lunchvoting.model.voting.PollItem;
import ua.belozorov.lunchvoting.model.voting.Vote;
import ua.belozorov.lunchvoting.repository.voting.PollingRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.voting.PollTestData.POLL_COMPARATOR;
import static ua.belozorov.lunchvoting.model.voting.VoteTestData.VOTE_COMPARATOR;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER_ID;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 04.01.17.
 */
public class VotingServiceImplTest extends AbstractServiceTest {

    @Autowired
    private VotingService votingService;

    @Autowired
    private PollingRepository repository;

    @Test
    public void createsPollForTodayMenus() throws Exception {
        String pollId = votingService.createPollForTodayMenus();
        Poll poll = votingService.getPollFullDetails(pollId);

        Set<LunchPlace> places = poll.getPollItems().stream()
                .map(PollItem::getItem)
                .collect(Collectors.toSet());
        Set<LunchPlace> expectedPlaces = new HashSet<>(Arrays.asList(testPlaces.getPlace3(), testPlaces.getPlace4()));

        // PollItems contain expected LunchPlace IDs
        assertTrue(
               places.stream()
                    .map(LunchPlace::getId)
                    .collect(Collectors.toSet())
                    .equals(
                            expectedPlaces.stream()
                                    .map(LunchPlace::getId).collect(Collectors.toSet())
        ));

        // LunchPlaces contain expected menus (that are active today)
        assertTrue(
                places.stream()
                    .flatMap(place -> place.getMenus().stream())
                    .collect(Collectors.toSet())
                    .equals(
                            testPlaces.getMenus().stream()
                                    .filter(menu -> menu.getEffectiveDate().equals(LocalDate.now())
                                                    && expectedPlaces.contains(menu.getLunchPlace()))
                                    .collect(Collectors.toSet())
        ));
    }

    @Test
    public void votesFirstTime() throws Exception {
        LunchPlacePoll poll1 = testPolls.getPoll2();
        PollItem item = poll1.getPollItems().iterator().next();
        Vote returned = votingService.vote(VOTER_ID, poll1.getId(), item.getId());
        Vote expected = testVotes.getVote1().setVoteTime(returned.getVoteTime());
        Vote actual = repository.getVoteInPoll(VOTER_ID, poll1.getId());
        assertThat(returned, matchSingle(expected, VOTE_COMPARATOR));
        assertThat(actual, matchSingle(expected, VOTE_COMPARATOR));
    }

    @Test
    public void updatesVoteBeforeTimeThreshold() throws Exception {
        LunchPlacePoll poll = testPolls.getPoll2();
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        PollItem item1 = iterator.next();
        PollItem item2 = iterator.next();

        Vote firstVote = votingService.vote(VOTER_ID, poll.getId(), item1.getId());
        Vote actual = repository.getVoteInPoll(VOTER_ID, poll.getId());
        assertTrue(firstVote.getId().equals(actual.getId()));

        Thread.sleep(100);
        Vote secondVote = votingService.vote(VOTER_ID, poll.getId(), item2.getId());
        assertTrue(secondVote.getId().equals(
                repository.getVoteInPoll(VOTER_ID, poll.getId())
                        .getId()
        ));
    }

    @Test
    public void getPollWithWholeHierarchy() throws Exception {
        LunchPlacePoll poll = (LunchPlacePoll)votingService.getPollFullDetails(testPolls.getPoll2().getId());
        assertThat(poll, matchSingle(poll, POLL_COMPARATOR));
        assertTrue(poll.getPollItems().stream()
                .flatMap(pi -> pi.getItem().getMenus().stream())
                .flatMap(m -> m.getDishes().stream())
                .count() == 2
        );
    }

    @Test
    public void getPollAndOnePollItemDetailed1() throws Exception {
        LunchPlacePoll poll = testPolls.getPoll2();
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        PollItem item1 = iterator.next();
        Poll actual = votingService.getPollItemDetails(poll.getId(), item1.getId());
        assertTrue(actual.getPollItems().iterator().next().getId().equals(item1.getId()));
        assertTrue(actual.getPollItems().stream()
                .flatMap(pi -> pi.getItem().getMenus().stream())
                .flatMap(m -> m.getDishes().stream())
                .count() == 1
        );
    }

    @Test
    public void getPollAndMultiplePollItemsDetailed() throws Exception {
        LunchPlacePoll poll = testPolls.getPoll2();
        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        PollItem item1 = iterator.next();
        PollItem item2 = iterator.next();
        List<String> itemIds = Arrays.asList(item1.getId(), item2.getId());

        Poll actual = votingService.getPollItemDetails(poll.getId(), itemIds);
        assertTrue(actual.getPollItems().stream()
                .map(PollItem::getId)
                .collect(Collectors.toSet())
                    .equals(new HashSet<>(itemIds))
        );
        assertTrue(actual.getPollItems().stream()
                .flatMap(pi -> pi.getItem().getMenus().stream())
                .flatMap(m -> m.getDishes().stream())
                .count() == 2
        );
    }


}