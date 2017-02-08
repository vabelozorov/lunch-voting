package ua.belozorov.lunchvoting.web;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ControllerUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.Code;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static ua.belozorov.lunchvoting.testdata.UserTestData.*;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */

public class UserManagementControllerTest extends AbstractControllerTest {
    private static final String REST_URL = UserManagementController.REST_URL;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void createMethodReturnsLocationUrlHeaderAndIdInBody() throws Exception {
        UserTo userTo = new UserTo("New User", "new@email.com", "strongPassword");
        MvcResult result = mockMvc
                .perform(post(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(result);
        String id = super.getCreatedId(location);
        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));

        assertJson(expected, result.getResponse().getContentAsString());

        String got = mockMvc.perform(get("{base}/{id}", REST_URL, id).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        UserTo to = jsonUtils.strToObject(got, UserTo.class);

        assertTrue(to.getId().equals(id));
    }

    @Test
    public void e409AndMessageOnCreateWithDuplicateEmail() throws Exception {
        UserTo userTo = new UserTo("New User", "god@email.com", "strongPassword");
        MvcResult result = mockMvc
                .perform(post(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                Code.DUPLICATE_EMAIL,
                "Email god@email.com already exists"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void e400AndMessageOnCreateWithValidationFails() throws Exception {
        UserTo userTo = new UserTo("", "god1@email.com", "strongPassword");
        MvcResult result = mockMvc
                .perform(post(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                Code.PARAMS_VALIDATION_FAILED,
                "name may not be empty"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testUpdate() throws Exception {
        UserTo userTo = new UserTo(VOTER_ID, VOTER.getName(), "newEmail@email.com", "newPassword");

        mockMvc.perform(put(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);

        assertEquals(userTo.getPassword(), user.getPassword());
        assertEquals(userTo.getEmail(), user.getEmail());
    }

    @Test
    public void testGet() throws Exception {
        String actual = mockMvc.perform(get(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expected = jsonUtils.toJson(new UserTo(VOTER));
        assertJson(expected, actual);
    }

    @Test
    public void e404AndMessageOnGetNonExistingId() throws Exception {
        MvcResult result = mockMvc.perform(get("{base}/{id}", REST_URL, "I_DO_NOT_EXIST"))
                .andExpect(status().isNotFound())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                Code.ENTITY_NOT_FOUND,
                "Entity(-ies) not found: I_DO_NOT_EXIST"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testGetAll() throws Exception {
        String actual = mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<UserTo> tos = ALL_USERS.stream()
                .map(UserTo::new)
                .sorted(Comparator.comparing(UserTo::getEmail))
                .collect(Collectors.toList());
        String expected = jsonUtils.toJson(tos);

        assertJson(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        assertNotNull(userService.get(VOTER_ID));
        mockMvc.perform(delete(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isNoContent());
        thrown.expect(NotFoundException.class);
        userService.get(VOTER_ID);
    }

    @Test
    public void deactivateUser() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", VOTER_ID);
        map.put("activated", false);
        mockMvc.perform(put(REST_URL  + "/activate")
                            .content(jsonUtils.toJson(map))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);
        assertFalse(user.isActivated());
    }

    @Test
    public void setRoles() throws Exception {
        Set<UserRole> expectedRoles = new HashSet<>();
        expectedRoles.add(UserRole.VOTER);
        expectedRoles.add(UserRole.ADMIN);

        Map<String, Object> map = new HashMap<>();
        map.put("id", VOTER_ID);
        map.put("roles", expectedRoles);
        mockMvc.perform(put(REST_URL + "/roles")
                .content(jsonUtils.toJson(map))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);

        assertEquals(user.getRoles(), expectedRoles);
    }
}