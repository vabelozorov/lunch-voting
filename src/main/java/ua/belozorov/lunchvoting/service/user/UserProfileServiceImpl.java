package ua.belozorov.lunchvoting.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.repository.user.UserRepository;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import static java.util.Optional.ofNullable;

/**

 *
 * Created on 14.02.17.
 */
@Service
public final class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    @Autowired
    public UserProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User register(User user) {
        ExceptionUtils.checkParamsNotNull(user);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateMainInfo(String id, String name, String email, String password) {
        ExceptionUtils.checkParamsNotNull(id, name, email, password);

        User persistedUser = userRepository.get(null, id);
        if (persistedUser == null) {
            throw new NotFoundException(id, User.class);
        }
        User updatedUser = persistedUser.toBuilder().name(name).email(email).password(password).build();
        userRepository.update(updatedUser);
    }

    @Override
    public void update(User user) {
        ExceptionUtils.checkParamsNotNull(user);

        if (userRepository.get(null, user.getId()) == null) {
            throw new NotFoundException(user.getId(), User.class);
        }
        userRepository.update(user);
    }

    @Override
    public User get(String id) {
        return ofNullable(userRepository.get(null, id))
                .orElseThrow(() -> new NotFoundException(id, User.class));
    }

    @Override
    public UserRepository getRepository() {
        return this.userRepository;
    }
}
