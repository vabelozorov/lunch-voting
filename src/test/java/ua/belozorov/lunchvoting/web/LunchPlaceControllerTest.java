package ua.belozorov.lunchvoting.web;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.DateTimeFormatters;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.model.UserTestData.GOD;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 25.11.16.
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
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567");
        LunchPlaceTo to = new LunchPlaceTo("Updated PLace", null, "Updated Description", phones);

        mockMvc.perform(put(REST_URL + "/{id}",  areaId, PLACE4_ID)
                .content(jsonUtils.toJson(to))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        LunchPlace expected = PLACE4.toBuilder().name("Updated PLace").description("Updated Description")
                .phones(phones).build();
        assertThat(
                placeService.get(areaId, PLACE4_ID),
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void updateWhereNameIsDuplicated() throws Exception {
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567");
        String duplicatedName = PLACE3.getName();
        LunchPlaceTo to = new LunchPlaceTo(duplicatedName, null, "Updated Description", phones);

        MvcResult result = mockMvc.perform(put(REST_URL + "/{id}", areaId, PLACE4_ID)
                .content(jsonUtils.toJson(to))
                .contentType(MediaType.APPLICATION_JSON))
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
        String actualJson = mockMvc.perform(get(REST_URL + "/{id}", areaId, PLACE4_ID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, String> properties = new HashMap<>();
        properties.put("id", PLACE4_ID);
        properties.put("name", PLACE4.getName());

        String expectedJson = jsonUtils.toJson(properties);
        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetWithFields() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL + "/{id}", areaId, PLACE4_ID)
                .param("fields", "name,description,menus"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Object obj = new Object() {
            String id = PLACE4.getId();
            String name = PLACE4.getName();
            String description = PLACE4.getDescription();
            Set<Menu> menus = new HashSet<>(Arrays.asList(
                    testPlaces.getMenu1(), testPlaces.getMenu2(),
                    testPlaces.getMenu3(), testPlaces.getMenu4()));
        };
        String expectedJson = jsonUtils.toJson(obj);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetByMenuDates() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL + "/{id}", areaId, PLACE4_ID)
                    .param("startDate", LocalDate.now().format(DateTimeFormatters.WEB_DATE_FORMATTER))
                    .param("fields", "menus")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Object obj = new Object() {
            String id = PLACE4.getId();
            Set<Menu> menus = new HashSet<>(Arrays.asList(testPlaces.getMenu3(), testPlaces.getMenu4()));
        };

        String expectedJson = jsonUtils.toJson(obj);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetMultiple() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL, areaId)
                        .param("ids", PLACE3_ID + "," + PLACE4_ID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<LunchPlace> places = Stream.of(PLACE3, PLACE4)
                .sorted(Comparator.comparing(LunchPlace::getName))
                .collect(Collectors.toList());

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
        String actualJson = mockMvc.perform(get(REST_URL, areaId)
                    .param("ids", PLACE3_ID + "," + PLACE4_ID)
                    .param("fields", "menus, name")
                    .param("startDate", LocalDate.now().minusDays(2).format(DateTimeFormatters.WEB_DATE_FORMATTER))
                    .param("endDate", LocalDate.now().minusDays(1).format(DateTimeFormatters.WEB_DATE_FORMATTER))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Object obj1 = new Object() {
            String id = PLACE3.getId();
            String name = PLACE3.getName();
            Set<Menu> menus = new HashSet<>(Arrays.asList(testPlaces.getMenu6()));
        };
        Object obj2 = new Object() {
            String id = PLACE4.getId();
            String name = PLACE4.getName();
            Set<Menu> menus = new HashSet<>(Arrays.asList(testPlaces.getMenu1(), testPlaces.getMenu2()));
        };

        String expectedJson = jsonUtils.toJson(Arrays.asList(obj1, obj2));

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void getAll() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL, areaId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<LunchPlace> places = testPlaces.getA1Places().stream().sorted().collect(Collectors.toList());

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
        String actualJson = mockMvc.perform(get(REST_URL, areaId)
                    .param("fields", "name,description"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<LunchPlace> places = testPlaces.getA1Places().stream().sorted().collect(Collectors.toList());
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
        MvcResult result = mockMvc.perform(get(REST_URL, areaId)
                .param("ids", PLACE3_ID + "," + "I_DO_NOT_EXIST")
                .param("fields", "menus, name")
                .param("startDate", LocalDate.now().minusDays(2).format(DateTimeFormatters.WEB_DATE_FORMATTER))
                .param("endDate", LocalDate.now().minusDays(1).format(DateTimeFormatters.WEB_DATE_FORMATTER))
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
        mockMvc.perform(delete(REST_URL + "/{id}", areaId, PLACE4_ID))
                .andExpect(status().isNoContent());
        Collection<LunchPlace> actual = placeService.getAll(areaId);
        List<LunchPlace> expected = testPlaces.getA1Places().stream().filter(lp -> !lp.getId().equals(PLACE4_ID)).collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, LUNCH_PLACE_COMPARATOR))
        );
    }
    //TODO test response codes and content during errors
}