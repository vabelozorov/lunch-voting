package ua.belozorov.lunchvoting.model;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class AuthorizedUser {
//    private static User GOD = new User("GOD_ID", 1, "Царь всея приложение", "god@email.com", "godpass",
//            new HashSet<>(Arrays.asList(UserRole.VOTER, UserRole.ADMIN)), LocalDateTime.of(2016, 11, 16, 0, 0, 1), true, "AREA1_ID");
//    private static User VOTER = new User("VOTER_ID", 1, "Синий Гном", "voter@email.com", "voterpass",
//            new HashSet<>(Arrays.asList(UserRole.VOTER)), LocalDateTime.of(2016, 11, 17, 13, 0, 0), true, "AREA1_ID");
//    private static User ADMIN = new User("ADMIN_ID", 1, "Just an admin", "admin@email.com", "adminpass",
//            new HashSet<>(Arrays.asList(UserRole.ADMIN)), LocalDateTime.of(2016, 11, 16, 13, 0, 0), true, "AREA1_ID");

//
//    public static void setVoter() {
//        authorizedUser = VOTER;
//    }
//
//    public static void setAdmin() {
//        authorizedUser = ADMIN;
//    }
//
//    public static void setGod() {
//        authorizedUser = GOD;
//    }
//    static {
//        setGod();
//    }
//

    private static User authorizedUser;
    public static void authorize(User user) {
        authorizedUser = user;
    }

    public static User get() {
        return authorizedUser;
    }


}
