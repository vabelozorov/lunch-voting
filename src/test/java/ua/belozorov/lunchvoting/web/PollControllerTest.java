package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.*;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.to.PollTo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.DateTimeFormatters.WEB_DATE_FORMATTER;
import static ua.belozorov.lunchvoting.DateTimeFormatters.WEB_DATE_TIME_FORMATTER;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.model.voting.polling.PollTestData.POLL_COMPARATOR;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 04.02.17.
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
                new FieldMappingEntry<>("menuDate", to -> to.getMenuDate().format(WEB_DATE_FORMATTER)),
                new ObjectMappingEntry<>("timeConstraint", PollTo::getTimeConstraint, timeConstraintConverter),
                new CollectionMappingEntry<>("pollItems", PollTo::getPollItems, pollItemConverter)
        );
    }

    @Test
    public void testCreatePollForTodayMenus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(REST_URL, areaId))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(mvcResult);

        String expected = mockMvc.perform(get(location).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        String id = getCreatedId(location);

        LunchPlacePoll poll = pollService.getWithPollItems(areaId, id);
        Map<String, Object> objProperties = this.converter.convert(new PollTo(poll));
        String actual = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testCreatePollForMenuDate() throws Exception {
        String date = NOW_DATE.plusDays(2).format(WEB_DATE_FORMATTER);
        MvcResult mvcResult = mockMvc.perform(post(REST_URL, areaId).param("menuDate", date))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(mvcResult);

        String expected = mockMvc.perform(get(location).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        String id = getCreatedId(location);

        LunchPlacePoll poll = pollService.getWithPollItems(areaId, id);
        Map<String, Object> objProperties = this.converter.convert(new PollTo(poll));
        String actual = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGet() throws Exception {
        String actual = mockMvc.perform(get(REST_URL + "/{pollId}", areaId, activePoll.getId())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> objProperties = this.converter.convert(new PollTo(testPolls.getActivePoll()));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetAll() throws Exception {
        String actual = mockMvc.perform(get(REST_URL, areaId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<LunchPlacePoll> polls = testPolls.getA1Polls();

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(get(REST_URL + "/{pollId}", areaId, testPolls.getFuturePoll().getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete(REST_URL + "/{pollId}", areaId, testPolls.getFuturePoll().getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(REST_URL + "/{pollId}", areaId, testPolls.getFuturePoll().getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPollsByActivePeriod() throws Exception {
        String start = NOW_DATE_TIME.minusDays(2).format(WEB_DATE_TIME_FORMATTER);
        String end = NOW_DATE_TIME.format(WEB_DATE_TIME_FORMATTER);
        String actual = mockMvc.perform(
                get(REST_URL, areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("start", start)
                        .param("end", end)
        )
        .andExpect(status().isOk())
        .andReturn()
                .getResponse().getContentAsString();

        List<LunchPlacePoll> polls = Stream.of(testPolls.getPastPoll(), testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetActivePolls() throws Exception {
        String actual = mockMvc.perform(get(REST_URL + "/active", areaId)
                                        .accept(MediaType.APPLICATION_JSON)
                        ).andExpect(status().isOk())
                        .andReturn()
                            .getResponse().getContentAsString();

        List<LunchPlacePoll> polls = Stream.of(testPolls.getActivePollNoUpdate(), testPolls.getActivePoll())
                .sorted()
                .collect(Collectors.toList());

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetFuturePolls() throws Exception {
        String actual = mockMvc.perform(get(REST_URL + "/future", areaId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        List<LunchPlacePoll> polls = Stream.of(testPolls.getFuturePoll())
                .sorted()
                .collect(Collectors.toList());

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetPastPolls() throws Exception {
        String actual = mockMvc.perform(get(REST_URL + "/past", areaId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        List<LunchPlacePoll> polls = Stream.of(testPolls.getPastPoll())
                .sorted()
                .collect(Collectors.toList());

        List<Map<String, Object>> objProperties = this.converter.convert(PollController.convertIntoTo(polls, false));
        String expected = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void checksWhetherPollIsActive() throws Exception {
        String id = testPolls.getPastPoll().getId();
        String actual = mockMvc.perform(get(REST_URL + "/active/{pollId}", areaId, id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Map<String,Boolean> map = new HashMap<>();
        map.put(id, pollService.isPollActive(areaId, id));
        String expected = jsonUtils.toJson(map);

        assertJson(expected, actual);
    }
}