package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.PollTo;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.01.17.
 */
@RestController
@RequestMapping(PollController.REST_URL)
public final class PollController {
    static final String REST_URL = "/api/areas/{areaid}/polls";

    private final PollService pollService;

    private final JsonFilter jsonFilter;

    @Autowired
    public PollController(VotingService votingService,
                          PollService pollService,
                          @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.pollService = pollService;
        this.jsonFilter = jsonFilter;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PollTo> get(@PathVariable String id) {
        String areaId = AuthorizedUser.get().getAreaId();
        PollTo to = new PollTo(pollService.getWithPollItems(areaId, id));
        this.filterTo(to);
        return ResponseEntity.ok(to);
    }

    @GetMapping
    public ResponseEntity<List<PollTo>> getAll() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getAll(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping(params = {"start", "end"})
    public ResponseEntity<List<PollTo>> getPollsByActivePeriod(@RequestParam LocalDateTime start,
                                                               @RequestParam LocalDateTime end) {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getPollsByActivePeriod(areaId, start, end), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/past")
    public ResponseEntity<List<PollTo>> getPastPolls() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getPastPolls(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PollTo>> getActivePolls() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getActivePolls(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/future")
    public ResponseEntity<List<PollTo>> getFuturePolls() {
        String areaId = AuthorizedUser.get().getAreaId();
        List<PollTo> tos = convertIntoTo(pollService.getFuturePolls(areaId), false);
        this.filterTo(tos);
        return ResponseEntity.ok(tos);
    }

    @GetMapping("/active/{id}")
    public ResponseEntity isPollActive(@PathVariable String id) {
        String areaId = AuthorizedUser.get().getAreaId();
        Map<String,Boolean> map = new HashMap<>();
        map.put(id, pollService.isPollActive(areaId, id));
        return ResponseEntity.ok(map);
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

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable String id) {
        String areaId = AuthorizedUser.get().getAreaId();
        pollService.delete(areaId, id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
