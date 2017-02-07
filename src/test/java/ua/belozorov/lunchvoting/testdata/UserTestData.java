package ua.belozorov.lunchvoting.testdata;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.EqualsComparator;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.objtosql.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
@Getter
public class UserTestData {
    public static final EqualsComparator<User> USER_COMPARATOR = new UserComparator();

    public static User GOD = GOD = new User("GOD_ID", "Царь всея приложение", "god@email.com", "godpass", (byte)3,
            LocalDateTime.of(2016, 11, 16, 0, 0, 1), true);
    public static User ADMIN = new User("ADMIN_ID", "Just an admin", "admin@email.com", "adminpass", (byte) 2,
            LocalDateTime.of(2016, 11, 16, 13, 0, 0), true);
    public static User VOTER = new User("VOTER_ID", "Синий Гном", "voter@email.com", "voterpass", (byte) 1,
            LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
    public static User VOTER1 = new User("VOTER1_ID", "Voter1_Name", "voter1@email.com", "voter1pass", (byte) 1,
            LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
    public static User VOTER2 = new User("VOTER2_ID", "Voter2_Name", "voter2@email.com", "voter2pass", (byte) 1,
            LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
    public static User VOTER3 = new User("VOTER3_ID", "Voter3_Name", "voter3@email.com", "voter3pass", (byte) 1,
            LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
    public static User VOTER4 = new User("VOTER4_ID", "Voter4_Name", "voter4@email.com", "voter4pass", (byte) 1,
            LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);

    public static String GOD_ID = GOD.getId();
    public static String ADMIN_ID = ADMIN.getId();
    public static String VOTER_ID = VOTER.getId();
    public static String VOTER1_ID = VOTER1.getId();
    public static String VOTER2_ID = VOTER2.getId();
    public static String VOTER3_ID = VOTER3.getId();
    public static String VOTER4_ID = VOTER4.getId();

    public static List<User> ALL_USERS = Stream.of(ADMIN, VOTER, VOTER1, VOTER2, VOTER3, VOTER4, GOD)
            .sorted(Comparator.comparing(User::getEmail)).collect(Collectors.toList());

    public static List<User> VOTERS = Arrays.asList(VOTER, VOTER1, VOTER2, VOTER3, VOTER4);

    private final Resource userSqlResource;

    public UserTestData() {
        String sql = new SimpleObjectToSqlConverter<>(
                "users",
                Arrays.asList(
                        new StringSqlColumn<>("id", User::getId),
                        new StringSqlColumn<>("name", User::getName),
                        new StringSqlColumn<>("email", User::getEmail),
                        new StringSqlColumn<>("password", User::getPassword),
                        new IntegerSqlColumn<>("roles", u -> (int)u.getRoles()),
                        new DateTimeSqlColumn<>("registered_date", User::getRegisteredDate),
                        new BooleanSqlColumn<>("activated", User::isActivated)
                )
        ).convert(ALL_USERS);
        this.userSqlResource = new ByteArrayResource(sql.getBytes(), "Users");
    }

    private static class UserComparator implements EqualsComparator<User>{
        @Override
        public boolean compare(User o1, User o2) {
            return  o1.getId().equals(o2.getId()) &&
                    o1.getName().equals(o2.getName()) &&
                    o1.getEmail().equals(o2.getEmail()) &&
                    o1.getPassword().equals(o2.getPassword()) &&
                    o1.getRegisteredDate().isEqual(o2.getRegisteredDate()) &&
                    o1.getRoles() == o2.getRoles() &&
                    o1.isActivated() == o2.isActivated();
        }
    }
}
