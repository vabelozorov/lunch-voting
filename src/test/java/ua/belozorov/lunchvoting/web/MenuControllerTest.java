package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;
import ua.belozorov.CollectionMappingEntry;
import ua.belozorov.FieldMappingEntry;
import ua.belozorov.SimpleObjectToMapConverter;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                        private int position = 0;
                    },
                    new Object() {
                        private String name = "Second Dish";
                        private float price = 10.12f;
                        private int position = 1;
                    }
            );
        };

        MvcResult result = mockMvc.perform(post(REST_URL, testAreas.getFirstAreaId(),testPlaces.getPlace4Id())
                    .content( jsonUtils.toJson(sent))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String  uri = jsonUtils.locationFromMvcResult(result);
        String id = getCreatedId(uri);

        mockMvc.perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertNotNull(service.getMenu(testAreas.getFirstAreaId(), testPlaces.getPlace4Id(), id));
    }

    @Test
    public void createWithWrongDishFails() throws Exception {
        Object sent = new Object() {
            private List<Object> dishes = Arrays.asList(
                    new Object() {
                        private int position = 1;
                    },
                    new Object() {
                        private String name = "Second Dish";
                        private float price = 10.12f;
                    }
            );
        };

        MvcResult result = mockMvc.perform(post(REST_URL, testAreas.getFirstAreaId(),testPlaces.getPlace4Id())
                .content( jsonUtils.toJson(sent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.PARAMS_VALIDATION_FAILED,
                "field 'effectiveDate', rejected value 'null', reason: may not be null\n" +
                        "field 'dishes[0].name', rejected value '', reason: must be provided and not empty\n" +
                        "field 'dishes[0].price', rejected value '-1.0', reason: must be provided and greater or equal 0\n" +
                        "field 'dishes[1].position', rejected value '-1', reason: must be provided and greater or equal 0"
        );

        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void getMenuWithDishes() throws Exception {
        String actual = mockMvc.perform(get(REST_URL + "/{id}",
                testAreas.getFirstAreaId(), testPlaces.getPlace4Id(), testPlaces.getMenu1Id())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MenuTo to = new MenuTo(testPlaces.getMenu1Id(), testPlaces.getMenu1().getEffectiveDate(), testPlaces.getMenu1().getDishes(), testPlaces.getPlace4Id());

        assertJson(
                jsonUtils.toJson(to),
                actual
        );
    }

    @Test
    public void testDelete() throws Exception {
        String place4Id = testPlaces.getPlace4Id();

        assertNotNull(repository.getMenu(testAreas.getFirstAreaId(), place4Id, testPlaces.getMenu1Id()));

        mockMvc.perform(
                    delete(REST_URL + "/{menuId}",
                            testAreas.getFirstAreaId(),testPlaces.getPlace4Id(), testPlaces.getMenu1Id()))
                    .andExpect(status().isNoContent());

        assertNull(repository.getMenu(testAreas.getFirstAreaId(), place4Id, testPlaces.getMenu1Id()));
    }
}