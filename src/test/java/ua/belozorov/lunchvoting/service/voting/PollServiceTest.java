package ua.belozorov.lunchvoting.service.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.WithMockAdmin;
import ua.belozorov.lunchvoting.WithMockVoter;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.exceptions.PollException;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.*;
import static ua.belozorov.lunchvoting.model.voting.polling.PollTestData.*;

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

    private final String areaId = testAreas.getFirstAreaId();

    @Test
    @WithMockAdmin
    public void createPollForMenuDate() throws Exception {
        reset();
        LunchPlacePoll expected = pollService.createPollForMenuDate(areaId, NOW_DATE.plusDays(2), TimeConstraint.getDefault());
        assertSql(1, 3, 2, 0); //TODO expected 0 updates; consider changing LunchPlacePoll#pollItems to Set?

        LunchPlacePoll actual = pollRepository.getWithPollItems(expected.getId());

        assertThat(actual, matchSingle(expected, POLL_COMPARATOR.noVotes()));
    }

    @Test(expected = PollException.class)
    @WithMockAdmin
    public void failOnCreatePollWhenNoMenusForTheDate() throws Exception {
        pollService.createPollForMenuDate(areaId, NOW_DATE.plusDays(1), TimeConstraint.getDefault());
    }

    @Test
    @WithMockVoter
    public void getPollWithPollItems() throws Exception {
        reset();
        LunchPlacePoll actual = pollService.getWithPollItems(areaId, testPolls.getActivePoll().getId());
        assertSelect(1);

        assertThat(actual, matchSingle(testPolls.getActivePoll(), POLL_COMPARATOR.noVotes()));
    }

    @Test
    @WithMockVoter
    public void getPollWithPollItemsAndVotes() throws Exception {
        reset();
        LunchPlacePoll actual = pollService.getWithPollItemsAndVotes(areaId, testPolls.getActivePoll().getId());
        assertSelect(1);

        assertThat(actual, matchSingle(testPolls.getActivePoll(), POLL_COMPARATOR));
    }

    @Test(expected = NotFoundException.class)
    @WithMockVoter
    public void failsGetWhenNonExistingId() throws Exception {
        pollService.getWithPollItems(areaId, "I_DO_NOT_EXIST");
    }

    @Test(expected = NotFoundException.class)
    @WithMockVoter
    public void failsGetWhenAreaNotCorresponds() throws Exception {
        pollService.getWithPollItems(testAreas.getSecondAreaId(), testPolls.getActivePoll().getId());
    }

    @Test
    @WithMockVoter
    public void getAllAreaPolls() throws Exception {
        reset();
        List<LunchPlacePoll> actual = pollService.getAll(areaId);
        assertSelect(1);

        List<LunchPlacePoll> expected = Stream.of(
                testPolls.getFuturePoll(), testPolls.getActivePoll(),
                testPolls.getActivePollNoUpdate(), testPolls.getPastPoll()
        ).sorted().collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR_NO_ASSOC))
        );
    }

    @Test
    @WithMockVoter
    public void getPollsByActivePeriod() throws Exception {
        reset();
        List<LunchPlacePoll> actual = pollService.getPollsByActivePeriod(areaId, NOW_DATE_TIME.minusDays(2), NOW_DATE_TIME);
        assertSelect(1);

        List<LunchPlacePoll> expected = Stream.of(testPolls.getPastPoll(), testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR_NO_ASSOC))
        );
    }

    @Test
    @WithMockVoter
    public void getPastPolls() throws Exception {
        reset();
        List<LunchPlacePoll> actual = pollService.getPastPolls(areaId);
        assertSelect(1);

        List<LunchPlacePoll> expected = Arrays.asList(testPolls.getPastPoll());
        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR_NO_ASSOC))
        );
    }

    @Test
    @WithMockVoter
    public void getActivePolls() throws Exception {
        reset();
        List<LunchPlacePoll> actual = pollService.getActivePolls(areaId);
        assertSelect(1);

        List<LunchPlacePoll> expected = Stream.of(testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR_NO_ASSOC))
        );
    }

    @Test
    @WithMockVoter
    public void getFuturePolls() throws Exception {
        reset();
        List<LunchPlacePoll> actual = pollService.getFuturePolls(areaId);
        assertSelect(1);

        List<LunchPlacePoll> expected = Arrays.asList(testPolls.getFuturePoll());

        assertThat(
                actual,
                contains(matchCollection(expected, POLL_COMPARATOR_NO_ASSOC))
        );
    }

    @Test
    @WithMockVoter
    public void checksWhetherPollIsActive() throws Exception {
        reset();
        Boolean isActive = pollService.isPollActive(areaId, testPolls.getPastPoll().getId());
        assertSelect(1);

        assertFalse(isActive);

        isActive = pollService.isPollActive(areaId, testPolls.getActivePoll().getId());
        assertTrue(isActive);
    }

    @Test
    @WithMockAdmin
    public void deletesPollFromItsArea() throws Exception {
        String pollId = testPolls.getFuturePoll().getId();

        reset();
        pollService.delete(areaId, pollId);
        assertDelete(1);

        thrown.expect(NotFoundException.class);
        asVoter(() -> pollService.getWithPollItems(areaId, pollId));
    }

    @Test(expected = NotFoundException.class)
    @WithMockAdmin
    public void failsDeletePollFromAnotherArea() throws Exception {
        String pollId = testPolls.getFuturePoll().getId();
        pollService.delete(testAreas.getSecondAreaId(), pollId);
    }
}