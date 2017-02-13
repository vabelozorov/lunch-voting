package ua.belozorov.lunchvoting.service.user;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
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
 * @author vabelozorov on 17.11.16.
 */
public class UserServiceTest extends AbstractServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void get() throws Exception {
        reset();
        User actual = userService.get(VOTER_ID);
        assertSelectCount(1);
        assertThat(actual, matchSingle(VOTER, USER_COMPARATOR));
    }

    @Test
    public void getAll() throws Exception {
        reset();
        Collection<User> users = userService.getAll();
        assertSelectCount(1);
        assertThat(
                ALL_USERS,
                contains(matchCollection(users, USER_COMPARATOR))
        );
    }

    @Test
    public void delete() throws Exception {
        reset();
        userService.delete(ADMIN_ID);
        assertUpdateCount(2); // Update to null in Vote and LunchPlace entities
        assertDeleteCount(1);

        Collection<User> users = userService.getAll();
        assertThat(
            ALL_USERS.stream()
                    .filter(u -> !u.getId().equals(ADMIN_ID))
                    .collect(Collectors.toList()),
            contains(matchCollection(users, USER_COMPARATOR))
        );
    }

    @Test
    public void update() throws Exception {
        User updated = userService.get(VOTER_ID);
        updated = updated.toBuilder().password("newPassword").email("updated@email.com").build();

        reset();
        userService.update(updated.getId(), updated.getName(), updated.getEmail(), updated.getPassword());
        assertSelectCount(1);
        assertUpdateCount(1);

        assertThat(userService.get(VOTER_ID), matchSingle(updated, USER_COMPARATOR));
    }

    @Test
    public void create() throws Exception {
        User expected = new User("NEW_USER_ID", "New User", "new@email.com", "strongPassword");

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
    public void activate() throws Exception {
        reset();
        userService.activate(VOTER_ID, false);
        assertSelectCount(1);
        assertUpdateCount(1);

        assertFalse(userService.get(VOTER_ID).isActivated());
    }

    @Test
    public void setRoles() throws Exception {
        Set<UserRole> expectedRoles = new HashSet<>();
        expectedRoles.add(UserRole.VOTER);
        expectedRoles.add(UserRole.ADMIN);

        reset();
        userService.setRoles(VOTER_ID, expectedRoles);
        assertSelectCount(1);
        assertUpdateCount(1);

        assertEquals(userService.get(VOTER_ID).getRoles(), expectedRoles);
    }


    @Test(expected = NotFoundException.class)
    public void getNotExisting() {
        userService.get("NotExistingId");
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExisting() {
        User updated = VOTER.toBuilder().id("NOT_EXISTING_ID").password("newPassword").email("updated@email.com").build();
        userService.update(updated.getId(), updated.getName(), updated.getEmail(), updated.getPassword());
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExisting() {
        userService.delete("NotExistingId");
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createDuplicate() {
        User duplicate = VOTER.toBuilder().id("XXX").email("god@email.com").build();
        userService.create(duplicate);
    }
}