package ua.belozorov.lunchvoting.web;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.security.IsAdmin;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A controller for administering users
 *
 * Generated on 15.11.16.
 */
@RestController
@RequestMapping(UserManagementController.REST_URL)
@IsAdmin
public class UserManagementController {
    static final String REST_URL = "/api/areas/{areaId}/members";

    private final UserService userService;

    /**
     * <p>Class constructor.</p>
     *
     * @param userService   an instance of a class which implements {@link UserService} interface
     */
    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    /**
     * <p>Updates an existing {@link User}.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId}/members/{userId} 204</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{userId}</b> existing {@link User} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td><code>application/json</code></td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>name<br>email<br>password<br></code></td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * <p>If a certain {@link User} property is not changed, its old value should be included in the request.</p>
     *
     * @param userId  existing {@link User} ID in the area of an authenticated user
     * @param userTo three non-empty parameters must be present in the JSON object:
     * <ul>
     *  <li><strong>name</strong>  new name of the User, must be between 2 and 100 characters long</li>
     *  <li><strong> password</strong>  new password of the User, must be between 6 and 30 characters long</li>
     *  <li><strong>email</strong>  new email of the User. The application enforces a unique constraint on this value and
     *  <code>DuplicateDataException</code> is thrown if value happens to be not unique</li>
     * </ul>
     *
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 409 Conflict is returned if the submitted email value is not unique</li>
     *      <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
     *      of an authenticated user</li>
     *  </ul>
     */
    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable String userId, @RequestBody @Validated(UserTo.Update.class)UserTo userTo) {
        String areaId = AuthorizedUser.getAreaId();
        ExceptionUtils.executeAndUnwrapException(
                () -> {
                    userService.updateMainInfo(areaId, userId, userTo.getName(), userTo.getEmail(), userTo.getPassword());
                    return null;
                },
                DataIntegrityViolationException.class,
                    new DuplicateDataException(ErrorCode.DUPLICATE_EMAIL, new Object[]{userTo.getEmail()})
        );
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>Retrieves a {@link User} with a given ID.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/members/{userId} 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{userId}</b> existing {@link User} ID
     *        </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param userId existing user ID in the area of an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 200 Ok </li>
     *      <li>JSON object with fields <code>userId, name, email, roles, registeredDate, activated, areaId</code> </li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
     *      of an authenticated user</li>
     *  </ul>
     *  */
    @GetMapping("/{userId}")
    public ResponseEntity<UserTo> get(@PathVariable String userId) {
        User user = userService.get(AuthorizedUser.get().getAreaId(), userId);
        return new ResponseEntity<>(new UserTo(user), HttpStatus.OK);
    }

    /**
     * <p>Retrieves all {@link User} objects in the {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/members 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *             </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 200 Ok </li>
     *      <li>JSON array where each object has fields {@code id, name, email, roles, registeredDate,
     *      activated, areaId} </li>
     *  </ul>
     */
    @GetMapping
    public ResponseEntity<Collection<UserTo>> getAll() {
        String areaId = AuthorizedUser.get().getAreaId();
        Collection<User> users = userService.getAll(areaId);
        Collection<UserTo> userTos = users.stream()
                .map(UserTo::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userTos, HttpStatus.OK);
    }

    /**
     * <p>Deletes a {@link User} with a given ID in the Area of an authenticated user..</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP DELETE /api/areas/{areaId}/members/{userId} 204</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{userId}</b> existing {@link User} ID
     *             </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param userId existing user ID
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found is returned if a {@link User} with the given ID does not exist
     *      in the {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}</li>
     *  </ul>
     *  */
    @DeleteMapping("/{userId}")
    public ResponseEntity delete(@PathVariable String userId) {
        userService.delete(AuthorizedUser.get().getAreaId(), userId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>Activates/deactivates a user account.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId}/members/{userId} 204</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{userId}</b> existing {@link User} ID
     *             </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td><code>application/x-www-form-urlencoded</code></td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>activated</code></td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param userId  existing <code>User</code> ID in the Area of an authenticated User
     * @param activated  <code>true</code> to activate the account, <code>false</code> to deactivate the account
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>
     *  If the request fails:
     * <ul>
     *      <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
     *      of an authenticated user/li>
     * </ul>
     */
    @PutMapping(value = "/{userId}", params = {"activated"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity activate(@PathVariable String userId, Boolean activated) {
        String areaId = AuthorizedUser.get().getAreaId();
        userService.activate(areaId, userId, activated);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>Sets roles for a user account.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId}/members/{userId} 204</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{userId}</b> existing {@link User} ID
     *             </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td><code>application/x-www-form-urlencoded</code></td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>roles</code></td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param roles represents request parameters and must contain:
     * <ul>
     *    <li><code>userId</code>  existing user ID</li>
     *    <li><code>roles</code>  comma-separated list of values. Valid values are: ADMIN, VOTER</li>
     * </ul>
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content </li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
 *          of an authenticated user</li>
     *  </ul>
     */
    @PutMapping(value = "/{userId}", params = {"roles"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity setRoles(@PathVariable String userId, UserRole[] roles) {
        String areaId = AuthorizedUser.get().getAreaId();
        userService.setRoles(areaId, userId, Sets.newHashSet(roles));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
