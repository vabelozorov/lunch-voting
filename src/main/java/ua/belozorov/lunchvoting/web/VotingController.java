package ua.belozorov.lunchvoting.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.Vote;

import java.time.LocalDate;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController("/api/voting")
public class VotingController {

    @PostMapping
    public ResponseEntity vote(String placeId) {

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<List<Vote>> getTodaysVotesForPlace(@PathVariable String placeId) {
        List<Vote> votes = null;
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<MultiValueMap<LunchPlace, Vote>> getTodaysVotes() {
        MultiValueMap<LunchPlace, Vote> votes = null;
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }

    @GetMapping(value = "/{placeId}", params = {"byDate"})
    public ResponseEntity<List<Vote>> getVotesForPlaceByDate(@PathVariable String placeId, @RequestParam LocalDate byDate) {
        List<Vote> votes = null;
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }

    @GetMapping(params = {"userId"})
    public ResponseEntity<List<Vote>> getVotesOfUser(@RequestParam String userId) {
        List<Vote> votes = null;
        return new ResponseEntity<List<Vote>>(votes, HttpStatus.OK);
    }
}
