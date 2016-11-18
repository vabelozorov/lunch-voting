package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.IUserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.UserUtils;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private IUserService userService;

    @PostMapping
    public ResponseEntity<UserTo> register(@RequestBody UserTo userTo, String password) {
        User newUser = UserUtils.convertIntoUser(userTo);
        newUser.setPassword(password);
        UserTo created = UserUtils.convertIntoTo(userService.create(newUser));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody UserTo userTo, String password) {
        User user = UserUtils.convertIntoUser(userTo);
        user.setPassword(password);
        userService.update(user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTo> get(@PathVariable String id) {
        User user = userService.get(id);
        UserTo userTo = UserUtils.convertIntoTo(user);
        return new ResponseEntity<>(userTo, HttpStatus.OK);
    }
}
