package ua.belozorov.lunchvoting.web;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.mocks.ServiceMocks;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 26.11.16.
 */
@ContextConfiguration(classes = ServiceMocks.class)
public class MenuControllerTest extends AbstractControllerTest {
    public static final String REST_URL = MenuController.REST_URL;

    @Autowired
    private LunchPlaceService placeService;

    @Autowired
    private MenuRepository menuRepository;
    private final String areaId = testAreas.getFirstAreaId();

    @Override
    public void beforeTest() {

    }

    @Test
    public void testCreate() throws Exception {
        Set<Dish> dishes = ImmutableSet.of(
                new Dish("First Dish", 20.21f, 0),
                new Dish("Second Dish", 10.12f, 1)
        );
        MenuTo sent = new MenuTo(NOW_DATE, dishes);

        when(placeService.addMenu(areaId, "placeId", NOW_DATE, dishes))
                .thenReturn(testPlaces.getMenu1());
        MvcResult result = mockMvc
                .perform(
                        post(REST_URL, areaId, "placeId")
                        .content( jsonUtils.toJson(sent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(placeService).addMenu(areaId, "placeId", NOW_DATE, dishes);

        String  uri = jsonUtils.locationFromMvcResult(result);
        String id = getCreatedId(uri);

        when(placeService.getMenu(areaId, "placeId", id, MenuRepositoryImpl.Fields.DISHES))
                .thenReturn(testPlaces.getMenu1());
        mockMvc.perform(get(uri).with(voter()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

        MvcResult result = mockMvc
                .perform(
                        post(REST_URL, "any", "any")
                        .content( jsonUtils.toJson(sent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
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
        when(placeService.getMenu(areaId, "FourthPlaceID", "menuId", MenuRepositoryImpl.Fields.DISHES))
                .thenReturn(testPlaces.getMenu1());
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/{id}",
                        areaId, "FourthPlaceID", "menuId")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(placeService).getMenu(areaId, "FourthPlaceID", "menuId", MenuRepositoryImpl.Fields.DISHES);

        MenuTo to = new MenuTo(testPlaces.getMenu1Id(), testPlaces.getMenu1().getEffectiveDate(), testPlaces.getMenu1().getDishes(), testPlaces.getPlace4Id());

        assertJson(
                jsonUtils.toJson(to),
                actual
        );
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(
                delete(REST_URL + "/{menuId}", areaId, "placeId", "menuId")
                .with(csrf())
                .with(god())
        )
        .andExpect(status().isNoContent());
        verify(placeService).deleteMenu(areaId, "placeId","menuId");
    }
}