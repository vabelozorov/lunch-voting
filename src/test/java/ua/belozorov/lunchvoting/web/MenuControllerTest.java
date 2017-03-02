package ua.belozorov.lunchvoting.web;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**

 *
 * Created on 26.11.16.
 */
public class MenuControllerTest extends AbstractControllerTest {
    public static final String REST_URL = MenuController.REST_URL;

    @Autowired
    private LunchPlaceService placeService;

    private final String areaId = testAreas.getFirstAreaId();

    @Before
    public void setUp() throws Exception {
        Mockito.reset(placeService);
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
                                .with(god())
        )
        .andExpect(status().isNoContent());
        verify(placeService).deleteMenu(areaId, "placeId","menuId");
    }
}