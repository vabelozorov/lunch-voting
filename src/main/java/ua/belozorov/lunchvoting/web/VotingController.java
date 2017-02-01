package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.VoteTo;
import ua.belozorov.lunchvoting.web.queries.VoteQueryParams;

import java.net.URI;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(VotingController.VOTE_URL)
public class VotingController {
    static final String VOTE_URL = "/api/votes";

    @Autowired
    private VotingService service;

    @PostMapping
    public ResponseEntity<VoteTo> vote(@RequestBody VoteQueryParams params) {
        Vote vote = service.vote(params.getVoterId(), params.getPollId(), params.getItemId());
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(VOTE_URL + "/" + vote.getId()).build().toUri();
        return ResponseEntity.created(uri).body(new VoteTo(vote));
    }


//    @GetMapping("/{placeId}")
//    public ResponseEntity<List<Vote>> getTodaysVotesForPlace(@PathVariable String placeId) {
//        List<Vote> votes = null;
//        return new ResponseEntity<>(votes, HttpStatus.OK);
//    }
//
//    @GetMapping
//    public ResponseEntity<MultiValueMap<LunchPlace, Vote>> getTodaysVotes() {
//        MultiValueMap<LunchPlace, Vote> votes = null;
//        return new ResponseEntity<>(votes, HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/{placeId}", params = {"byDate"})
//    public ResponseEntity<List<Vote>> getVotesForPlaceByDate(@PathVariable String placeId, @RequestParam LocalDate byDate) {
//        List<Vote> votes = null;
//        return new ResponseEntity<>(votes, HttpStatus.OK);
//    }
//
//    @GetMapping(params = {"userId"})
//    public ResponseEntity<List<Vote>> getVotesOfUser(@RequestParam String userId) {
//        List<Vote> votes = null;
//        return new ResponseEntity<List<Vote>>(votes, HttpStatus.OK);
//    }
}
