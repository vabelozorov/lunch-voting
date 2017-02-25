package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.PollTo;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A controller to manage {@link LunchPlacePoll} objects
 *
 * Created on 31.01.17.
 */
@RestController
@RequestMapping(PollController.REST_URL)
public class PollController {

    static final String REST_URL = "/api/areas/{areaId}/polls";

    private final PollService pollService;

    private final JsonFilter jsonFilter;

    /**
     * A constructor
     * @param votingService an instance of a class which implements {@link VotingService} interface
     * @param pollService an instance of a class which implements {@link PollService} interface
     * @param jsonFilter an instance of a class which implements {@link JsonFilter} interface
     */
    @Autowired
    public PollController(VotingService votingService,
                          PollService pollService,
                          @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.pollService = pollService;
        this.jsonFilter = jsonFilter;
    }

    /**
     * <p>Returns a LunchPlacePoll object by its ID.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/{pollId} 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{pollId}</b> existing {@link LunchPlacePoll} ID
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
     * @param pollId ID of existing {@link LunchPlacePoll} in the area of an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON object with fields {@code id, menuDate, timeConstraint, pollItems}<br>
     *          {@code timeConstraint} is an object with fields {@code startTime, endTime, voteChangeThreshold}<br>
     *              {@code pollItems} is an array of objects with fields {@code id, position, itemId}, where <code>itemId</code>
     *              refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found is returned if {@code pollId} refers to non-existent {@link LunchPlacePoll}
     *      in the authenticated user's {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}</li>
     *  </ul>
     */
    @GetMapping(value = "/{pollId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<PollTo> get(@PathVariable String pollId) {
        String areaId = AuthorizedUser.getAreaId();
        PollTo to = new PollTo(pollService.getWithPollItems(areaId, pollId));
        this.filterTo(to);
        return ResponseEntity.ok(to);
    }

    /**
     * <p>Returns a list of LunchPlacePoll objects in the area.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
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
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array where each object contains fields {@code id, menuDate, timeConstraint, pollItems}<br>
     *          {@code timeConstraint} is an object with fields {@code startTime, endTime, voteChangeThreshold}<br>
     *              {@code pollItems} is an array of objects with fields {@code id, position, itemId}, where <code>itemId</code>
     *              refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
     * </ul>
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<List<PollTo>> getAll() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getAll(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    /**
     * <p>Returns a list of LunchPlacePoll objects in the area filtered by their start or/and end time.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
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
     *         <td><code>start<br>end</code></td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     * @param start a start of filtering range for poll active time. If null, it's set to 1900/1/1 00:00:00
     * @param end an end of filtering range for poll active time. If null, it's set to 7777/1/1 00:00:00
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array where each object contains fields {@code id, menuDate, timeConstraint, pollItems}<br>
     *          {@code timeConstraint} is an object with fields {@code startTime, endTime, voteChangeThreshold}<br>
     *              {@code pollItems} is an array of objects with fields {@code id, position, itemId}, where <code>itemId</code>
     *              refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
     *  </ul>
     */
    @GetMapping(params = {"start", "end"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<List<PollTo>> getPollsByActivePeriod(@RequestParam LocalDateTime start,
                                                               @RequestParam LocalDateTime end) {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getPollsByActivePeriod(areaId, start, end), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    /**
     * <p>Returns a list of polls in the area which have ended.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/past 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
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
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array where each object contains fields {@code id, menuDate, timeConstraint, pollItems}<br>
     *          {@code timeConstraint} is an object with fields {@code startTime, endTime, voteChangeThreshold}<br>
     *              {@code pollItems} is an array of objects with fields {@code id, position, itemId}, where <code>itemId</code>
     *              refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
     *  </ul>
     */
    @GetMapping("/past")
    @IsAdminOrVoter
    public ResponseEntity<List<PollTo>> getPastPolls() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getPastPolls(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    /**
     * <p>Returns a list of polls in the area which are active for the moment of the request.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/active 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
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
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array where each object contains fields {@code id, menuDate, timeConstraint, pollItems}<br>
     *          {@code timeConstraint} is an object with fields {@code startTime, endTime, voteChangeThreshold}<br>
     *              {@code pollItems} is an array of objects with fields {@code id, position, itemId}, where <code>itemId</code>
     *              refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
     *  </ul>
     */
    @GetMapping("/active")
    @IsAdminOrVoter
    public ResponseEntity<List<PollTo>> getActivePolls() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getActivePolls(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    /**
     * <p>Returns a list of polls in the area which are scheduled for some time in the future.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/future 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
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
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array where each object contains fields {@code id, menuDate, timeConstraint, pollItems}<br>
     *          {@code timeConstraint} is an object with fields {@code startTime, endTime, voteChangeThreshold}<br>
     *              {@code pollItems} is an array of objects with fields {@code id, position, itemId}, where <code>itemId</code>
     *              refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
     *  </ul>
     */
    @GetMapping("/future")
    @IsAdminOrVoter
    public ResponseEntity<List<PollTo>> getFuturePolls() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getFuturePolls(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    /**
     * <p>Provides an information whether a poll is active for the moment of the request.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/active 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>{@code id}</td>
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
     * @param id ID of {@link LunchPlacePoll} in the area of authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>a JSON object with a fields:
     *      <ul>
     *          <li>{@code id} specifies requested poll ID</li>
     *          <li>{@code active} specifies the poll state as {@code true} or {@code false}</li>
     *      </ul>
     *      </li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404, if {@code id} refers to a non-existent {@link LunchPlacePoll} in the area of
     *      authenticated user</li>
     *  </ul>
     */
    @GetMapping(value = "/active", params = {"id"})
    @IsAdminOrVoter
    public ResponseEntity isPollActive(@RequestParam String id) {
        String areaId = AuthorizedUser.get().getAreaId();
        Map<String,Object> map = new HashMap<>();
        map.put("id", id);
        map.put("active", pollService.isPollActive(areaId, id));
        return ResponseEntity.ok(map);
    }

    /**
     * <p>Deletes a poll with a given ID.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP DELETE /api/areas/{areaId}/polls/{pollId} 204</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
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
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     * @param pollId ID of {@link LunchPlacePoll} in the area of authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 204 Ok</li>
     *  </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404, if {@code pollId} refers to a non-existent {@link LunchPlacePoll} in the area of
     *      authenticated user</li>
     *  </ul>
     */
    @DeleteMapping("{pollId}")
    @IsAdmin
    public ResponseEntity delete(@PathVariable String pollId) {
        String areaId = AuthorizedUser.get().getAreaId();
        pollService.delete(areaId, pollId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    static List<PollTo> convertIntoTo(Collection<LunchPlacePoll> polls, boolean includePollItems) {
        Function<LunchPlacePoll, PollTo> ref =  includePollItems ?
                PollTo::new : (poll) -> new PollTo(poll, false);
        return polls.stream().map(ref).collect(Collectors.toList());
    }

    private void filterTo(Object obj) {
        Map<Class<?>, Set<String>> map = new HashMap<>();
        map.put(
                PollItem.class,
                Stream.of("version", "position", "poll").collect(Collectors.toSet())
        );
        jsonFilter.excludingFilter(obj, map);
    }
}
