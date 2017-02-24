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
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.web.security.InSecure;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.net.URI;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * A controller that manages user requests about updating user's own profile.
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
     * <p>Registers a new {@link User}.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP POST /api/profile/ 201}</font><br></td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/json}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td><code>name<br>email<br>password<br></code></td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td>does not require</td>
     *     </tr>
     * </table>
     *
     * <p>The {@link User}  must belong to the same {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}
     * as the {@link User} whose credentials were submitted.<br>
     * Content of the request must a JSON object and {@code Content-Type} must be set to {@code application/json}
     *
     * </p>
     * Three non-empty parameters must be present in the JSON object:
     * <ul>
     *  <li><strong>name</strong>  new name of the User, must be between 2 and 100 characters long</li>
     *  <li><strong> password</strong>  new password of the User, must be between 6 and 30 characters long</li>
     *  <li><strong>email</strong>  new email of the User. The application enforces a unique contraint on this value and
     *  {@code DuplicateDataException} is thrown if value happens to be not unique</li>
     * </ul>
     * Other parameters are ignored.
     *
     * @param userTo represents request parameters and must contain non-empty {@code name, password, email} fields
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>An HTTP Status 201 Created </li>
     *      <li>A URL to access the created object in HTTP Location Header</li>
     *      <li>A JSON object with a field {@code id} containing the ID of the newly created {@code User}</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 409 Conflict is returned if the submitted email value is not unique</li>
     *  </ul>
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

    /**
     * <p>Updates an existing {@link User}.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP PUT /api/profile/{userId} 204}</font><br>
     *             <b>{userId}</b> existing {@link User} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/json}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td><code>name<br>email<br>password<br></code></td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td>any authenticated user</td>
     *     </tr>
     * </table>
     *
     * <p>Content of the request must a JSON object and {@code Content-Type} must be set to {@code application/json}</p>
     * Three non-empty parameters must be present in the JSON object:
     *
     * <ul>
     *  <li><strong>name</strong>  new name of the User, must be between 2 and 100 characters long</li>
     *  <li><strong> password</strong>  new password of the User, must be between 6 and 30 characters long</li>
     *  <li><strong>email</strong>  new email of the User. The application enforces a unique contraint on this value and
     *  {@code DuplicateDataException} is thrown if value happens to be not unique</li>
     * </ul>
     * Other parameters are ignored.
     *
     * If a certain {@link User}  property is not changed, its old value should be included in the request.
     * @param userId  existing {@link User} ID
     * @param userTo  represents request parameters and must contain non-empty {@code name, password, email} fields
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 409 Conflict is returned if the submitted email value is not unique</li>
     *      <li>HTTP Status 404 Not_Found is returned if a {@link User}  with the given ID does not exist</li>
     *  </ul>
     */
    @PutMapping("/{userId}")
    @IsAdminOrVoter
    public ResponseEntity update(@PathVariable String userId,
                                 @RequestBody @Validated(UserTo.Update.class)UserTo userTo) {
        ExceptionUtils.executeAndUnwrapException(
                () -> {
                    profileService.updateMainInfo(userId, userTo.getName(), userTo.getEmail(), userTo.getPassword());
                    return  null;
                },
                DataIntegrityViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_EMAIL, new Object[]{userTo.getEmail()})
        );
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>Retrieves an authenticated {@link User}</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/profile 200}</font><br>
     *             </td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td>any authenticated user</td>
     *     </tr>
     * </table>
     *
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 200 Ok </li>
     *      <li>JSON object with fields {@code userId, name, email, roles, registeredDate, activated} </li>
     *  </ul>
     *  */
    @GetMapping
    @IsAdminOrVoter
    public ResponseEntity<UserTo> get() {
        return new ResponseEntity<>(new UserTo(AuthorizedUser.get()), HttpStatus.OK);
    }
}
