package ua.belozorov.lunchvoting.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.repository.user.UserRepository;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**

 *
 * Created on 15.11.16.
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User create(User user) {
        ExceptionUtils.checkParamsNotNull(user);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(String areaId, User user) {
        ExceptionUtils.checkParamsNotNull(areaId, user);

        if (userRepository.get(areaId, user.getId()) == null) {
            throw new NotFoundException(user.getId(), User.class);
        }
        userRepository.update(user);
    }

    @Override
    @Transactional
    public void updateMainInfo(String areaId, String id, String name, String email, String password) {
        ExceptionUtils.checkParamsNotNull(areaId, id, name, email, password);

        User persistedUser = this.get(areaId, id);
        User updatedUser = persistedUser.withName(name).withEmail(email).withPassword(password);
        userRepository.update(updatedUser);
    }


    @Override
    public User get(String areaId, String userId) {
        ExceptionUtils.checkParamsNotNull(areaId, userId);

        return ofNullable(userRepository.get(areaId, userId))
                .orElseThrow(() -> new NotFoundException(userId, User.class));
    }

    @Override
    public Collection<User> getAll(String areaId) {
        ExceptionUtils.checkParamsNotNull(areaId);

        return userRepository.getAll(areaId);
    }

    @Override
    @Transactional
    public void delete(String areaId, String id) {
        if ( ! userRepository.delete(areaId, id)) {
            throw new NotFoundException(id, User.class);
        }
    }

    @Override
    @Transactional
    public void activate(String areaId, String userId, boolean isActive) {
        User user = this.get(areaId, userId);
        userRepository.update(user.withActivated(isActive));
    }

    @Override
    @Transactional
    public void setRoles(String areaId, String userId, Set<UserRole> roles) {
        User user = this.get(areaId, userId);
        userRepository.update(user.withRoles(roles));
    }

    @Override
    public User getFresh(Supplier<User> supplier) {
        this.userRepository.flushAndClear();
        return supplier.get();
    }

    @Override
    public List<User> getUsersByRole(String areaId, UserRole role) {
        return this.userRepository.getUsersByRole(areaId, role);
    }

    /*
           For Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails user = userRepository.geByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("UserRecord %s not found", email)) );
        return user;
    }
}
