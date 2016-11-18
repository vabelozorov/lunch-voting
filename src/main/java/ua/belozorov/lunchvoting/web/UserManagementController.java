package ua.belozorov.lunchvoting.web;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.IUserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.UserUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(UserManagementController.REST_URL)
public class UserManagementController {
    static final String REST_URL = "/api/usermgmt";

    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<UserTo> create(@RequestBody UserTo userTo) {
        User newUser = UserUtils.convertIntoUser(userTo);
        UserTo created = UserUtils.convertIntoTo(userService.create(newUser));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody UserTo userTo) {
        User user = UserUtils.convertIntoUser(userTo);
        userService.update(user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTo> get(@PathVariable String id) {
        User user = userService.get(id);
        UserTo userTo = UserUtils.convertIntoTo(user);
        return new ResponseEntity<>(userTo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<UserTo>> getAll() {
        Collection<User> users = userService.getAll();
        Collection<UserTo> userTos = users.stream().map(UserUtils::convertIntoTo).collect(Collectors.toList());
        return new ResponseEntity<>(userTos, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        userService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity activate(@PathVariable String id, boolean isActive) {
        userService.activate(id, isActive);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/rights.set")
    public ResponseEntity setRights(@PathVariable String id, @RequestBody byte rights) {
        userService.setRoles(id, rights);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
