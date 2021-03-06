package ua.belozorov.lunchvoting.web;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.*;
import ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.AreaTestData;
import ua.belozorov.lunchvoting.model.area.EatingArea;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.service.area.EatingAreaService;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.to.PollTo;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ControllerUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.util.DateTimeFormatters.DATE_FORMATTER;
import static ua.belozorov.lunchvoting.model.UserTestData.*;

/**

 *
 * Created on 12.02.17.
 */
public class EatingAreaControllerTest extends AbstractControllerTest {
    private static final String REST_URL = EatingAreaController.REST_URL;

    @Autowired
    private EatingAreaService areaService;

    @Autowired
    private UserService userService;

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
                new FieldMappingEntry<>("menuDate", to -> to.getMenuDate().format(DATE_FORMATTER)),
                new ObjectMappingEntry<>("timeConstraint", PollTo::getTimeConstraint, timeConstraintConverter),
                new CollectionMappingEntry<>("pollItems", PollTo::getPollItems, pollItemConverter)
        );
    }

    @Before
    public void resetMocks() throws Exception {
        Mockito.reset(areaService);
        Mockito.reset(userService);
        Mockito.reset(placeService);
        Mockito.reset(pollService);
    }

    @Test
    public void createArea() throws Exception {
        String name = "Name";
        EatingArea created = new EatingArea(name);
        created = created.addMember(VOTER.assignAreaId(created.getId()));

        when(areaService.create(name, VOTER)).thenReturn(created);

        MvcResult mvcResult = mockMvc
                .perform(
                        post(REST_URL).param("name", name)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                                                .with(voter())
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(areaService).create(name, VOTER);

        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = getCreatedId(location);

        when(areaService.getAsTo(areaId, true))
                .thenReturn(new AreaTo("id", "name", NOW_DATE_TIME, 1L, 1L, 1L));
        mockMvc.perform(get(location).with(voter()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));

        assertJson(expected, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void failseWhenAreaNameIsTooShort() throws Exception {
        String tooShortName = "n";
        MvcResult result = mockMvc
                .perform(
                        post(REST_URL).param("name", tooShortName)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                                .with(voter())
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        //TODO complete test
    }

    @Test
    public void e409AndMessageOnCreateAreaWithDuplicateName() throws Exception {
        String duplicateName = "name";
        when(areaService.create(duplicateName, VOTER))
                .thenThrow(new DuplicateDataException(ErrorCode.AREA_DUPLICATE_NAME, new Object[]{duplicateName}));
        MvcResult result = mockMvc
                .perform(
                        post(REST_URL).param("name", duplicateName)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                                                .with(voter())
                )
                .andExpect(status().isConflict())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.AREA_DUPLICATE_NAME,
                "Area name " + duplicateName + " already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void createUserInArea() throws Exception {
        UserTo userTo = new UserTo("New User", "new@email.com", "strongPassword");
        User newUser = new User(userTo.getName(), userTo.getEmail(), userTo.getPassword());

        when(areaService.createUserInArea(eq(areaId), any())).thenReturn(VOTER);

        MvcResult mvcResult = mockMvc
                .perform(
                        post(REST_URL + "/{id}/members", areaId)
                        .content(jsonUtils.toJson(VOTER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                                                .with(god())
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(areaService).createUserInArea(eq(areaId), userNoIdNoDate(newUser));

        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = getCreatedId(location);

        when(userService.get(areaId, id))
                .thenReturn(VOTER);
        mockMvc.perform(get(location).with(god()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));

        assertJson(expected, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void failsWhenCreateUserWithEmptyName() throws Exception {
        UserTo userTo = new UserTo("", "new@email.com", "strongPassword");
        MvcResult result = mockMvc
                .perform(
                        post(REST_URL + "/{id}/members", areaId)
                                .content(jsonUtils.toJson(userTo))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(god())
                                                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.PARAMS_VALIDATION_FAILED,
                "field 'name', rejected value '', reason: may not be empty"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void e409AndMessageOnCreateUserWithDuplicateEmail() throws Exception {
        when(areaService.createUserInArea(any(), any()))
                .thenThrow(new DuplicateDataException(ErrorCode.DUPLICATE_EMAIL, new Object[]{VOTER.getEmail()}));
        MvcResult result = mockMvc
                .perform(post(REST_URL + "/{id}/members", areaId)
                        .content(jsonUtils.toJson(VOTER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                                                .with(god())
                )
                .andExpect(status().isConflict())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.DUPLICATE_EMAIL,
                "Email " + VOTER.getEmail() + " already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void createPlaceInArea() throws Exception {
        Map<String, Object> jsonValues = getNewLunchPlaceMap();
        LunchPlace place = new LunchPlace((String)jsonValues.get("name"));

        when(areaService.createPlaceInArea(areaId, null, (String)jsonValues.get("name"),
                (String)jsonValues.get("address"), (String)jsonValues.get("description"), (Set<String>) jsonValues.get("phones")
        )).thenReturn(place);

        MvcResult result = mockMvc
                .perform(
                        post(REST_URL + "/{areaId}/places", testAreas.getFirstAreaId())
                        .content(jsonUtils.toJson(jsonValues))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                                                .with(god())
                )
                .andExpect(status().isCreated())
                .andReturn();
        verify(areaService).createPlaceInArea(
                areaId, null, (String)jsonValues.get("name"),
                (String)jsonValues.get("address"), (String)jsonValues.get("description"), (Set<String>) jsonValues.get("phones"));

        String uri = jsonUtils.locationFromMvcResult(result);
        String id = getCreatedId(uri);

        when(placeService.getMultiple(areaId, Collections.singleton(id))).thenReturn(Arrays.asList(place));

        mockMvc.perform(get(uri).with(voter()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    private Map<String, Object> getNewLunchPlaceMap() {
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567");
        Map<String, Object> jsonValues = new HashMap<>();
        jsonValues.put("name", "Дача");
        jsonValues.put("address", "Французский бул., 85");
        jsonValues.put("description", "Приходите к закату");
        jsonValues.put("phones", phones);
        return jsonValues;
    }

    @Test
    public void createPollForMenuDate() throws Exception {
        String date = NOW_DATE.plusDays(2).format(DATE_FORMATTER);

        when(areaService.createPollInArea(areaId, NOW_DATE.plusDays(2), null, null, null))
                .thenReturn(testPolls.getFuturePoll());

        MvcResult mvcResult = mockMvc
                .perform(
                        post(REST_URL + "/{id}/polls", areaId)
                        .param("menuDate", date)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                                                .with(god())
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(areaService).createPollInArea(areaId, NOW_DATE.plusDays(2), null, null, null);

        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = getCreatedId(location);

        when(pollService.getWithPollItems(areaId, id)).thenReturn(testPolls.getActivePoll());

        String expected = mockMvc.perform(get(location).with(voter()).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        LunchPlacePoll poll = pollService.getWithPollItems(areaId, id);
        Map<String, Object> objProperties = this.converter.convert(new PollTo(poll));
        String actual = jsonUtils.toJson(objProperties);

        assertJson(expected, actual);
    }

    @Test
    public void updateAreaName() throws Exception {
        mockMvc
                .perform(
                        put(REST_URL)
                        .param("name", "NEW_AWESOME_NAME")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                .with(god())
                )
                .andExpect(status().isNoContent());

        verify(areaService).updateAreaName("NEW_AWESOME_NAME", GOD);
    }

    @Test
    public void failsToUpdateAreaNameWhenNameExists() throws Exception {
        String duplicateName = "NEW_AWESOME_NAME";
        doThrow(new DuplicateDataException(ErrorCode.AREA_DUPLICATE_NAME, new Object[]{duplicateName}))
                .when(areaService).updateAreaName(duplicateName, GOD);

        MvcResult result = mockMvc
                .perform(
                        put(REST_URL)
                                .param("name", duplicateName)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                                .with(god())
                )
                .andExpect(status().isConflict())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.AREA_DUPLICATE_NAME,
                "Area name " + duplicateName + " already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testGetFullDto() throws Exception {
        AreaTo dtoFull = AreaTestData.dto(testAreas.getFirstArea());

        when(areaService.getAsTo(areaId, false)).thenReturn(dtoFull);

        String actual = mockMvc
                .perform(
                        get("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                        .param("summary", "false")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected = jsonUtils.toJson(AreaTestData.dto(testAreas.getFirstArea()));

        assertJson(expected, actual);
    }

    @Test
    public void testGetSummaryDto() throws Exception {
        AreaTo dtoSummary = AreaTestData.dtoSummary(testAreas.getFirstArea());

        when(areaService.getAsTo(areaId, true)).thenReturn(dtoSummary);

        String actual = mockMvc
                .perform(
                        get("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(areaService).getAsTo(areaId, true);

        String expected = jsonUtils.toJson(dtoSummary);

        assertJson(expected, actual);
    }

    @Test
    public void testFilterByName() throws Exception {

        List<EatingArea> expectedAreas = Arrays.asList(testAreas.getFirstArea(), testAreas.getSecondArea());
        when(areaService.filterByNameStarts("Chow")).thenReturn(
                expectedAreas
        );

        String actual = mockMvc
                .perform(
                        get(REST_URL)
                        .param("name", "Chow")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> convert = new SimpleObjectToMapConverter<>(
                new FieldMappingEntry<>("id", EatingArea::getId),
                new FieldMappingEntry<>("name", EatingArea::getName),
                new FieldMappingEntry<>("created", EatingArea::getCreated)
        ).convert(expectedAreas);
        String expected = jsonUtils.toJson(convert);

        assertJson(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL)
                                .with(god())
        )
        .andExpect(status().isNoContent());

        verify(areaService).delete(areaId);
    }

    @Test
    public void failsToDeleteAreaWithoutAdminRight() throws Exception {
        MvcResult result = mockMvc.perform(delete(REST_URL)
                .with(voter())
        )
                .andExpect(status().isForbidden())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.AUTH_NO_PERMISSIONS,
                ErrorCode.AUTH_NO_PERMISSIONS.name().toLowerCase()
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }
}