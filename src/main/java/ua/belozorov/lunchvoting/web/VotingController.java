package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.voting.VotingResult;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.CountPerItemTo;
import ua.belozorov.lunchvoting.to.VoteTo;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A controller to manage voting process and providing results of voting
 *
 * Created on 15.11.16.
 */
@RestController
@RequestMapping(VotingController.REST_URL)
@IsAdminOrVoter
public class VotingController {
    static final String REST_URL = "/api/areas/{areaId}/votes";

    private final VotingService votingService;
    private final JsonFilter jsonFilter;

    /**
     * A constructor
     * @param votingService an instance of a class that implements {@link VotingService} interface
     * @param jsonFilter an instance of a class that implements {@link JsonFilter} interface
     */
    public VotingController(VotingService votingService,
                            @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.votingService = votingService;
        this.jsonFilter = jsonFilter;
    }

    /**
     * <p>Accepts and reviews a vote from an authenticated user.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/votes 201</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td><code>application/x-www-form-urlencoded</code></td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>pollId, pollItemId</code></td>
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
     * <p>Voter can vote for a poll when the poll is active. Only one vote per poll is accepted.
     * Voter can change his/her mind before poll's voteChange time. In that case the previous vote is deleted and a new
     * vote is accepted</p>
     * @param pollId ID of existing {@link ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll} in the area of
     *               an authenticated user
     * @param pollItemId ID of existing {@link ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll} in the area of
     *                   an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 201 Created</li>
     *      <li>a JSON object with fields <code>id, voterId, pollId, itemId</code></li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found, if <code>pollId</code> or <code>pollItemId</code> refers to a non-existent object
     *      in the area of an authenticated user</li>
     *      <li>HTTP Status 422 Unprocessable_Entity, if:
     *          <ul>
     *              <li>the poll is not active</li>
     *              <li>vote change attempt is made after the
     *                  corresponding time threshold</li>
     *              <li>2nd and subsequent attempt to vote for the same item is made</li>
     *          </ul>
     *      </li>
     *  </ul>
     */
    @PostMapping(params = {"pollId", "pollItemId"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VoteTo> vote(@RequestParam String pollId,
                                       @RequestParam String pollItemId) {
        User voter = AuthorizedUser.get();
        Vote vote = votingService.vote(voter, pollId, pollItemId);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/" + vote.getId()).build().toUri();
        return ResponseEntity.created(uri).body(new VoteTo(vote, true));
    }

    /**
     * <p>Returns all Vote objects made for a specified poll.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/votes 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>pollId</code></td>
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
     * @param pollId ID of existing {@link ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll} in the area
     *               of an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200</li>
     *      <li>a JSON object with fields:
     *          <ul>
     *              <li><code>pollId</code></li>
     *              <li><code>votes</code> a JSON array of Vote objects with fields <code>id, voterId, itemId</code></li>
     *          </ul>
     *      </li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found, if <code>pollId</code> refers to a non-existent object
     *      in the area of an authenticated user</li>
     *  </ul>
     */
    @GetMapping(params = {"pollId"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getVotesForPoll(@RequestParam String pollId) {
        String areaId = AuthorizedUser.getAreaId();
        List<Vote> votesForPoll = votingService.getFullVotesForPoll(areaId, pollId);
        List<VoteTo> tos = convertIntoTo(votesForPoll, false);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pollId", pollId);
        map.put("votes", tos);
        return ResponseEntity.ok(map);
    }

    /**
     * <p>Returns the result of voting for a poll grouped by an item</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/votes/results 200</code></font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>type=item,pollId</code></td>
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
     * @param pollId ID of an existing {@link ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll} in the area
     *               of an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200</li>
     *      <li>a JSON object with fields
     *          <ul>
     *              <li><code>pollId</code></li>
     *              <li><code>result</code> a JSON object representing a poll result.
     *              Contains fields <code>pollItemId, itemId, count</code>, where <code>itemId </code> refers to
     *              an ID of {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace}</li>
     *          </ul>
     *      </li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400, if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found, if <code>pollId</code> refers to a non-existent object
     *      in the area of an authenticated user</li>
     *  </ul>
     */
    @GetMapping(value = "/results", params = {"type=item"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CountPerItemTo> getPollResult(@RequestParam String pollId) {
        String areaId = AuthorizedUser.get().getAreaId();
        VotingResult<PollItem> pollResult = votingService.getPollResult(areaId, pollId);
        CountPerItemTo to = new CountPerItemTo(pollResult, pollId);
        return ResponseEntity.ok(to);
    }

    /**
     * <p>Returns a list of item ID that an authenticated user has voted for.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/votes 200</code></font>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td><code>filterBy=voter, pollId</code></td>
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
     * @param pollId ID of an existing {@link ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll} in the area
     *               of an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status </li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400, if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found, if <code>pollId</code> refers to a non-existent object
     *      in the area of an authenticated user</li>
     *  </ul>
     */
    @GetMapping(params = {"filterBy=voter", "pollId"})
    public ResponseEntity getVotedByVoter(@RequestParam String pollId) {
        User voter = AuthorizedUser.get();
        List<String> votedByVoter = votingService.getVotedForPollByVoter(voter, pollId);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pollId", pollId);
        map.put("voterId", voter.getId());
        map.put("votedPollItems", votedByVoter);
        return ResponseEntity.ok(map);
    }

    /**
     * <p>Revokes a user vote.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP DELETE /api/areas/{areaId}/votes 204</code></font>
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Request Content-Type</td>
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
     * @param voteId ID of an existing vote which was made by an authenticated user.
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 204 No_Content</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found, if <code>voteId</code> refers to a non-existent object or the vote has been
     *      made by another voter.</li>
     *  </ul>
     */
    @DeleteMapping("/{voteId}")
    public ResponseEntity revokeVote(@PathVariable String voteId) {
        User voter = AuthorizedUser.get();
        votingService.revokeVote(voter.getId(),voteId);
        return ResponseEntity.noContent().build();
    }

    static List<VoteTo> convertIntoTo(Collection<Vote> votes, boolean includePollId) {
        return votes.stream().map(vote -> new VoteTo(vote, includePollId)).collect(Collectors.toList());
    }
}
