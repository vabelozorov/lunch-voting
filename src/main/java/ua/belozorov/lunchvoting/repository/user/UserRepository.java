package ua.belozorov.lunchvoting.repository.user;

import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.User;

import java.util.Collection;

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
}
