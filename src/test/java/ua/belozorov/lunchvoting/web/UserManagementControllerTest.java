package ua.belozorov.lunchvoting.web;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.mocks.ServicesTestConfig;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.UserTestData.*;


/**
 * <h2></h2>
 *
 * Created on 17.11.16.
 */
public class UserManagementControllerTest extends AbstractControllerTest {
    private static final String REST_URL = UserManagementController.REST_URL;

    @Autowired
    private UserService userService;

    private final String areaId = testAreas.getFirstAreaId();

    @Before
    public void setUp() throws Exception {
        Mockito.reset(userService);
    }

    @Test
    public void updateUserEmailAndPassword() throws Exception {
        UserTo userTo = new UserTo(VOTER.getName(), "newEmail@email.com", "newPassword");

        mockMvc.perform(
                put(REST_URL + "/{userId}", testAreas.getFirstAreaId(), VOTER_ID)
                .content(jsonUtils.toJson(userTo))
                .contentType(MediaType.APPLICATION_JSON)
                .with(god())
                .with(csrf()))
        .andExpect(status().isNoContent());
        verify(userService)
                .updateMainInfo(areaId, VOTER_ID, userTo.getName(), userTo.getEmail(), userTo.getPassword());
    }

    @Test
    public void getUser() throws Exception {
        when(userService.get(areaId, VOTER_ID)).thenReturn(VOTER);
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/{voter}", testAreas.getFirstAreaId(),  VOTER_ID)
                        .with(god())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        verify(userService).get(areaId, VOTER_ID);

        String expected = jsonUtils.toJson(new UserTo(VOTER));
        assertJson(expected, actual);
    }

    @Test
    public void e404AndMessageOnGetNonExistingId() throws Exception {
        when(userService.get(any(), any())).thenThrow(new NotFoundException("I_DO_NOT_EXIST", User.class));
        MvcResult result = mockMvc
                .perform(
                        get(REST_URL + "/{id}", testAreas.getFirstAreaId(), "I_DO_NOT_EXIST")
                        .with(god())
                )
                .andExpect(status().isNotFound())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.ENTITY_NOT_FOUND,
                "entity(-ies) not found: I_DO_NOT_EXIST"
        );
        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    @Test
    public void testGetAll() throws Exception {
        when(userService.getAll(areaId)).thenReturn(A1_USERS);

        String actual = mockMvc
                .perform(
                        get(REST_URL, testAreas.getFirstAreaId())
                        .with(god())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<UserTo> tos = A1_USERS.stream()
                .map(UserTo::new)
                .sorted(Comparator.comparing(UserTo::getEmail))
                .collect(Collectors.toList());
        String expected = jsonUtils.toJson(tos);

        assertJson(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc
                .perform(
                        delete(REST_URL + "/{voter}", testAreas.getFirstAreaId(), VOTER_ID)
                        .with(god())
                        .with(csrf())
                )
                .andExpect(status().isNoContent());
        verify(userService).delete(areaId, VOTER_ID);
    }

    @Test
    public void deactivateUser() throws Exception {
        mockMvc.perform(
                put(REST_URL  + "/{id}", testAreas.getFirstAreaId(), VOTER_ID)
                .param("activated", "false")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(god())
                .with(csrf())
        )
        .andExpect(status().isNoContent());

        verify(userService).activate(areaId, VOTER_ID, false);
    }

    @Test
    public void setRoles() throws Exception {
        Set<UserRole> expectedRoles = Sets.newHashSet(UserRole.VOTER, UserRole.ADMIN);
        String rolesString = expectedRoles.stream().map(UserRole::name).collect(Collectors.joining(","));

        mockMvc.perform(
                put(REST_URL + "/{id}", testAreas.getFirstAreaId(), VOTER_ID)
                .param("roles", rolesString)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(god())
                .with(csrf())
        )
        .andExpect(status().isNoContent());

        verify(userService).setRoles(areaId, VOTER_ID, expectedRoles);
    }
}