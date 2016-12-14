package ua.belozorov.lunchvoting.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 25.11.16.
 */
public class LunchPlaceControllerTest extends AbstractControllerTest {
    private static final String REST_URL = LunchPlaceController.REST_URL;

    @Autowired
    private LunchPlaceService placeService;

    @Test
    public void testCreate() throws Exception {
        List<String> phones = Arrays.asList("0661234567", "0441234567");
        LunchPlaceTo to = new LunchPlaceTo(null, "New PLace", "New Street 12/12", "New Description", phones);

        MvcResult result = mockMvc.perform(post(REST_URL)
                .content(jsonUtils.toJson(to))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String uri = jsonUtils.locationFromMvcResult(result);
        String id = getCreatedId(uri);

        LunchPlace saved = DtoIntoEntity.toLunchPlace(to.toBuilder().id(id).build(), AuthorizedUser.get().getId());
        String expectedJson = jsonUtils.removeFields(saved, LunchPlaceController.EXCLUDED_FIELDS);

        String actualJson = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testUpdate() throws Exception {
        List<String> phones = Arrays.asList("0661234567", "0441234567");
        LunchPlaceTo to = new LunchPlaceTo(PLACE4_ID, "Updated PLace", "Updated Street 12/12", "Updated Description", phones);

        mockMvc.perform(put(REST_URL)
                .content(jsonUtils.toJson(to))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        LunchPlace expected = DtoIntoEntity.toLunchPlace(to, GOD_ID);
        assertThat(
                placeService.get(PLACE4_ID, GOD),
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void testGet() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL + "/" + PLACE4_ID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = jsonUtils.removeFields(PLACE4, LunchPlaceController.EXCLUDED_FIELDS);
        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetWithFields() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL + "/" + PLACE4_ID)
                    .param("fields", "name,description"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, String> properties = new HashMap<>();
        properties.put("id", PLACE4_ID);
        properties.put("name", PLACE4.getName());
        properties.put("description", PLACE4.getDescription());
        String expectedJson = jsonUtils.toJson(properties);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void getAll() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<LunchPlace> places = Stream.of(PLACE3, PLACE4)
                .sorted(Comparator.comparing(LunchPlace::getName))
                .collect(Collectors.toList());

        String expectedJson = jsonUtils.removeFieldsFromCollection(places, LunchPlaceController.EXCLUDED_FIELDS);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testGetAllWithFields() throws Exception {
        String actualJson = mockMvc.perform(get(REST_URL)
                    .param("fields", "name,description"))
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
            properties.put("description", lp.getDescription());
            placesValues.add(properties);
        }
        String expectedJson = jsonUtils.toJson(placesValues);

        assertJson(expectedJson, actualJson);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + "/" + PLACE4_ID))
                .andExpect(status().isNoContent());
        Collection<LunchPlace> actual = placeService.getAll(AuthorizedUser.get());

        assertThat(
                actual,
                contains(matchCollection(Collections.singletonList(PLACE3), LUNCH_PLACE_COMPARATOR))
        );
    }
    //TODO test response codes and content during errors
}