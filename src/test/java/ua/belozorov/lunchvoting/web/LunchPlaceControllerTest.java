package ua.belozorov.lunchvoting.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.JsonUtils;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.to.transformers.LunchPlaceTransformer;
import ua.belozorov.lunchvoting.to.transformers.UserTransformer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static ua.belozorov.lunchvoting.MatcherUtils.matchByToString;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD;
import static ua.belozorov.lunchvoting.testdata.UserTestData.USER_COMPARATOR;

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
        LunchPlaceTo expected = new LunchPlaceTo(null, "New PLace", "New Street 12/12", "New Description", phones);

        MvcResult result = mockMvc.perform(post(REST_URL)
                .content(JsonUtils.toJson(expected))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        LunchPlaceTo actual = JsonUtils.mvcResultToObject(result, LunchPlaceTo.class);
        phones = phones.stream().sorted().collect(Collectors.toList());
        expected.setId(actual.getId());
        expected.setPhones(phones);

        assertReflectionEquals(expected, actual);
    }

    @Test
    public void testUpdate() throws Exception {
        List<String> phones = Arrays.asList("0661234567", "0441234567");
        LunchPlaceTo expected = new LunchPlaceTo(PLACE4_ID, "Updated PLace", "Updated Street 12/12", "Updated Description", phones);

        phones = phones.stream().sorted().collect(Collectors.toList());
        expected.setPhones(phones);

        mockMvc.perform(put(REST_URL)
                .content(JsonUtils.toJson(expected))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        LunchPlaceTo actual = placeService.get(PLACE4_ID, GOD);
        assertReflectionEquals(expected, actual);
    }

    @Test
    public void testGet() throws Exception {
        MvcResult result = mockMvc.perform(get(REST_URL + "/" + PLACE4_ID))
                .andExpect(status().isOk()).andReturn();

        LunchPlaceTo actual = JsonUtils.mvcResultToObject(result, LunchPlaceTo.class);
        LunchPlaceTo expected = LunchPlaceTransformer.toDto(PLACE4);

        assertThat(actual, matchByToString(expected));
    }

    @Test
    public void getAll() throws Exception {
        MvcResult result = mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andReturn();

        Collection<LunchPlaceTo> actual = JsonUtils.mvcResultToObject(result, new TypeReference<Collection<LunchPlaceTo>>() {});
        List<LunchPlaceTo> expected = Stream.of(PLACE3, PLACE4)
                .map(LunchPlaceTransformer::toDto)
                .sorted(Comparator.comparing(LunchPlaceTo::getName))
                .collect(Collectors.toList());

        assertReflectionEquals(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + "/" + PLACE4_ID))
                .andExpect(status().isNoContent());

        Collection<LunchPlaceTo> actual = placeService.getAll(AuthorizedUser.get());
        Collection<LunchPlaceTo> expected = LunchPlaceTransformer.collectionToDto(Collections.singletonList(PLACE3));
        assertReflectionEquals(expected, actual);
    }

    //TODO test response codes and content during errors

}