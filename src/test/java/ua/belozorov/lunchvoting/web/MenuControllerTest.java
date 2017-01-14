package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 26.11.16.
 */
public class MenuControllerTest extends AbstractControllerTest {
    public static final String REST_URL = MenuController.REST_URL;

    @Autowired
    private LunchPlaceService service;

    @Autowired
    private MenuRepository repository;

    @Test
    public void testCreate() throws Exception {
        Object sent = new Object() {
            private LocalDate effectiveDate = LocalDate.now();
            private List<Object> dishes = Arrays.asList(
                    new Object() {
                        private String name = "First Dish";
                        private float price = 20.21f;
                    },
                    new Object() {
                        private String name = "Second Dish";
                        private float price = 10.12f;
                    }
            );
        };

        String sentContent = jsonUtils.toJson(sent);

        MvcResult result = mockMvc.perform(post(REST_URL + "/" + placeTestData.getPlace4Id() + "/menus/")
                .content(sentContent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String  uri = jsonUtils.locationFromMvcResult(result);
        String id = getCreatedId(uri);
    }

    @Test
    public void testDelete() throws Exception {
        assertFalse(repository.getMenu(placeTestData.getMenu1Id()) == null);
        mockMvc.perform(delete(REST_URL + "/" + placeTestData.getPlace4Id() + "/menus/" + placeTestData.getMenu1Id()))
                    .andExpect(status().isNoContent());
        assertTrue(repository.getMenu(placeTestData.getMenu1Id()) == null);
    }
}