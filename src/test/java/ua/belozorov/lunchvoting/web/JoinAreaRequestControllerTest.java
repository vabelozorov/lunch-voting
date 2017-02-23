package ua.belozorov.lunchvoting.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.FieldMappingEntry;
import ua.belozorov.ObjectToMapConverter;
import ua.belozorov.SimpleObjectToMapConverter;
import ua.belozorov.lunchvoting.mocks.ServicesTestConfig;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.service.area.JoinAreaRequestService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.UserTestData.*;
import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.02.17.
 */

public class JoinAreaRequestControllerTest extends AbstractControllerTest {
    static final String REST_URL = JoinAreaRequestController.REST_URL;

    @Autowired
    private JoinAreaRequestService requestService;

    private final ObjectToMapConverter<JoinAreaRequest> converter;

    private final String areaId = testAreas.getFirstAreaId();

    public JoinAreaRequestControllerTest() {
        this.converter = new SimpleObjectToMapConverter<>(
                new FieldMappingEntry<>("id", JoinAreaRequest::getId),
                new FieldMappingEntry<>("requester", (jar) -> jar.getRequester().getId()),
                new FieldMappingEntry<>("area", (jar) -> jar.getArea().getId()),
                new FieldMappingEntry<>("status", (jar) -> jar.getStatus().name()),
                new FieldMappingEntry<>("created", JoinAreaRequest::getCreated),
                new FieldMappingEntry<>("decidedOn", JoinAreaRequest::getDecidedOn)
        );
    }

    @Before
    public void resetMocks() throws Exception {
        Mockito.reset(requestService);
    }

    @Test
    public void testMakeRequest() throws Exception {
        JoinAreaRequest madeRequest = new JoinAreaRequest(ALIEN_USER1, testAreas.getFirstArea());
        when(requestService.make(ALIEN_USER1, areaId))
                .thenReturn(madeRequest);

        MvcResult mvcResult = mockMvc
                .perform(
                        post(REST_URL).param("id", areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(alien())
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(requestService).make(ALIEN_USER1, areaId);

        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = super.getCreatedId(location);

        when(requestService.getByRequester(ALIEN_USER1, id))
                .thenReturn(madeRequest);
        mockMvc.perform(get(location).with(alien()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(toMap("id", id));

        assertJson(expected, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void requesterCanRequestItsOwnRequest() throws Exception {
        JoinAreaRequest madeRequest = new JoinAreaRequest(ALIEN_USER1, testAreas.getFirstArea());

        when(requestService.getByRequester(ALIEN_USER1, madeRequest.getId()))
                .thenReturn(madeRequest);
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/{id}", madeRequest.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(alien())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(requestService).getByRequester(ALIEN_USER1, madeRequest.getId());

        Map<String, Object> objectProperties = this.converter.convert(madeRequest);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void adminCanGetAllAreaRequestsByStatus() throws Exception {
        EatingArea area = testAreas.getFirstArea();

        JoinAreaRequest request1 = new JoinAreaRequest(A2_USER1, area).approve();
        JoinAreaRequest request2 = new JoinAreaRequest(A2_USER2, area);
        JoinAreaRequest request3 = new JoinAreaRequest(A2_USER3, area);

        List<JoinAreaRequest> expectedRequests = Arrays.asList(request3, request2);
        when(requestService.getByStatus(area.getId(), JoinAreaRequest.JoinStatus.PENDING))
                .thenReturn(expectedRequests);

        String actual = mockMvc
                .perform(
                        get(REST_URL)
                        .param("status", "PENDING")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(requestService).getByStatus(area.getId(), JoinAreaRequest.JoinStatus.PENDING);

        List<Map<String, Object>> objectProperties = this.converter.convert(expectedRequests);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test//TODO when security
    public void notAdminCannotGetRequestsByStatus() throws Exception {

    }

    @Test
    public void requesterGetsItsOwnRequests() throws Exception {
        EatingArea area = testAreas.getFirstArea();
        JoinAreaRequest request1 = new JoinAreaRequest(VOTER, area).cancel();
        JoinAreaRequest request2 = new JoinAreaRequest(VOTER, area);
        JoinAreaRequest request3 = new JoinAreaRequest(ALIEN_USER2, area);
        List<JoinAreaRequest> expectedRequests = Arrays.asList(request2, request1);

        when(requestService.getByRequester(VOTER)).thenReturn(expectedRequests);

        String actual = mockMvc
                .perform(
                        get(REST_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(requestService).getByRequester(VOTER);

        List<Map<String, Object>> objectProperties = this.converter.convert(expectedRequests);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testApprove() throws Exception {
        String requestId = "req_id";
        mockMvc
                .perform(
                        put(REST_URL + "/{id}/approve", requestId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
                .andExpect(status().isNoContent());

        verify(requestService).approve(GOD, requestId);
    }

    @Test
    public void testReject() throws Exception {
        String requestId = "req_id";
        mockMvc
                .perform(
                        put(REST_URL + "/{id}/reject", requestId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
                .andExpect(status().isNoContent());

        verify(requestService).reject(GOD, requestId);
    }

    @Test
    public void testCancel() throws Exception {
        String requestId = "req_id";

        mockMvc
                .perform
                        (put("{base}/{id}/cancel", REST_URL, requestId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(alien())
                        )
                .andExpect(status().isNoContent());

        verify(requestService).cancel(ALIEN_USER1, requestId);
    }
}