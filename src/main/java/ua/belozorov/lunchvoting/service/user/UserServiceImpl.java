package ua.belozorov.lunchvoting.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.repository.user.UserRepository;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.util.Collection;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User get(String id) {
        return ofNullable(userRepository.get(id))
                .orElseThrow(() -> new NotFoundException(id, User.class));
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    @Transactional
    public void delete(String id) {
        if ( ! userRepository.delete(id)) {
            throw new NotFoundException(id, User.class);
        }
    }

    @Override
    @Transactional
    public void update(String id, String name, String email, String password) {
        ExceptionUtils.checkParamsNotNull(id, name, email, password);

        User persistedUser = userRepository.get(id);
        if (persistedUser == null) {
            throw new NotFoundException(id, User.class);
        }
        User updatedUser = persistedUser.toBuilder().name(name).email(email).password(password).build();
        userRepository.update(updatedUser);
    }

    @Override
    @Transactional
    public User create(User user) {
        ExceptionUtils.checkParamsNotNull(user);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void activate(String id, boolean isActive) {
        User user = ofNullable(userRepository.get(id))
                .orElseThrow(() -> new NotFoundException(id, User.class));
        userRepository.update(user.setActivated(isActive));
    }

    @Override
    @Transactional
    public void setRoles(String id, Set<UserRole> roles) {
        User user = ofNullable(userRepository.get(id))
                .orElseThrow(() -> new NotFoundException(id, User.class));
        userRepository.update(user.setRoles(roles));
    }
}
