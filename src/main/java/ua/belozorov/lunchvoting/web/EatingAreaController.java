package ua.belozorov.lunchvoting.web;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.service.area.EatingAreaService;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import javax.validation.constraints.Size;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * A controller to manage {@link EatingArea} objects and other objects that logically belong to an {@link EatingArea}.
 *
 * Generated on 12.02.17.
 */

@RestController
@RequestMapping(EatingAreaController.REST_URL)
@Validated
public class EatingAreaController {

    static final String REST_URL = "/api/areas";

    private final EatingAreaService areaService;

    private final JsonFilter jsonFilter;

    @Autowired
    public EatingAreaController(EatingAreaService areaService,
                                @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.areaService = areaService;
        this.jsonFilter = jsonFilter;
    }

    /**
     * <p>Creates an {@link EatingArea}, adds the creator to the lists of area members and
     * sets his rights to <strong>ADMIN</strong></p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP POST /api/areas/ 201}</font></td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/x-www-form-urlencoded}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td>{@code name}</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param name a unique name for the area, must 2-50 characters long. The application enforces a unique constraint on this value and
     *  {@code DuplicateDataException} is thrown if value happens to be not unique
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>An HTTP Status 201 Created </li>
     *      <li>A URL to access the created object in HTTP Location Header</li>
     *      <li>A JSON object with a field {@code id} containing the ID of the newly created {@code EatingArea}</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 409 Conflict is returned if the submitted name is not unique</li>
     *  </ul>
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @IsAdminOrVoter
    public ResponseEntity create(@RequestParam @Size(min = 2, max = 50) String name) {
        EatingArea area = ExceptionUtils.executeAndUnwrapException(
                () -> areaService.create(name, AuthorizedUser.get()),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_AREA_NAME, new Object[]{name})
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("{base}/{id}").buildAndExpand(REST_URL, area.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", area.getId()));
    }


    /**
     * <p>Registers a new {@link User} in the area of a currently authenticated user with <strong>ADMIN</strong> role.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP POST /api/areas/{areaId}/members/ 201}</font><br>
     *              <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *        </td>
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
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * <p>The {@link User}  will be created in the same {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}
     * as the {@link User} whose credentials were submitted.<br>
     * Content of the request must a JSON object and {@code Content-Type} must be set to {@code application/json}
     *
     * </p>
     * Three non-empty parameters must be present in the JSON object:
     * <ul>
     *  <li><strong>name</strong>  name of the User, must be between 2 and 100 characters long</li>
     *  <li><strong> password</strong>  password of the User, must be between 6 and 30 characters long</li>
     *  <li><strong>email</strong>  email of the User. The application enforces a unique constraint on this value and
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
     *      <li>HTTP Status 409 Conflict is returned if the submitted email is not unique</li>
     *  </ul>
     */
    @PostMapping(value = "/{id}/members", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdmin
    public ResponseEntity createUserInArea(@RequestBody @Validated(UserTo.Create.class) UserTo userTo) {
        String areaId = AuthorizedUser.get().getAreaId();
        User newUser = new User(userTo.getName(), userTo.getEmail(), userTo.getPassword());
        User created = ExceptionUtils.executeAndUnwrapException(
                () -> areaService.createUserInArea(areaId, newUser),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_EMAIL, new Object[]{userTo.getEmail()})
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(UserManagementController.REST_URL + "/{id}").buildAndExpand(created.getId(), created.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", created.getId()));
    }

    /**
     * <p>Creates a new {@link LunchPlace} object in the area of an authenticated {@code User}.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP POST /api/areas/{areaId}/places 201}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/json}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td>{@code name}</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>{@code address, description, phones}</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * <p>The {@link LunchPlace} will be created in the same {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}
     * as the {@link User} whose credentials were submitted.<br>
     * </p>
     * The following parameters must be present in the JSON object:
     * <ul>
     *  <li><strong>name</strong>  name of a new {@link LunchPlace}, should not exist and must be between 2 and 50 characters long.
     *  The application enforces a unique contraint on this value and
     *  {@code DuplicateDataException} is thrown if value happens to be not unique</li>
     *  <li><strong>address</strong>  address of the {@link LunchPlace}, must not exceed 200 characters, optional field</li>
     *  <li><strong>description</strong>  description of the {@link LunchPlace}, must not exceed 1000 characters, optional field</li>
     *  <li><strong>phones</strong> phones ofthe {@link LunchPlace}, comma-separated list of strings, each string is 10-digit value</li>
     * </ul>
     * Other parameters are ignored.
     *
     * @param dto represents request parameters
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 201 Created</li>
     *      <li>A URL to access the created object in HTTP Location Header</li>
     *      <li>A JSON object with a field {@code id} containing the ID of the newly created {@code LunchPlace}</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 409 Conflict is returned if the submitted name value is not unique</li>
     *  </ul>
     */
    @PostMapping(value = "/{areaId}/places", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdmin
    public ResponseEntity createPlaceInArea(@RequestBody @Validated(LunchPlaceTo.Create.class) LunchPlaceTo dto) {
        String areaId = AuthorizedUser.get().getAreaId();

        LunchPlace created = ExceptionUtils.executeAndUnwrapException(
                () -> areaService.createPlaceInArea(areaId, null, dto.getName(), dto.getAddress(),
                        dto.getDescription(), dto.getPhones()),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_PLACE_NAME, new Object[]{dto.getName()})
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("{base}/{id}").buildAndExpand(LunchPlaceController.REST_URL, created.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", created.getId()));
    }

    /**
     * <p>Creates a new {@link LunchPlacePoll} object in the area of an authenticated {@code User}.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP POST /api/areas/{areaId}/polls 201}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/x-www-form-urlencoded}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>{@code menuDate<br>start<br>end<br>change}</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     * <p>Items of the poll are LunchPlace objects with menus for given date or a current day, if not given.</p>
     * <p>
     *     The poll starts at <code>start</code> time or will be 09-00 of a current day.<br>
     *     The poll ends at <code>end</code> time or will be 12-00 of a current day.<br>
     *     The poll final time to change a vote is <code>change</code> time or will be 11-00 of a current day.
     * </p>
     * <p>The {@link LunchPlacePoll} will be created in the same {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}
     * as the {@link User} whose credentials were submitted.<br>
     * </p>
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 201 Created</li>
     *      <li>A URL to access the created object in HTTP Location Header</li>
     *      <li>A JSON object with a field {@code id} containing the ID of the newly created {@code LunchPlacePoll}</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 422 Unprocessable_Entity is returned
     *      if the area does not contain LunchPlace objects with Menu for today</li>
     *  </ul>
     * @param menuDate date of Menu which determines which LunchPlace objects will be added as items to the LunchPlacePoll
     *                 optional. Default value is 09-00 of a current day
     * @param start time when the poll starts, optional. Default value is 12-00 of a current day
     * @param end time when the poll ends, optional. Default value is 11-00 of a current day
     * @param change time before which user can change a vote, optional. Default value is 11-00 of a current day.
     *               The following constraint applies: start <= change <= end
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 201 Created</li>
     *      <li>A URL to access the created object in HTTP Location Header</li>
     *      <li>A JSON object with a field {@code id} containing the ID of the newly created {@code LunchPlacePoll}</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 422 Unprocessable_entity is returned if no {@code LunchPlace} objects
     *      with {@code menudate}</li>
     *  </ul>
     */
    @PostMapping(value = "/{areaId}/polls", params = "menuDate",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdmin
    public ResponseEntity createPoll(@RequestParam(required = false) LocalDate menuDate,
                                     @RequestParam(required = false) LocalDateTime start,
                                     @RequestParam(required = false) LocalDateTime end,
                                     @RequestParam(required = false) LocalDateTime change) {
        String areaId = AuthorizedUser.get().getAreaId();
        LunchPlacePoll poll = areaService.createPollInArea(areaId, menuDate, start, end, change);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PollController.REST_URL + "/{id}").buildAndExpand(areaId, poll.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * <p>Updates {@link EatingArea} name.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP PUT /api/areas/{areaId} 204}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/x-www-form-urlencoded}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td>{@code name}</td>
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
     * @param name a unique name for the area, must 2-50 characters long. The application enforces a unique constraint on this value and
     *  {@code DuplicateDataException} is thrown if value happens to be not unique
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>An HTTP Status 204 Created </li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 409 Conflict is returned if the submitted name is not unique</li>
     *  </ul>
     */
    @PutMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @IsAdmin
    public ResponseEntity update(@RequestParam @Size(min = 2, max = 50) String name) {
        ExceptionUtils.executeAndUnwrapException(
                () -> {areaService.updateAreaName(name, AuthorizedUser.get()); return null; },
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_AREA_NAME, new Object[]{name})
        );
        return ResponseEntity.noContent().build();
    }

    /**
     * <p>Returns a requested EatingArea object.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/areas/{areaId} 200}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     *
     * @param id id of existing {@code EatingArea}
     * @param summary true causes the summary information to be included into a response instead of IDs
     *                of {@link User}, {@link LunchPlacePoll} and {@link LunchPlace} objects. {@code true} means that IDs
     *                are included, but summary info is not.
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>An HTTP Status 200 Ok</li>
     *      <li>JSON object with the following fields:
     *          <ul>
     *              <li>if {@code summary=true}, {@code id, name, created, userCount, placeCount, pollCount} are included</li>
     *              <li>if {@code summary=false}, {@code id, name, created, users, places, polls} are included</li>
     *          </ul>
     *      </li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *  </ul>
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<AreaTo> get(@PathVariable String id, @RequestParam(defaultValue = "true") boolean summary) {
        AreaTo to = areaService.getAsTo(id, summary);
        return ResponseEntity.ok(to);
    }

    /**
     * <p>Returns an EatingArea objects that start with a given parameter.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/areas 200}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td>{@code name}</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param name start of {@code EatingArea} name, must be 2-50 characters long
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>An HTTP Status 200 Ok</li>
     *      <li>JSON object with the following fields: {@code id, name, created} are included</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *  </ul>
     */
    @GetMapping(params = "name")
    @IsAdminOrVoter
    public ResponseEntity<List<EatingArea>> filterByName(@RequestParam @Size(min = 2, max = 50) String name) {
        List<EatingArea> areas = areaService.filterByNameStarts(name);
        this.filterArea(areas);
        return ResponseEntity.ok(areas);
    }

    /**
     * <p>Deletes an EatingArea objects and all LunchPlace, LunchPlace poll objects that area associated with it.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP DELETE /api/areas 204}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><<strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * <p>Users of the area does not belong to any area after this request is complete</p>
     *
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>An HTTP Status 204 No_Content</li>
     *  </ul>
     */
    @DeleteMapping
    @IsAdmin
    public ResponseEntity delete() {
        String areaId = AuthorizedUser.getAreaId();
        areaService.delete(areaId);
        return ResponseEntity.noContent().build();
    }

    private void filterArea(Object obj) {
        Map<Class<?>, Set<String>> filterMap = new HashMap<>();
        filterMap.put(
                EatingArea.class, Stream.of("users", "places", "polls", "version").collect(Collectors.toSet())
        );
        jsonFilter.excludingFilter(obj, filterMap);
    }
}