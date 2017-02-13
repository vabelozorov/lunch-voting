package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.FieldMappingEntry;
import ua.belozorov.SimpleObjectToMapConverter;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.AreaTestData;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.service.lunchplace.EatingAreaService;
import ua.belozorov.lunchvoting.util.ControllerUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.Code;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.UserTestData.ALIEN_USER1;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.AREA_COMPARATOR;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.02.17.
 */
public class EatingAreaControllerTest extends AbstractControllerTest {
    private static final String REST_URL = EatingAreaController.REST_URL;

    @Autowired
    private EatingAreaService areaService;

    @Test
    public void testCreate() throws Exception {
        AuthorizedUser.authorize(ALIEN_USER1);
        MvcResult mvcResult = mockMvc.perform(post(REST_URL).param("name", "Name").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = getCreatedId(location);

        mockMvc.perform(get(location)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));
        assertJson(expected, mvcResult.getResponse().getContentAsString());

        assertNotNull(areaService.getArea(id));
    }

    @Test
    public void e409AndMessageIfViolatesUniqueNameConstraint() throws Exception {
        AuthorizedUser.authorize(ALIEN_USER1);
        MvcResult result = mockMvc.perform(post(REST_URL).param("name", testAreas.getFirstAreaName()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                Code.DUPLICATE_DATA,
                "Area name " + testAreas.getFirstAreaName() + " already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testUpdate() throws Exception {
        mockMvc.perform(put(REST_URL).param("name", "NEW_AWESOME_NAME"))
                .andExpect(status().isNoContent());
        EatingArea updated = areaService.getArea(testAreas.getFirstAreaId());

        assertThat(updated, matchSingle(testAreas.getFirstArea().changeName("NEW_AWESOME_NAME"), AREA_COMPARATOR));
    }


    @Test
    public void testGetFullDto() throws Exception {
        String actual = mockMvc.perform(get("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                    .param("summary", "false")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected = jsonUtils.toJson(AreaTestData.dto(testAreas.getFirstArea()));

        assertJson(expected, actual);
    }

    @Test
    public void testGetSummaryDto() throws Exception {
        String actual = mockMvc.perform(get("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expected = jsonUtils.toJson(AreaTestData.dtoSummary(testAreas.getFirstArea()));

        assertJson(expected, actual);
    }

    @Test
    public void testFilterByName() throws Exception {
        EatingArea chow1 = areaService.create("ChowArea", ALIEN_USER1);
        EatingArea chow2 = areaService.create("ChowAreaNew", ALIEN_USER1);
        String actual = mockMvc.perform(get("{base}/filter", REST_URL).param("name", "Chow")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> convert = new SimpleObjectToMapConverter<>(
                new FieldMappingEntry<>("id", EatingArea::getId),
                new FieldMappingEntry<>("name", EatingArea::getName),
                new FieldMappingEntry<>("created", EatingArea::getCreated)
        ).convert(chow1, chow2);
        String expected = jsonUtils.toJson(convert);

        assertJson(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("{base}/{id}", REST_URL, testAreas.getFirstAreaId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        thrown.expect(NotFoundException.class);
        areaService.getArea(testAreas.getFirstAreaId());
    }

}