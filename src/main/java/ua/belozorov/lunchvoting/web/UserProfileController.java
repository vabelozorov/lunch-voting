package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.IUserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.to.transformers.UserTransformer;

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
        User newUser = UserTransformer.toEntity(userTo);
//        newUser.setPassword(password);
        UserTo created = UserTransformer.toDto(userService.create(newUser));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody UserTo userTo, String password) {
        User user = UserTransformer.toEntity(userTo);
//        user.setPassword(password);
        userService.update(user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTo> get(@PathVariable String id) {
        User user = userService.get(id);
        UserTo userTo = UserTransformer.toDto(user);
        return new ResponseEntity<>(userTo, HttpStatus.OK);
    }
}
