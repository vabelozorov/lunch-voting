package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.to.UserTo;

/**
 * <h2><A controller that manages user requests about updating his/her own profile/h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(UserProfileController.REST_URL)
public class UserProfileController {
    static final  String REST_URL = "/api/profile";
    private final UserManagementController controller;

    @Autowired
    public UserProfileController(UserManagementController controller) {
        this.controller = controller;
    }

    @PostMapping
    public ResponseEntity register(@RequestBody @Validated(UserTo.Create.class) UserTo userTo) {
        return this.controller.create(userTo);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Validated(UserTo.Update.class)UserTo userTo) {
        return this.controller.update(userTo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTo> get(@PathVariable String id) {
        return this.controller.get(id);
    }
}
