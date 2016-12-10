package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.to.transformers.UserTransformer;

import java.util.Collection;
import java.util.Map;
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
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserTo> create(@RequestBody UserTo userTo) {
        User newUser = UserTransformer.toEntity(userTo);
        UserTo created = UserTransformer.toDto(userService.create(newUser));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody UserTo userTo) {
        userService.update(userTo.getId(), userTo.getName(), userTo.getEmail(), userTo.getPassword());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTo> get(@PathVariable String id) {
        User user = userService.get(id);
        UserTo userTo = UserTransformer.toDto(user);
        return new ResponseEntity<>(userTo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<UserTo>> getAll() {
        Collection<User> users = userService.getAll();
        Collection<UserTo> userTos = users.stream()
                .map(UserTransformer::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userTos, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        userService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/activate")
    //TODO Implement validation for Map<String, Boolean> params content
    public ResponseEntity activate(@PathVariable String id, @RequestBody Map<String, Boolean> params) {
        userService.activate(id,  params.get("isActive"));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/rights.set")
    //TODO Implement validation for Map<String, Byte> params content
    public ResponseEntity setRights(@PathVariable String id, @RequestBody Map<String, Byte> params) {
        userService.setRoles(id, params.get("rights"));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
