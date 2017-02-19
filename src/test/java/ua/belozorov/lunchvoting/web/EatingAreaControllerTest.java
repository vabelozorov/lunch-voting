package ua.belozorov.lunchvoting.web;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.*;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.AreaTestData;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.service.area.EatingAreaService;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.PollTo;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ControllerUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.DateTimeFormatters.WEB_DATE_FORMATTER;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.UserTestData.ALIEN_USER1;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.AREA_COMPARATOR;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.02.17.
 */
public class EatingAreaControllerTest extends AbstractControllerTest {
    private static final String REST_URL = EatingAreaController.REST_URL;
    @Autowired
    private EatingAreaService areaService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private LunchPlaceService placeService;

    @Autowired
    private PollService pollService;

    private final String areaId = testAreas.getFirstAreaId();

    private final ObjectToMapConverter<PollTo> converter;

    public EatingAreaControllerTest() {
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
    public void testCreate() throws Exception {
        AuthorizedUser.authorize(ALIEN_USER1);
        MvcResult mvcResult = mockMvc.perform(post(REST_URL).param("name", "Name").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = getCreatedId(location);

        mockMvc.perform(get(location)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));
        assertJson(expected, mvcResult.getResponse().getContentAsString());

        assertNotNull(areaService.getArea(id));
    }

    @Test
    public void e409AndMessageOnCreateAreaWithDuplicateName() throws Exception {
        AuthorizedUser.authorize(ALIEN_USER1);
        MvcResult result = mockMvc.perform(post(REST_URL).param("name", testAreas.getFirstAreaName()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.DUPLICATE_AREA_NAME,
                "Area name " + testAreas.getFirstAreaName() + " already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testCreateUserInArea() throws Exception {
        String areaId = testAreas.getFirstAreaId();
        User newUser = new User(null, "Name", "new@meil.com", "newpassword").assignAreaId(areaId);
        MvcResult mvcResult = mockMvc.perform(post(REST_URL + "/{id}/members", areaId)
                    .content(jsonUtils.toJson(newUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = getCreatedId(location);

        mockMvc.perform(get(location)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));
        assertJson(expected, mvcResult.getResponse().getContentAsString());

        assertEquals(profileService.get(id).getAreaId(), areaId);
    }

    @Test
    public void e409AndMessageOnCreateUserWithDuplicateEmail() throws Exception {
        UserTo userTo = new UserTo("New User", "god@email.com", "strongPassword");
        MvcResult result = mockMvc
                .perform(post(REST_URL + "/{id}/members", testAreas.getFirstAreaId())
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.DUPLICATE_EMAIL,
                "Email god@email.com already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void createPlaceInArea() throws Exception {
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567");
        LunchPlaceTo to = new LunchPlaceTo("New PLace", "New Street 12/12", "New Description", phones);

        MvcResult result = mockMvc.perform(post(REST_URL + "/{areaId}/places", testAreas.getFirstAreaId())
                .content(jsonUtils.toJson(to))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String uri = jsonUtils.locationFromMvcResult(result);
        String id = getCreatedId(uri);

        mockMvc.perform(get(uri)).andExpect(status().isOk());

        assertNotNull(placeService.get(testAreas.getFirstAreaId(), id));
    }

    @Test
    public void testCreatePollForTodayMenus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(REST_URL + "/{id}/polls", areaId))
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
        MvcResult mvcResult = mockMvc.perform(post(REST_URL + "/{id}/polls", areaId).param("menuDate", date))
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
    public void testUpdate() throws Exception {
        mockMvc.perform(put(REST_URL).param("name", "NEW_AWESOME_NAME"))
                .andExpect(status().isNoContent());
        EatingArea updated = areaService.getArea(testAreas.getFirstAreaId());

        assertThat(updated, matchSingle(testAreas.getFirstArea().changeName("NEW_AWESOME_NAME"), AREA_COMPARATOR));
    }

    @Test
    public void testGetFullDto() throws Exception {
        String actual = mockMvc.perform(get("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                    .param("summary", "false")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected = jsonUtils.toJson(AreaTestData.dto(testAreas.getFirstArea()));

        assertJson(expected, actual);
    }

    @Test
    public void testGetSummaryDto() throws Exception {
        String actual = mockMvc.perform(get("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected = jsonUtils.toJson(AreaTestData.dtoSummary(testAreas.getFirstArea()));

        assertJson(expected, actual);
    }

    @Test
    public void testFilterByName() throws Exception {
        EatingArea chow1 = areaService.create("ChowArea", ALIEN_USER1);
        EatingArea chow2 = areaService.create("ChowAreaNew", ALIEN_USER1);
        String actual = mockMvc.perform(get("{base}/filter", REST_URL).param("name", "Chow")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> convert = new SimpleObjectToMapConverter<>(
                new FieldMappingEntry<>("id", EatingArea::getId),
                new FieldMappingEntry<>("name", EatingArea::getName),
                new FieldMappingEntry<>("created", EatingArea::getCreated)
        ).convert(chow1, chow2);
        String expected = jsonUtils.toJson(convert);

        assertJson(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        thrown.expect(NotFoundException.class);
        areaService.getArea(testAreas.getFirstAreaId());
    }

}