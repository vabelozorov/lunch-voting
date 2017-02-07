package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.PollTo;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.01.17.
 */
@RestController
@RequestMapping(PollController.POLL_URL)
public final class PollController {
    static final String POLL_URL = "/api/polls";

    private final PollService pollService;

    private final JsonFilter jsonFilter;

    @Autowired
    public PollController(VotingService votingService,
                          PollService pollService,
                          @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.pollService = pollService;
        this.jsonFilter = jsonFilter;
    }

    /**
     * Creates a poll based on menus that currently exists with an effective date == today
     * @return
     */
    @PostMapping
    public ResponseEntity createPollForTodayMenus() {
        Poll poll = pollService.createPollForTodayMenus();
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(POLL_URL + "/{id}").buildAndExpand(poll.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping(params = "menuDate")
    public ResponseEntity createPollForMenuDate(@RequestParam LocalDate menuDate) {
        Poll poll = pollService.createPollForMenuDate(menuDate);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(POLL_URL + "/{id}").buildAndExpand(poll.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PollTo> get(@PathVariable String id) {
        PollTo to = new PollTo(pollService.get(id));
        this.filterTo(to);
        return ResponseEntity.ok(to);
    }

    @GetMapping
    public ResponseEntity<List<PollTo>> getAll() {
        List<PollTo> tos = convertIntoTo(pollService.getAll());
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable String id) {
        pollService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping(params = {"start", "end"})
    public ResponseEntity<List<PollTo>> getPollsByActivePeriod(@RequestParam LocalDateTime start,
                                                               @RequestParam LocalDateTime end) {
        List<PollTo> tos = convertIntoTo(pollService.getPollsByActivePeriod(start, end));
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PollTo>> getActivePolls() {
        List<PollTo> tos = convertIntoTo(pollService.getActivePolls());
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/future")
    public ResponseEntity<List<PollTo>> getFuturePolls() {
        List<PollTo> tos = convertIntoTo(pollService.getFuturePolls());
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/past")
    public ResponseEntity<List<PollTo>> getPastPolls() {
        List<PollTo> tos = convertIntoTo(pollService.getPastPolls());
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/active/{id}")
    public ResponseEntity isPollActive(@PathVariable String id) {
        Map<String,Boolean> map = new HashMap<>();
        map.put(id, pollService.isPollActive(id));
        return ResponseEntity.ok(map);
    }

    public static List<PollTo> convertIntoTo(Collection<Poll> polls) {
        return polls.stream().map(PollTo::new).collect(Collectors.toList());
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
