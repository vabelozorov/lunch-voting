package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.FieldMappingEntry;
import ua.belozorov.ObjectToMapConverter;
import ua.belozorov.SimpleObjectToMapConverter;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.service.area.EatingAreaService;
import ua.belozorov.lunchvoting.service.area.JoinAreaRequestService;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.user.UserService;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
    private EatingAreaService areaService;

    @Autowired
    private JoinAreaRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService profileService;

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

    @Test
    public void testMakeRequest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(
                        post(REST_URL).param("id", areaId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(alien())
                )
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = super.getCreatedId(location);

        mockMvc.perform(get(location).with(alien()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(toMap("id", id));

        assertJson(expected, mvcResult.getResponse().getContentAsString());

        assertNotNull(areaService.getRepository().getJoinRequest(testAreas.getFirstAreaId(), id));
    }

    @Test
    public void requesterCanRequestItsOwnRequest() throws Exception {
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER1, areaId));

        String actual = mockMvc
                .perform(
                        get(REST_URL + "/{id}", request.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(alien())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JoinAreaRequest requestFromDb = areaService.getRepository().getJoinRequest(areaId, request.getId());

        Map<String, Object> objectProperties = this.converter.convert(requestFromDb);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void adminCanGetAllAreaRequestsByStatus() throws Exception {
        JoinAreaRequest request1 = asVoter(() -> requestService.make(A2_USER1, areaId));
        JoinAreaRequest request2 = asVoter(() -> requestService.make(A2_USER2, areaId));
        JoinAreaRequest request3 = asVoter(() -> requestService.make(A2_USER3, areaId));
        asAdmin(() -> {
            requestService.approve(GOD, request1.getId());
            return null;
        });

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

        List<JoinAreaRequest> requests = areaService.getRepository()
                .getJoinRequestsByStatus(areaId, JoinAreaRequest.JoinStatus.PENDING);
        List<Map<String, Object>> objectProperties = this.converter.convert(requests);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test//TODO when security
    public void notAdminCannotGetRequestsByStatus() throws Exception {

    }

    @Test
    public void requesterGetsItsOwnRequests() throws Exception {
        JoinAreaRequest request1 = asVoter(() -> requestService.make(VOTER, areaId));
        asVoter(() -> {
            requestService.cancel(VOTER, request1.getId());
            return null;
        });
        JoinAreaRequest request2 = asVoter(() -> requestService.make(VOTER, areaId));
        JoinAreaRequest request3 = asVoter(() -> requestService.make(ALIEN_USER2, areaId));

        String actual = mockMvc
                .perform(
                        get(REST_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<JoinAreaRequest> requests = areaService.getRepository()
                                            .getJoinRequestsByRequester(VOTER.getId());

        List<Map<String, Object>> objectProperties = this.converter.convert(requests);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testApprove() throws Exception {
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER2, areaId));

        mockMvc
                .perform(
                        put(REST_URL + "/{id}/approve", request.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
                .andExpect(status().isNoContent());
        JoinAreaRequest actual = areaService.getRepository().getJoinRequest(areaId, request.getId());

        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.APPROVED);
    }

    @Test
    public void testReject() throws Exception {
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER2, areaId));

        mockMvc
                .perform(
                        put(REST_URL + "/{id}/reject", request.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
                .andExpect(status().isNoContent());
        JoinAreaRequest actual = areaService.getRepository()
                                    .getJoinRequest(areaId, request.getId());

        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.REJECTED);
    }

    @Test
    public void testCancel() throws Exception {
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER1, areaId));

        mockMvc
                .perform
                        (put("{base}/{id}/cancel", REST_URL, request.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(alien())
                        )
                .andExpect(status().isNoContent());
        JoinAreaRequest actual = areaService.getRepository()
                                    .getJoinRequestByRequester(ALIEN_USER1.getId(), request.getId());

        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.CANCELLED);
    }
}