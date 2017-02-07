package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
        Vote vote = votingService.vote(voterId, pollId, pollItemId);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(VOTE_URL + "/" + vote.getId()).build().toUri();
        return ResponseEntity.created(uri).body(new VoteTo(vote, true));
    }

    @GetMapping("/polls/{id}")
    public ResponseEntity getVotesForPoll(@PathVariable("id") String pollId) {
        Collection<Vote> votesForPoll = votingService.getVotesForPoll(pollId);
        Collection<VoteTo> tos = convertIntoTo(votesForPoll, false);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pollId", pollId);
        map.put("votes", tos);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/polls/{id}/result/perItem")
    public ResponseEntity<CountPerItemTo> getPollResult(@PathVariable("id") String pollId) {
        VotingResult<PollItem> pollResult = votingService.getPollResult(pollId);
        CountPerItemTo to = new CountPerItemTo(pollResult, pollId);
        return ResponseEntity.ok(to);
    }

    @GetMapping(value = "/polls/{id}", params = "voterId")
    public ResponseEntity getVotedByVoter(@PathVariable("id") String pollId, @RequestParam String voterId) {
        Collection<String> votedByVoter = votingService.getVotedByVoter(pollId, voterId);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pollId", pollId);
        map.put("voterId", voterId);
        map.put("votedItems", votedByVoter);
        return ResponseEntity.ok(map);
    }

    public static Collection<VoteTo> convertIntoTo(Collection<Vote> votes, boolean includePollId) {
        return votes.stream().map(vote -> new VoteTo(vote, includePollId)).collect(Collectors.toList());
    }

//    private void removePollId()
}
