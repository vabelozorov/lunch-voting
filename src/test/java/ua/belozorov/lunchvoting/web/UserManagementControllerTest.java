package ua.belozorov.lunchvoting.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.to.transformers.UserTransformer;
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
import static ua.belozorov.lunchvoting.MatcherUtils.*;
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
                messageSource.getMessage(
                        "error.duplicate_email",
                        new Object[]{userTo.getEmail()},
                        LocaleContextHolder.getLocale()
                )
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
        UserTo userTo = UserTransformer.toDto(VOTER);
        userTo.setPassword("newPassword");
        userTo.setEmail("newEmail@email.com");

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
        MvcResult result = mockMvc.perform(get(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isOk())
                .andReturn();

        UserTo actual = jsonUtils.fromMvcResultBody(result, UserTo.class);
        UserTo expected = UserTransformer.toDto(VOTER);
        assertThat(actual, matchByToString(expected));
    }

    @Test
    public void testGetAll() throws Exception {
        MvcResult result = mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andReturn();

        Collection<UserTo> actual = jsonUtils.fromMvcResultBody(result, new TypeReference<Collection<UserTo>>() {});
        List<UserTo> expected = ALL_USERS.stream()
                .map(UserTransformer::toDto)
                .sorted(Comparator.comparing(UserTo::getEmail))
                .collect(Collectors.toList());

        assertReflectionEquals(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isNoContent());
        Collection<User> actual = userService.getAll();
        Collection<User> expected = ALL_USERS.stream()
                                                .filter(u -> !u.getId().equals(VOTER_ID))
                                                .collect(Collectors.toList());

        assertThat(
                actual,
                contains(matchCollection(expected, USER_COMPARATOR))
        );
    }

    @Test
    public void activate() throws Exception {
        mockMvc.perform(put(REST_URL + "/" + VOTER_ID + "/activate")
                            .content(jsonUtils.toJson("isActive", "false"))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);
        assertFalse(user.isActivated());
    }

    @Test
    public void setRights() throws Exception {
        mockMvc.perform(put(REST_URL + "/" + VOTER_ID + "/rights.set")
                .content(jsonUtils.toJson("rights", "3"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);
        Set<UserRole> expectedRoles = new HashSet<>();
        expectedRoles.add(UserRole.VOTER);
        expectedRoles.add(UserRole.ADMIN);
        assertEquals(UserRole.toUserRoles(user.getRoles()), expectedRoles);
    }
}