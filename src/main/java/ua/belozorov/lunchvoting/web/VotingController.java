package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.service.voting.VotingService;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(VotingController.REST_URL)
public class VotingController {
    static final String REST_URL = "/api/voting";

    @Autowired
    private VotingService service;

    @PostMapping
    public ResponseEntity vote(String placeId) {

        return new ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * Returns a currently active poll for the best lunch place's menu.
     * @return
     */
    @GetMapping()
    public ResponseEntity<String> getRunningMenuPoll() {
        String pollId = service.createPollForTodayMenus();
        return new ResponseEntity<>(pollId, HttpStatus.OK);
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
