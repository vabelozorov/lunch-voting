package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.voting.VotingResult;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.CountPerItemTo;
import ua.belozorov.lunchvoting.to.VoteTo;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(VotingController.VOTE_URL)
public final class VotingController {
    static final String VOTE_URL = "/api/votes";

    private final VotingService votingService;
    private final JsonFilter jsonFilter;

    public VotingController(VotingService votingService,
                            @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.votingService = votingService;
        this.jsonFilter = jsonFilter;
    }

    @PostMapping("/polls/{id}")
    public ResponseEntity<VoteTo> vote(@PathVariable("id") String pollId,
                                       @RequestParam String voterId,
                                       @RequestParam String pollItemId) {
        User voter = AuthorizedUser.get();
        Vote vote = votingService.vote(voter, pollId, pollItemId);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(VOTE_URL + "/" + vote.getId()).build().toUri();
        return ResponseEntity.created(uri).body(new VoteTo(vote, true));
    }

    @GetMapping("/polls/{id}")
    public ResponseEntity getVotesForPoll(@PathVariable("id") String pollId) {
        String areaId = AuthorizedUser.get().getAreaId();
        List<Vote> votesForPoll = votingService.getVotesForPoll(areaId, pollId);
        List<VoteTo> tos = convertIntoTo(votesForPoll, false);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pollId", pollId);
        map.put("votes", tos);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/polls/{id}/result/perItem")
    public ResponseEntity<CountPerItemTo> getPollResult(@PathVariable("id") String pollId) {
        String areaId = AuthorizedUser.get().getAreaId();
        VotingResult<PollItem> pollResult = votingService.getPollResult(areaId, pollId);
        CountPerItemTo to = new CountPerItemTo(pollResult, pollId);
        return ResponseEntity.ok(to);
    }

    @GetMapping(value = "/polls/{id}", params = "voterId")
    public ResponseEntity getVotedByVoter(@PathVariable("id") String pollId, @RequestParam String voterId) {
        User voter = AuthorizedUser.get();
        List<String> votedByVoter = votingService.getVotedByVoter(voter, pollId);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pollId", pollId);
        map.put("voterId", voterId);
        map.put("votedItems", votedByVoter);
        return ResponseEntity.ok(map);
    }

    static List<VoteTo> convertIntoTo(Collection<Vote> votes, boolean includePollId) {
        return votes.stream().map(vote -> new VoteTo(vote, includePollId)).collect(Collectors.toList());
    }
}
