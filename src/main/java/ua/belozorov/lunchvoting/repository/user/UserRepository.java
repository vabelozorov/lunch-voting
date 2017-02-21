package ua.belozorov.lunchvoting.repository.user;

import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by vabelozorov on 14.11.16.
 */

public interface UserRepository {

    User save(User user);

    void update(User user);

    User get(@Nullable String areaId, String userId);

    Collection<User> getAll(@Nullable String areaId);

    boolean delete(@Nullable  String areaId, String userId);

    void flushAndClear();

    List<User> getUsersByRole(String areaId, UserRole role);

    Optional<User> geByEmail(String email);
}
