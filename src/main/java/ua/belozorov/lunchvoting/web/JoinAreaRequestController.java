package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.service.area.JoinAreaRequestService;
import ua.belozorov.lunchvoting.to.JoinRequestTo;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * A controller which manages user requests to join a particular area.
 *
 * Created on 12.02.17.
 */
@RestController
@RequestMapping(JoinAreaRequestController.REST_URL)
public class JoinAreaRequestController {
    static final String REST_URL = "/api/{areaId}/requests";

    private final JoinAreaRequestService requestService;

    private final JsonFilter jsonFilter;

    /**
     * A constructor
     * @param requestService any instance that implements {@link JoinAreaRequestService} interface
     * @param jsonFilter any instance that implements {@link JsonFilter} interface
     */
    @Autowired
    public JoinAreaRequestController(JoinAreaRequestService requestService,
                                     @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.requestService = requestService;
        this.jsonFilter = jsonFilter;
    }

    /**
     * <p>Creates a new {@link JoinAreaRequest} to join a given area.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP POST /api/{areaId}/requests 201}</font><br>
     *             <b>{areaId}</b> existing {@link EatingArea} ID<br>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
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
     * <p>Any user can submit a request to join an area no matter whether he/she currently has a member of a particular
     * area. Certain restrictions applies to only approving the requests (see {@link JoinAreaRequestController#approve(String)}).
     * </p>
     * @param areaId  ID of the area a current user wants to join
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>An HTTP Status 201 Created</li>
     *      <li>JSON object with a field {@code id} which contains the ID of the newly created {@link JoinAreaRequest}</li>
     *  </ul>
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity makeRequest(@PathVariable("areaId") String areaId) {
        JoinAreaRequest request = requestService.make(AuthorizedUser.get(), areaId);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("{base}/{id}").buildAndExpand(REST_URL, request.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", request.getId()));
    }

    /**
     * <p>Retrieves a {@link JoinAreaRequest} by its ID, previously made by a currently authenticated user.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/{areaId}/requests/{requestId} 200}</font><br>
     *             <b>{areaId}</b> existing {@link EatingArea} ID<br>
     *             <b>{areaId}</b> existing {@link JoinAreaRequest} ID<br>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
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
     * @param requestId  ID of {@link JoinAreaRequest} made in the area of a currently authenticated user
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 200 Ok </li>
     *      <li>JSON object with fields {@code id, area, requester, status, created, decidedOn}</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found is returned if a {@link JoinAreaRequest} with the given ID does not exist
     *      in the user's {@link EatingArea}</li>
     *  </ul>
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<JoinRequestTo> getById(@PathVariable("id") String requestId) {
        JoinAreaRequest request = requestService.getByRequester(AuthorizedUser.get(), requestId);
        return ResponseEntity.ok(new JoinRequestTo(request));
    }

    /**
     * <p>Retrieves all {@link JoinAreaRequest} objects in the area of a currently authenticated user
     * with the given {@link JoinAreaRequest.JoinStatus}.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/{areaId}/requests 200}</font><br>
     *             <b>{areaId}</b> existing {@link EatingArea} ID<br>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>status</code></td>
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
     * @param status {@link JoinAreaRequest.JoinStatus}
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array where each object has fields {@code id, area, requester, status, created, decidedOn}</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *  </ul>
     */
    @GetMapping(params = "status", produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdmin
    public ResponseEntity<List<JoinRequestTo>> getAllInAreaOfStatus(@RequestParam JoinAreaRequest.JoinStatus status) {
        String areaId = ofNullable(AuthorizedUser.get()).map(User::getAreaId).orElse(null);
        List<JoinAreaRequest> requests = requestService.getByStatus(areaId, status);
        return ResponseEntity.ok(toDto(requests));
    }

    /**
     * <p>Retrieves all {@link JoinAreaRequest} objects of a currently authenticated user.
     * </p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/{areaId}/requests 200}</font><br>
     *             <b>{areaId}</b> existing {@link EatingArea} ID<br>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
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
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array where each object has fields {@code id, area, requester, status, created, decidedOn}</li>
     *  </ul>
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<List<JoinRequestTo>> getAllInAreaOfRequester() {
        List<JoinAreaRequest> requests = requestService.getByRequester(AuthorizedUser.get());
        return ResponseEntity.ok(toDto(requests));
    }

    /**
     * <p>Approves a specified {@link JoinAreaRequest}.
     * </p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP PUT /api/{areaId}/requests/{requestId} 204}</font><br>
     *             <b>{areaId}</b> existing {@link EatingArea} ID<br>
     *             <b>{requestId}</b> existing {@link JoinAreaRequest} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/x-www-form-urlencoded}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>{@code status}</td>
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
     * The request can be approved regardless of the membership or its absence in a certain area. The requester can be
     * a voter or an admin in an area. However, if the requester is the last user with <strong>ADMIN</strong> right in
     * his current area, approval for such request fails and {@link ua.belozorov.lunchvoting.exceptions.NoAreaAdminException}
     * is thrown
     *
     * @param requestId  ID of {@link JoinAreaRequest} in the area of approving user
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found, if a {@link JoinAreaRequest} with the given ID does not exist
     *      in the approving user's {@link EatingArea}</li>
     *      <li>HTTP Status 422 Unprocessable entity, if the requester has an area membership and
     *      is the only ADMIN in that area.</li>
     *  </ul>
     */
    @PutMapping(value = "/{id}", params = {"status=APPROVED"},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @IsAdmin
    public ResponseEntity approve(@PathVariable("id") String requestId) {
        requestService.approve(AuthorizedUser.get(), requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * <p>Rejects a specified {@link JoinAreaRequest}.
     * </p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP PUT /api/{areaId}/requests/{requestId} 204}</font><br>
     *             <b>{areaId}</b> existing {@link EatingArea} ID<br>
     *             <b>{requestId}</b> existing {@link JoinAreaRequest} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/x-www-form-urlencoded}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>{@code status}</td>
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
     * @param requestId  ID of {@link JoinAreaRequest} in the area of approving user
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found is returned if a {@link JoinAreaRequest} with the given ID does not exist
     *      in the approving user's {@link EatingArea}</li>
     *  </ul>
     */
    @PutMapping(value = "/{id}", params = {"status=REJECTED"},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @IsAdmin
    public ResponseEntity reject(@PathVariable("id") String requestId) {
        requestService.reject(AuthorizedUser.get(), requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * <p>Cancels a specified {@link JoinAreaRequest} previously made by a currently authenticated user.
     * </p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP PUT /api/{areaId}/requests/{requestId} 204}</font><br>
     *             <b>{areaId}</b> existing {@link EatingArea} ID<br>
     *             <b>{requestId}</b> existing {@link JoinAreaRequest} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/x-www-form-urlencoded}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>{@code status}</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>VOTER</strong></td>
     *     </tr>
     * </table>
     *
     * @param requestId  ID of {@link JoinAreaRequest} in the area of a currently authenticated user
     * @return ResponseEntity instance with the following values upon success:
     *  <ul>
     *      <li>HTTP Status 204 No_Content</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found is returned if a {@link JoinAreaRequest} with the given ID does not exist
     *      in the currently authenticated user's {@link EatingArea}</li>
     *  </ul>
     */
    @PutMapping(value = "/{id}", params = {"status=CANCELLED"},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @IsAdminOrVoter
    public ResponseEntity cancel(@PathVariable("id") String requestId) {
        requestService.cancel(AuthorizedUser.get(), requestId);
        return ResponseEntity.noContent().build();
    }

    public static List<JoinRequestTo> toDto(Collection<JoinAreaRequest> requests) {
        return requests.stream()
                .map(JoinRequestTo::new).collect(Collectors.toList());
    }
}
