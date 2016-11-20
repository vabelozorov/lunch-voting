package ua.belozorov.lunchvoting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.repository.user.IUserRepository;

import java.util.Collection;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Service
@Transactional(readOnly = true)
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

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
    public void update(User user) {
        if ( ! userRepository.update(user)) {
            throw new NotFoundException(user.getId(), User.class);
        }
    }

    @Override
    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void activate(String id, boolean isActive) {
        if ( ! userRepository.activate(id, isActive)) {
            throw new NotFoundException(id, User.class);
        }
    }

    @Override
    @Transactional
    public void setRoles(String id, byte bitmask) {
        if ( ! userRepository.setRoles(id, bitmask)) {
            throw new NotFoundException(id, User.class);
        }
    }


}
