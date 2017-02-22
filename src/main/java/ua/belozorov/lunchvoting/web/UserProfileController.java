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
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.security.InSecure;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.net.URI;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2><A controller that manages user requests about updating his/her own profile/h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(UserProfileController.REST_URL)
public class UserProfileController {
    static final  String REST_URL = "/api/profile";
    private final UserProfileService profileService;

    @Autowired
    public UserProfileController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Creates a new User object via a HTTP POST request. Three non-empty parameters must be present in the request:
     * {@code name, password, email}. Other parameters are ignored.
     * @param userTo represents request parameters and must contain non-empty {@code name, password, email} fields
     * @return ResponseEntity instance with the following values upon successful completion of the request:
     *  <ul>
     *      <li>HTTP Status 201 Created </li>
     *      <li>URL to access the created object in HTTP Location Header</li>
     *      <li>ID of created object in JSON format {"id" : <ID>}</li>
     *  </ul>
     *  @throws DuplicateDataException if a provided email already exists in the database
     */
    @PostMapping
    @InSecure
    public ResponseEntity register(@RequestBody @Validated(UserTo.Create.class) UserTo userTo) {
        User created = ExceptionUtils.executeAndUnwrapException(
                () -> profileService.register(new User(userTo.getName(), userTo.getEmail(), userTo.getPassword())),
                DataIntegrityViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_EMAIL, new Object[]{userTo.getEmail()})
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", created.getId()));
    }

    @PutMapping
    @IsAdminOrVoter
    public ResponseEntity update(@RequestBody @Validated(UserTo.Update.class)UserTo userTo) {
        ExceptionUtils.executeAndUnwrapException(
                () -> {
                    profileService.updateMainInfo(userTo.getId(), userTo.getName(), userTo.getEmail(), userTo.getPassword());
                    return  null;
                },
                DataIntegrityViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_EMAIL, new Object[]{userTo.getEmail()})
        );
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @IsAdminOrVoter
    public ResponseEntity<UserTo> get(@PathVariable String id) {
        User user = profileService.get(id);
        return new ResponseEntity<>(new UserTo(user), HttpStatus.OK);
    }
}
