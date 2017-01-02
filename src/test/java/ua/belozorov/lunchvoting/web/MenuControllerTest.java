package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.JsonUtils;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACE4_ID;
import static ua.belozorov.lunchvoting.testdata.MenuTestData.MENU1_ID;

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

        MvcResult result = mockMvc.perform(post(REST_URL + "/" + PLACE4_ID + "/menus/")
                .content(sentContent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String  uri = jsonUtils.locationFromMvcResult(result);
        String id = getCreatedId(uri);
    }

    @Test
    public void testDelete() throws Exception {
        assertFalse(repository.getMenu(MENU1_ID) == null);
        mockMvc.perform(delete(REST_URL + "/" + PLACE4_ID + "/menus/" + MENU1_ID))
                    .andExpect(status().isNoContent());
        assertTrue(repository.getMenu(MENU1_ID) == null);
    }
}