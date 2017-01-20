package ua.belozorov.lunchvoting.service.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.repository.voting.PollingRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.voting.polling.PollTestData.POLL_COMPARATOR;
import static ua.belozorov.lunchvoting.model.voting.polling.VoteTestData.VOTE_COMPARATOR;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD_ID;


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
    public void getPollWithWholeHierarchy() throws Exception {
        LunchPlacePoll poll = (LunchPlacePoll)votingService.getPollFullDetails(testPolls.getActivePoll().getId());
        assertThat(poll, matchSingle(poll, POLL_COMPARATOR));
        assertTrue(poll.getPollItems().stream()
                .flatMap(pi -> pi.getItem().getMenus().stream())
                .flatMap(m -> m.getDishes().stream())
                .count() == 3
        );
    }

    @Test
    public void getPollAndOnePollItemDetailed1() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        String item1Id = testPolls.getActivePollPollItem1().getId();
        Poll actual = votingService.getPollItemDetails(poll.getId(), item1Id);
        assertTrue(actual.getPollItems().iterator().next().getId().equals(item1Id));
        assertTrue(actual.getPollItems().stream()
                            .flatMap(pi -> pi.getItem().getMenus().stream())
                            .flatMap(m -> m.getDishes().stream())
                .count() == 1
        );
    }

    @Test
    public void getPollAndMultiplePollItemsDetailed() throws Exception {
        LunchPlacePoll poll = testPolls.getActivePoll();
        PollItem item1 = testPolls.getActivePollPollItem1();
        PollItem item2 = testPolls.getActivePollPollItem2();
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
                .count() == 3
        );
    }


}