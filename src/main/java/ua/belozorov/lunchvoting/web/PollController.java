package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.PollTo;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.01.17.
 */
@RestController
public class PollController {
    static final String POLL_URL = "/api/polls";

    private final VotingService service;

    private final LunchPlaceJsonFilter jsonFilter;

    @Autowired
    public PollController(VotingService service, LunchPlaceJsonFilter jsonFilter) {
        this.service = service;
        this.jsonFilter = jsonFilter;
    }

    /**
     * Returns a currently active poll for the best lunch place's menu.
     * @return
     */
    @PostMapping
    public ResponseEntity<PollTo> createPollForTodayMenus() {
        Poll poll = service.createPollForTodayMenus();
        return new ResponseEntity<>(new PollTo(poll, false), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Poll> getFullPoll(@PathVariable String id) {
        Poll fullPoll = service.getPollFullDetails(id);
        PollTo to = new PollTo(fullPoll, true);

    }
}
