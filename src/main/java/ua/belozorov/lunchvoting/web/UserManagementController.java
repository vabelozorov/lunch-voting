package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.to.transformers.UserTransformer;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(UserManagementController.REST_URL)
public final class UserManagementController {
    static final String REST_URL = "/api/usermgmt";

    private final UserService userService;
    private final MessageSource messageSource;
    private final JsonFilter jsonFilter;

    @Autowired
    public UserManagementController(UserService userService,
                                    MessageSource messageSource,
                                    @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.jsonFilter = jsonFilter;
    }

    /**
     * Creates a new User instance via a HTTP POST request. Three non-empty parameters must be present in the request:
     * {@code name, password, email}. Other parameters are ignored.
     * @param userTo represents request parameters and must contain non-empty {@code name, password, email} fields
     * @return ResponseEntity instance with the following values:
     *  <ul>
     *      <li>HTTP Status 201 Created </li>
     *      <li>URL to access the created object in HTTP Location Header</li>
     *      <li>ID of created object in JSON format {"id" : <ID>}</li>
     *  </ul>
     *  @throws
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
     * @return ResponseEntity instance with the following values:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>
     */
    @PutMapping
    public ResponseEntity update(@RequestBody @Validated(UserTo.Update.class)UserTo userTo) {
        userTo.validateForObjectUpdate();
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

    private void filterUserFields(Object object) {
        Map<Class<?>, Set<String>> map = new HashMap<>();
        map.put(
                User.class,
                Stream.of("version", "password").collect(Collectors.toSet())
        );
        jsonFilter.excludingFilter(object, map);
    }
}
