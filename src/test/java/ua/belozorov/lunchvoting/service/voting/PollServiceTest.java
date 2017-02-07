package ua.belozorov.lunchvoting.service.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.exceptions.NoPollItemsException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.voting.polling.PollTestData.POLL_COMPARATOR;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 03.02.17.
 */
public class PollServiceTest extends AbstractServiceTest {

    @Autowired
    private PollService pollService;
    @Autowired
    private PollRepository pollRepository;

    @Test
    public void createsPollForTodayMenus() throws Exception {
        String pollId = pollService.createPollForTodayMenus().getId();
        Poll poll = pollService.get(pollId);

        List<Poll> expected = Stream.of(
                testPolls.getFuturePoll(), testPolls.getActivePoll(),
                testPolls.getActivePollNoUpdate(), testPolls.getPastPoll(), poll
        ).sorted().collect(Collectors.toList());

        assertThat(
                pollService.getAll(),
                contains(matchCollection(expected, POLL_COMPARATOR))
        );
    }

    @Test
    public void createPollForMenuDate() throws Exception {
        String pollId = pollService.createPollForMenuDate(NOW_DATE.plusDays(2)).getId();
        Poll poll = pollService.get(pollId);

        List<Poll> expected = Stream.of(
                testPolls.getFuturePoll(), testPolls.getActivePoll(),
                testPolls.getActivePollNoUpdate(), testPolls.getPastPoll(), poll
        ).sorted().collect(Collectors.toList());

        assertThat(
                pollService.getAll(),
                contains(matchCollection(expected, POLL_COMPARATOR))
        );
    }

    @Test(expected = NoPollItemsException.class)
    public void failsWhenNoMenusForTheDate() throws Exception {
        pollService.createPollForMenuDate(NOW_DATE.plusDays(1));
    }

    @Test
    public void testDelete() throws Exception {
        pollService.delete(testPolls.getFuturePoll().getId());

        assertTrue(pollService.getAll().size() == 3);
    }

    @Test
    public void testGetAll() throws Exception {
        List<Poll> actual = pollService.getAll();
        List<Poll> expected = Stream.of(
                testPolls.getFuturePoll(), testPolls.getActivePoll(),
                testPolls.getActivePollNoUpdate(), testPolls.getPastPoll()
        ).sorted().collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR))
        );
    }

    @Test
    public void testGet() throws Exception {
        Poll actual = pollService.get(testPolls.getActivePoll().getId());

        assertThat(actual, matchSingle(testPolls.getActivePoll(), POLL_COMPARATOR));
    }

    @Test(expected = NotFoundException.class)
    public void failsWhenGetNonExistingId() throws Exception {
        pollService.get("I_DO_NOT_EXIST");
    }

    @Test
    public void testGetPollsByActivePeriod() throws Exception {
        List<Poll> actual = pollService.getPollsByActivePeriod(NOW_DATE_TIME.minusDays(2), NOW_DATE_TIME);
        List<Poll> expected = Stream.of(testPolls.getPastPoll(), testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR))
        );
    }

    @Test
    public void testGetActivePolls() throws Exception {
        List<Poll> actual = pollService.getActivePolls();
        List<Poll> expected = Stream.of(testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR))
        );
    }

    @Test
    public void testGetFuturePolls() throws Exception {
        List<Poll> actual = pollService.getFuturePolls();
        List<Poll> expected = Arrays.asList(testPolls.getFuturePoll());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR))
        );
    }

    @Test
    public void testGetPastPolls() throws Exception {
        List<Poll> actual = pollService.getPastPolls();
        List<Poll> expected = Arrays.asList(testPolls.getPastPoll());
        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR))
        );
    }

    @Test
    public void checksWhetherPollIsActive() throws Exception {
        Boolean isActive = pollService.isPollActive(testPolls.getPastPoll().getId());
        assertFalse(isActive);

        isActive = pollService.isPollActive(testPolls.getActivePoll().getId());
        assertTrue(isActive);
    }
}