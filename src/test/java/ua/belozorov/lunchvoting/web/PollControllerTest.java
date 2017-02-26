package ua.belozorov.lunchvoting.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ua.belozorov.*;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.to.PollTo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.DateTimeFormatters.DATE_FORMATTER;
import static ua.belozorov.lunchvoting.DateTimeFormatters.DATE_TIME_FORMATTER;

/**

 *
 * Created on 04.02.17.
 */
public class PollControllerTest extends AbstractControllerTest {
    private static final String REST_URL = PollController.REST_URL;

    @Autowired
    private PollService pollService;

    private final String areaId = testAreas.getFirstAreaId();

    private final LunchPlacePoll activePoll = testPolls.getActivePoll();

    private final ObjectToMapConverter<PollTo> converter;

    public PollControllerTest() {
        ObjectToMapConverter<TimeConstraint> timeConstraintConverter = new SimpleObjectToMapConverter<>(
                new FieldMappingEntry<>("startTime", TimeConstraint::getStartTime),
                new FieldMappingEntry<>("endTime", TimeConstraint::getEndTime),
                new FieldMappingEntry<>("voteChangeThreshold", TimeConstraint::getVoteChangeThreshold)
        );
        ObjectToMapConverter<PollItem> pollItemConverter = new SimpleObjectToMapConverter<>(
                new FieldMappingEntry<>("id", PollItem::getId),
                new FieldMappingEntry<>("itemId", PollItem::getItemId)
        );
        this.converter = new SimpleObjectToMapConverter<>(
                new FieldMappingEntry<>("id", PollTo::getId),
                new FieldMappingEntry<>("menuDate", to -> to.getMenuDate().format(DATE_FORMATTER)),
                new ObjectMappingEntry<>("timeConstraint", PollTo::getTimeConstraint, timeConstraintConverter),
                new CollectionMappingEntry<>("pollItems", PollTo::getPollItems, pollItemConverter)
        );
    }

    @Before
    public void setUp() throws Exception {
        Mockito.reset(pollService);
    }

    @Test
    public void testGet() throws Exception {
        String activePollId = activePoll.getId();

        when(pollService.getWithPollItems(areaId, activePollId)).thenReturn(testPolls.getActivePoll());
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/{pollId}", areaId, activePollId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(pollService).getWithPollItems(areaId, activePollId);

        Map<String, Object> objProperties = this.converter.convert(new PollTo(testPolls.getActivePoll()));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetAll() throws Exception {
        when(pollService.getAll(areaId)).thenReturn(testPolls.getA1Polls());
        String actual = mockMvc
                .perform(
                        get(REST_URL, areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        verify(pollService).getAll(areaId);

        List<LunchPlacePoll> polls = testPolls.getA1Polls();

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetPollsByActivePeriod() throws Exception {
        String start = NOW_DATE_TIME.minusDays(2).format(DATE_TIME_FORMATTER);
        String end = NOW_DATE_TIME.format(DATE_TIME_FORMATTER);

        List<LunchPlacePoll> polls = Stream.of(
                    testPolls.getPastPoll(),
                    testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        when(pollService.getPollsByActivePeriod(areaId, NOW_DATE_TIME.minusDays(2), NOW_DATE_TIME))
                .thenReturn(polls);
        String actual = mockMvc
                .perform(
                    get(REST_URL, areaId)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("start", start)
                    .param("end", end)
                            .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        verify(pollService).getPollsByActivePeriod(areaId, NOW_DATE_TIME.minusDays(2), NOW_DATE_TIME);

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetPastPolls() throws Exception {
        List<LunchPlacePoll> polls = Stream.of(testPolls.getPastPoll())
                .sorted()
                .collect(Collectors.toList());

        when(pollService.getPastPolls(areaId)).thenReturn(polls);
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/past", areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        verify(pollService).getPastPolls(areaId);

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetActivePolls() throws Exception {
        List<LunchPlacePoll> polls = Stream.of(testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        when(pollService.getActivePolls(areaId)).thenReturn(polls);
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/active", areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                        .andReturn()
                            .getResponse().getContentAsString();
        verify(pollService).getActivePolls(areaId);

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetFuturePolls() throws Exception {
        List<LunchPlacePoll> polls = Stream.of(testPolls.getFuturePoll())
                .sorted()
                .collect(Collectors.toList());

        when(pollService.getFuturePolls(areaId)).thenReturn(polls);
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/future", areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        verify(pollService).getFuturePolls(areaId);

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void checksWhetherPollIsActive() throws Exception {
        String id = testPolls.getPastPoll().getId();

        when(pollService.isPollActive(areaId, id)).thenReturn(false);
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/active", areaId)
                        .param("pollId", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        verify(pollService).isPollActive(areaId, id);

        Map<String,Object> map = new HashMap<>();
        map.put("pollId", id);
        map.put("active", false);
        String expected = jsonUtils.toJson(map);

        assertJson(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        String id = testPolls.getFuturePoll().getId();

        mockMvc.perform(
                delete(REST_URL + "/{pollId}", areaId, id)
                .with(csrf())
                .with(god())
        )
        .andExpect(status().isNoContent());
        verify(pollService).delete(areaId, id);
    }
}