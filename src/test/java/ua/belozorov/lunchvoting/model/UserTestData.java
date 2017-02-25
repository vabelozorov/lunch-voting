package ua.belozorov.lunchvoting.model;

import lombok.Getter;
import org.mockito.ArgumentMatcher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.EqualsComparator;
import ua.belozorov.lunchvoting.util.hibernate.RolesToIntegerConverter;
import ua.belozorov.objtosql.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.argThat;

/**
 * <h2></h2>
 *
 * Created on 17.11.16.
 */
@Getter
public class UserTestData {
    public static final UserComparator USER_COMPARATOR = new UserComparator();

    public static User GOD = new User("GOD_ID", 1, "Царь всея приложение", "god@email.com", "godpass",
            new HashSet<>(Arrays.asList(UserRole.VOTER, UserRole.ADMIN)), LocalDateTime.of(2016, 11, 16, 0, 0, 1), true, "AREA1_ID");
    public static User VOTER = new User("VOTER_ID", 1, "Синий Гном", "voter@email.com", "voterpass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA1_ID");
    public static User ADMIN = new User("ADMIN_ID", 1, "Just an admin", "admin@email.com", "adminpass",
            new HashSet<>(Arrays.asList(UserRole.ADMIN)), LocalDateTime.of(2016, 11, 16, 13, 0, 0), true, "AREA1_ID");
    public static User VOTER1 = new User("VOTER1_ID", 1, "Voter1_Name", "voter1@email.com", "voter1pass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA1_ID");
    public static User VOTER2 = new User("VOTER2_ID", 1, "Voter2_Name", "voter2@email.com", "voter2pass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA1_ID");
    public static User VOTER3 = new User("VOTER3_ID", 1, "Voter3_Name", "voter3@email.com", "voter3pass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA1_ID");
    public static User VOTER4 = new User("VOTER4_ID", 1, "Voter4_Name", "voter4@email.com", "voter4pass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA1_ID");

    public static User A2_ADMIN = new User("A2_ADMIN_ID", 1, "A2admin_Name", "a2admin@email.com", "a2_adminpass",
            new HashSet<>(Arrays.asList(UserRole.ADMIN)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA2_ID"  );
    public static User A2_USER1 = new User("A2_USER1_ID", 1, "A2_Name1", "a2_user1@email.com", "a2_user1pass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA2_ID");
    public static User A2_USER2 = new User("A2_USER2_ID", 1, "A2_Name2", "a2_user2@email.com", "a2_user2pass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA2_ID");
    public static User A2_USER3 = new User("A2_USER3_ID", 1, "A2_Name3", "a2_user3@email.com", "a2_user3pass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA2_ID");

    public static User ALIEN_USER1 = new User("ALIEN1_ID", 1, "Alien1_Name", "alien1@email.com", "a1lienpass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, null  );
    public static User ALIEN_USER2 = new User("ALIEN2_ID", 1, "Alien2_Name", "alien2@email.com", "a2lienpass",
            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, null  );

    public static String GOD_ID = GOD.getId();
    public static String ADMIN_ID = ADMIN.getId();
    public static String VOTER_ID = VOTER.getId();
    public static String VOTER1_ID = VOTER1.getId();
    public static String VOTER2_ID = VOTER2.getId();
    public static String VOTER3_ID = VOTER3.getId();
    public static String VOTER4_ID = VOTER4.getId();

    public static List<User> A1_USERS = Stream.of(ADMIN, VOTER, VOTER1, VOTER2, VOTER3, VOTER4, GOD)
            .sorted().collect(Collectors.toList());

    public static List<User> A2_USERS = Stream.of(A2_ADMIN, A2_USER1, A2_USER2, A2_USER3)
            .sorted().collect(Collectors.toList());

    public static List<User> ALIEN_USERS = Stream.of(ALIEN_USER1, ALIEN_USER2)
            .sorted().collect(Collectors.toList());

    public static List<User> A1_VOTERS = Arrays.asList(VOTER, VOTER1, VOTER2, VOTER3, VOTER4);

    private final Resource userSqlResource;

    public UserTestData() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(A1_USERS);
        allUsers.addAll(A2_USERS);
        allUsers.addAll(ALIEN_USERS);

        String sql = new SimpleObjectToSqlConverter<>(
                "users",
                Arrays.asList(
                        new StringSqlColumn<>("id", User::getId),
                        new StringSqlColumn<>("area_id", User::getAreaId),
                        new StringSqlColumn<>("name", User::getName),
                        new StringSqlColumn<>("email", User::getEmail),
                        new StringSqlColumn<>("password", User::getPassword),
                        new IntegerSqlColumn<>("roles", u -> new RolesToIntegerConverter().convertToDatabaseColumn(u.getRoles())),
                        new DateTimeSqlColumn<>("registered_date", User::getRegisteredDate),
                        new BooleanSqlColumn<>("activated", User::isActivated)
                )
        ).convert(allUsers);
        this.userSqlResource = new ByteArrayResource(sql.getBytes(), "Users");
    }

    public static class UserComparator implements EqualsComparator<User>, ArgumentMatcher<User>{
        private final boolean matchId;
        private final boolean matchDate;
        private final User expected;

        public UserComparator(User user, boolean matchId, boolean matchDate) {
            this.matchId = matchId;
            this.expected = user;
            this.matchDate = matchDate;
        }

        public UserComparator() {
            this.matchId = true;
            expected = null;
            matchDate = true;
        }

        public UserComparator(User user) {
            this.matchId = true;
            expected = user;
            matchDate = true;
        }

        public UserComparator noMatchId() {
            return new UserComparator(this.expected, false, this.matchDate);
        }

        public UserComparator noDate() {
            return new UserComparator(this.expected, this.matchId, false);
        }

        @Override
        public boolean compare(User obj, User another) {
            return  matchId ? obj.getId().equals(another.getId()) : true
                    && obj.getName().equals(another.getName())
                    && obj.getEmail().equals(another.getEmail())
                    && obj.getPassword().equals(another.getPassword())
                    && matchDate ? obj.getRegisteredDate().isEqual(another.getRegisteredDate()) : true
                    && obj.getRoles().equals(another.getRoles())
                    && Objects.equals(obj.getAreaId(), another.getAreaId())
                    && obj.isActivated() == another.isActivated();
        }

        @Override
        public boolean matches(User argument) {
            return this.expected == argument || (this.expected != null && argument != null && compare(this.expected, argument));
        }
    }

    public static User userNoIdNoDate(User user) {
        return argThat(new UserComparator(user, false, false));
    }
}
