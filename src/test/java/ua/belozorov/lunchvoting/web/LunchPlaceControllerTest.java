package ua.belozorov.lunchvoting.web;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.DateTimeFormatters;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * Created on 25.11.16.
 */
public class LunchPlaceControllerTest extends AbstractControllerTest {
    private static final String REST_URL = LunchPlaceController.REST_URL;

    @Autowired
    private LunchPlaceService placeService;

    private final String areaId = testAreas.getFirstAreaId();
    private final LunchPlace PLACE3 = super.testPlaces.getPlace3();
    private final String PLACE3_ID = PLACE3.getId();
    private final LunchPlace PLACE4 = super.testPlaces.getPlace4();
    private final String PLACE4_ID = PLACE4.getId();

    @Test
    public void testUpdate() throws Exception {
        Set<String> phones = Sets.newHashSet("0661234567", "0441234567");
        LunchPlaceTo to = new LunchPlaceTo("Updated PLace", null, "Updated Description", phones);

        mockMvc.perform(
                put(REST_URL + "/{id}",  areaId, PLACE4_ID)
                .content(jsonUtils.toJson(to))
                .contentType(MediaType.APPLICATION_JSON)
                                .with(god())
        )
        .andExpect(status().isNoContent());

        verify(placeService).bulkUpdate(areaId, PLACE4_ID, to.getName(), to.getAddress(), to.getDescription(), to.getPhones());
    }

    @Before
    public void setUp() throws Exception {
        Mockito.reset(placeService);
    }

    @Test
    public void updateWhereNameIsDuplicated() throws Exception {
        String duplicatedName = PLACE3.getName();
        LunchPlaceTo to = new LunchPlaceTo(duplicatedName, null, "Updated Description", Collections.emptySet());

        doThrow(new DuplicateDataException(ErrorCode.DUPLICATE_PLACE_NAME, new Object[]{duplicatedName}))
                .when(placeService).bulkUpdate(any(), any(), any(), any(), any(), any());

        MvcResult result = mockMvc
                .perform(
                        put(REST_URL + "/{id}", areaId, PLACE4_ID)
                        .content(jsonUtils.toJson(to))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                                                .with(god())
                )
                .andExpect(status().isConflict()).andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.DUPLICATE_PLACE_NAME,
                "LunchPlace name " + duplicatedName + " already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testGet() throws Exception {
        when(placeService.getMultiple(any(), any()))
                .thenReturn(Collections.singletonList(testPlaces.getPlace4()));

        String actualJson = mockMvc
                .perform(
                        get(REST_URL + "/{id}", areaId, PLACE4_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMultiple(areaId, Collections.singleton(PLACE4_ID));

        Map<String, String> properties = new HashMap<>();
        properties.put("id", PLACE4_ID);
        properties.put("name", PLACE4.getName());

        String expectedJson = jsonUtils.toJson(properties);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void getWithFields() throws Exception {
        when(placeService.getMultipleWithMenu(any(), any(), any(), any()))
                    .thenReturn(Collections.singletonList(testPlaces.getPlace4()));

        String actualJson = mockMvc
                .perform(
                        get(REST_URL + "/{id}", areaId, PLACE4_ID)
                        .param("fields", "name,description,menus")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMultipleWithMenu(areaId, Collections.singleton(PLACE4_ID), null, null);

        Object obj = new Object() {
            String id = PLACE4.getId();
            String name = PLACE4.getName();
            String description = PLACE4.getDescription();
            Set<Menu> menus = new HashSet<>(testPlaces.getPlace4().getMenus());
        };
        String expectedJson = jsonUtils.toJson(obj);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void getByMenuDates() throws Exception {
        Set<Menu> expectedMenus = Sets.newHashSet(testPlaces.getMenu3(), testPlaces.getMenu4());
        when(placeService.getMultipleWithMenu(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(testPlaces.getPlace4().withMenus(expectedMenus)));

        String actualJson = mockMvc
                .perform(
                        get(REST_URL + "/{id}", areaId, PLACE4_ID)
                        .param("startDate", NOW_DATE.format(DateTimeFormatters.DATE_FORMATTER))
                        .param("fields", "menus")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMultipleWithMenu(areaId, Collections.singleton(PLACE4_ID), NOW_DATE, null);

        Object obj = new Object() {
            String id = PLACE4.getId();
            Set<Menu> menus = expectedMenus;
        };

        String expectedJson = jsonUtils.toJson(obj);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetMultiple() throws Exception {
        List<LunchPlace> places = Arrays.asList(PLACE4, PLACE3);

        when(placeService.getMultiple(any(), any()))
                .thenReturn(places);

        String actualJson = mockMvc
                .perform(
                        get(REST_URL, areaId)
                        .param("ids", PLACE3_ID + "," + PLACE4_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMultiple(areaId, Sets.newHashSet(PLACE3_ID, PLACE4_ID));

        List<Map<String,String>> placesValues = new ArrayList<>();
        for (LunchPlace lp : places) {
            Map<String, String> properties = new HashMap<>();
            properties.put("id", lp.getId());
            properties.put("name", lp.getName());
            placesValues.add(properties);
        }

        String expectedJson = jsonUtils.toJson(placesValues);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetMultipleByMenuDates() throws Exception {
        LunchPlace place3 = PLACE3.withMenus(Sets.newHashSet(testPlaces.getMenu6()));
        LunchPlace place4 = PLACE4.withMenus(Sets.newHashSet(testPlaces.getMenu1(), testPlaces.getMenu2()));

        when(placeService.getMultipleWithMenu(any(), any(), any(), any()))
                .thenReturn(Arrays.asList(place4, place3));

        String actualJson = mockMvc
                .perform(
                        get(REST_URL, areaId)
                        .param("ids", PLACE3_ID + "," + PLACE4_ID)
                        .param("fields", "menus, name")
                        .param("startDate", NOW_DATE.minusDays(2).format(DateTimeFormatters.DATE_FORMATTER))
                        .param("endDate", NOW_DATE.minusDays(1).format(DateTimeFormatters.DATE_FORMATTER))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMultipleWithMenu(areaId, Sets.newHashSet(PLACE3_ID, PLACE4_ID),
                NOW_DATE.minusDays(2), NOW_DATE.minusDays(1));

        Object obj1 = new Object() {
            String id = PLACE3.getId();
            String name = PLACE3.getName();
            Set<Menu> menus = place3.getMenus();
        };
        Object obj2 = new Object() {
            String id = PLACE4.getId();
            String name = PLACE4.getName();
            Set<Menu> menus = place4.getMenus();
        };

        String expectedJson = jsonUtils.toJson(Arrays.asList(obj1, obj2));

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void getAll() throws Exception {
        List<LunchPlace> places = testPlaces.getA1Places();

        when(placeService.getMultiple(areaId, Collections.emptySet()))
                .thenReturn(places);

        String actualJson = mockMvc
                .perform(
                        get(REST_URL, areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMultiple(areaId, Collections.emptySet());

        List<Map<String,String>> placesValues = new ArrayList<>();
        for (LunchPlace lp : places) {
            Map<String, String> properties = new HashMap<>();
            properties.put("id", lp.getId());
            properties.put("name", lp.getName());
            placesValues.add(properties);
        }

        String expectedJson = jsonUtils.toJson(placesValues);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetAllWithFields() throws Exception {
        List<LunchPlace> places = testPlaces.getA1Places();

        when(placeService.getMultiple(any(), any()))
                .thenReturn(places);

        String actualJson = mockMvc
                .perform(
                        get(REST_URL, areaId)
                        .param("fields", "name,description")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMultiple(areaId, Collections.emptySet());

        List<Map<String,String>> placesValues = new ArrayList<>();
        for (LunchPlace lp : places) {
            Map<String, String> properties = new HashMap<>();
            properties.put("id", lp.getId());
            properties.put("name", lp.getName());
            properties.put("description", lp.getDescription());
            placesValues.add(properties);
        }
        String expectedJson = jsonUtils.toJson(placesValues);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetMultipleOneNotExists() throws Exception {
        when(placeService.getMultipleWithMenu(any(), any(), any(), any()))
                .thenThrow(new NotFoundException(Sets.newHashSet("I_DO_NOT_EXIST"), LunchPlace.class));
        MvcResult result = mockMvc
                .perform(
                        get(REST_URL, areaId)
                        .param("ids", PLACE3_ID + "," + "I_DO_NOT_EXIST")
                        .param("fields", "menus, name")
                        .param("startDate", LocalDate.now().minusDays(2).format(DateTimeFormatters.DATE_FORMATTER))
                        .param("endDate", LocalDate.now().minusDays(1).format(DateTimeFormatters.DATE_FORMATTER))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.ENTITY_NOT_FOUND,
                "entity(-ies) not found: [I_DO_NOT_EXIST]"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(
                delete(REST_URL + "/{id}", areaId, PLACE4_ID)
                                                .with(god())
        )
                .andExpect(status().isNoContent());

        verify(placeService).delete(areaId, PLACE4_ID);
    }
}