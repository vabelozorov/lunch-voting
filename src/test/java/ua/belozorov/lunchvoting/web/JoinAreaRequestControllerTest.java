package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.FieldMappingEntry;
import ua.belozorov.ObjectToMapConverter;
import ua.belozorov.SimpleObjectToMapConverter;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.service.lunchplace.EatingAreaService;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.user.UserService;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
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
    private UserService userService;

    @Autowired
    private UserProfileService profileService;

    private final ObjectToMapConverter<JoinAreaRequest> converter;

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
        MvcResult mvcResult = mockMvc.perform(post(REST_URL).param("id", testAreas.getFirstAreaId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(mvcResult);
        String id = super.getCreatedId(location);

        mockMvc.perform(get(location)).andExpect(status().isOk());

        String expected = jsonUtils.toJson(toMap("id", id));

        assertJson(expected, mvcResult.getResponse().getContentAsString());

        assertNotNull(areaService.getJoinRequest(testAreas.getFirstAreaId(), id));
    }

    @Test
    public void requesterCanRequestItsOwnRequest() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = areaService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest request2 = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        AuthorizedUser.authorize(profileService.get(ALIEN_USER2.getId()));
        String actual = mockMvc.perform(get("{base}/{id}", REST_URL, request2.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JoinAreaRequest request = areaService.getJoinRequest(areaId, request2.getId());

        Map<String, Object> objectProperties = this.converter.convert(request);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void adminCanGetAllAreaRequestsByStatus() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = areaService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest request2 = areaService.makeJoinRequest(VOTER2, areaId);
        JoinAreaRequest request3 = areaService.makeJoinRequest(VOTER3, areaId);
        areaService.approveJoinRequest(userService.get(areaId, ALIEN_USER1.getId()), request1.getId());

        AuthorizedUser.authorize(profileService.get(ALIEN_USER1.getId()));
        String actual = mockMvc.perform(get(REST_URL).param("status", "PENDING").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<JoinAreaRequest> requests = areaService.getJoinRequestsByStatus(areaId, JoinAreaRequest.JoinStatus.PENDING);
        List<Map<String, Object>> objectProperties = this.converter.convert(requests);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test//TODO when security
    public void notAdminCannotGetRequestsByStatus() throws Exception {

    }

    @Test
    public void testGetByRequester() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = areaService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest request2 = areaService.makeJoinRequest(ALIEN_USER2, testAreas.getFirstArea().getId());
        JoinAreaRequest request3 = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        AuthorizedUser.authorize(profileService.get(ALIEN_USER1.getId()));
        String actual = mockMvc.perform(get(REST_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<JoinAreaRequest> requests = areaService.getJoinRequestsByRequester(ALIEN_USER1);

        List<Map<String, Object>> objectProperties = this.converter.convert(requests);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testApprove() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        AuthorizedUser.authorize(userService.get(areaId, ALIEN_USER1.getId()));
        mockMvc.perform(put("{base}/{id}/approve", REST_URL, request.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        JoinAreaRequest actual = areaService.getJoinRequest(areaId, request.getId());

        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.APPROVED);
    }

    @Test
    public void testReject() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        AuthorizedUser.authorize(userService.get(areaId, ALIEN_USER1.getId()));
        mockMvc.perform(put("{base}/{id}/reject", REST_URL, request.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        JoinAreaRequest actual = areaService.getJoinRequest(areaId, request.getId());

        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.REJECTED);
    }

    @Test
    public void testCancel() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        User cancelingUser = profileService.get(ALIEN_USER2.getId());
        AuthorizedUser.authorize(cancelingUser);
        mockMvc.perform(put("{base}/{id}/cancel", REST_URL, request.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        JoinAreaRequest actual = areaService.getJoinRequestByRequester(cancelingUser, request.getId());

        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.CANCELLED);
    }
}