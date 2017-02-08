package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.exceptions.DuplicateEmailException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2>A controller for administering users</h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(UserManagementController.REST_URL)
public final class UserManagementController {
    static final String REST_URL = "/api/usermgmt";

    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public UserManagementController(UserService userService,
                                    MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    /**
     * Creates a new User instance via a HTTP POST request. Three non-empty parameters must be present in the request:
     * {@code name, password, email}. Other parameters are ignored.
     * @param userTo represents request parameters and must contain non-empty {@code name, password, email} fields
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 201 Created </li>
     *      <li>URL to access the created object in HTTP Location Header</li>
     *      <li>ID of created object in JSON format {"id" : <ID>}</li>
     *  </ul>
     *  @throws DuplicateEmailException if a provided email already exists in the database
     */
    @PostMapping
    public ResponseEntity create(@RequestBody @Validated(UserTo.Create.class) UserTo userTo) {
        User created;
        try {
            created = userService.create(new User(null, userTo.getName(), userTo.getEmail(), userTo.getPassword()));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(messageSource.getMessage(
                    "error.duplicate_email",
                    new Object[]{userTo.getEmail()},
                    LocaleContextHolder.getLocale()
            ));
        }
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", created.getId()));
    }

    /**
     * Updates an existing User instance via a HTTP PUT request. Four non-empty parameters must be present in the request:
     * {@code id, name, password, email}. Other parameters are ignored.
     * @param userTo  represents request parameters and must contain non-empty {@code id, name, password, email} fields
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>
     */
    @PutMapping
    public ResponseEntity update(@RequestBody @Validated(UserTo.Update.class)UserTo userTo) {
        userService.update(userTo.getId(), userTo.getName(), userTo.getEmail(), userTo.getPassword());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves a User instance with ID {@code id}
     * @param id existing user ID
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 200 Ok </li>
     *      <li>User instance in JSON format with fields
     *      {@code id, name, email, roles, registeredDate, activated} </li>
     *  </ul>
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserTo> get(@PathVariable String id) {
        User user = userService.get(id);
        return new ResponseEntity<>(new UserTo(user), HttpStatus.OK);
    }

    /**
     * Retrieves all User instances with.
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 200 Ok </li>
     *      <li>User instances in JSON format with fields
     *      {@code id, name, email, roles, registeredDate, activated} </li>
     *  </ul>
     */
    @GetMapping
    public ResponseEntity<Collection<UserTo>> getAll() {
        Collection<User> users = userService.getAll();
        Collection<UserTo> userTos = users.stream()
                .map(UserTo::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userTos, HttpStatus.OK);
    }

    /**
     * Deletes a User instance with ID {@code id}
     * @param id existing user ID
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>     */
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        userService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Activates/deactivates a user account. Expects a JSON body with fields {@code id, activated}
     * @param userTo represents request parameters and must contain:
     * <ul>
     *   <li>{@code id} - existing user ID</li>
*        <li>{@code activated} - boolean true/false</li>
     * </ul>
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>
     */
    @PutMapping("/activate")
    public ResponseEntity activate(@RequestBody @Validated(UserTo.Activate.class) UserTo userTo) {
        userService.activate(userTo.getId(), userTo.isActivated());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Sets user rights for user account. Expects a JSON body with fields {@code id, roles}
     * @param userTo represents request parameters and must contain:
     * <ul>
     *   <li>{@code id} - existing user ID</li>
 *       <li>{@code roles} - JSON array of roles. Valid values are: ADMIN, VOTER</li>
     * </ul>
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>
     */
    @PutMapping("/roles")
    public ResponseEntity setRoles(@RequestBody @Validated(UserTo.Roles.class) UserTo userTo) {
        userService.setRoles(userTo.getId(), userTo.getRoles());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
