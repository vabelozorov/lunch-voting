package ua.belozorov.lunchvoting.service.user;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ua.belozorov.lunchvoting.WithMockAdmin;
import ua.belozorov.lunchvoting.WithMockVoter;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.util.*;
import java.util.stream.Collectors;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.UserTestData.*;

/**
 * <h2></h2>
 *
 * Created on 17.11.16.
 */
@WithMockAdmin
public class UserServiceTest extends AbstractServiceTest {

    @Autowired
    private UserService userService;

    private final String areaId;

    public UserServiceTest() {
        this.areaId = testAreas.getFirstAreaId();
    }

    @Test
    public void createWithoutArea() throws Exception {
        User expected = new User("New User", "new@email.com", "strongPassword");

        reset();
        User actual = userService.create(expected);
        assertInsertCount(1);

        expected = expected.toBuilder()
                .registeredDate(actual.getRegisteredDate())
                .activated(true)
                .build();
        assertThat(actual, matchSingle(expected, USER_COMPARATOR));
    }

    @Test
    public void get() throws Exception {
        reset();
        User actual = userService.get(areaId, VOTER_ID);
        assertSelectCount(1);
        assertThat(actual, matchSingle(VOTER, USER_COMPARATOR));
    }

    @Test
    public void getAll() throws Exception {
        reset();
        Collection<User> users = userService.getAll(areaId);
        assertSelectCount(1);
        assertThat(
                A1_USERS,
                contains(matchCollection(users, USER_COMPARATOR))
        );
    }

    @Test
    public void delete() throws Exception {
        reset();
        userService.delete(areaId, ADMIN_ID);
        assertDeleteCount(1);

        Collection<User> users = userService.getAll(areaId);
        assertThat(
            A1_USERS.stream()
                    .filter(u -> !u.getId().equals(ADMIN_ID))
                    .collect(Collectors.toList()),
            contains(matchCollection(users, USER_COMPARATOR))
        );
    }

    @Test
    public void update() throws Exception {
        User updated = userService.get(areaId, VOTER_ID);
        updated = updated.toBuilder().password("newPassword").email("updated@email.com").build();

        reset();
        userService.updateMainInfo(areaId, updated.getId(), updated.getName(), updated.getEmail(), updated.getPassword());
        assertSelectCount(1);
        assertUpdateCount(1);

        assertThat(userService.get(areaId, VOTER_ID), matchSingle(updated, USER_COMPARATOR));
    }

    @Test
    @WithMockAdmin
    public void activate() throws Exception {
        reset();
        userService.activate(areaId, VOTER_ID, false);
        assertSelectCount(1);
        assertUpdateCount(1);

        assertFalse(userService.get(areaId, VOTER_ID).isActivated());
    }

    @Test
    @WithMockAdmin
    public void setRoles() throws Exception {
        Set<UserRole> expectedRoles = new HashSet<>();
        expectedRoles.add(UserRole.VOTER);
        expectedRoles.add(UserRole.ADMIN);

        reset();
        userService.setRoles(areaId, VOTER_ID, expectedRoles);
        assertSelectCount(1);
        assertUpdateCount(1);

        assertEquals(userService.get(areaId, VOTER_ID).getRoles(), expectedRoles);
    }

    @Test
    @WithMockAdmin
    public void getUsersInAreaByRole() throws Exception {
        reset();
        List<User> actual = userService.getUsersByRole(areaId, UserRole.ADMIN);
        assertSelectCount(1);

        List<User> expected = Arrays.asList(ADMIN, GOD);

        assertThat(actual, contains(matchCollection(expected, USER_COMPARATOR)));
    }

    @Test(expected = NotFoundException.class)
    @WithMockAdmin
    public void getNotExisting() {
        userService.get(areaId, "NotExistingId");
    }

    @Test(expected = NotFoundException.class)
    @WithMockAdmin
    public void updateNotExisting() {
        User updated = VOTER.toBuilder().id("NOT_EXISTING_ID").password("newPassword").email("updated@email.com").build();
        userService.updateMainInfo(areaId, updated.getId(), updated.getName(), updated.getEmail(), updated.getPassword());
    }

    @Test(expected = NotFoundException.class)
    @WithMockAdmin
    public void deleteNotExisting() {
        userService.delete(areaId, "NotExistingId");
    }

    @Test(expected = DataIntegrityViolationException.class)
    @WithMockAdmin
    public void createDuplicate() {
        User duplicate = VOTER.toBuilder().id("XXX").email("god@email.com").build();
        userService.create(duplicate);
    }
}