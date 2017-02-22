package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.service.area.JoinAreaRequestService;
import ua.belozorov.lunchvoting.to.JoinRequestTo;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.02.17.
 */
@RestController
@RequestMapping(JoinAreaRequestController.REST_URL)
public class JoinAreaRequestController {
    static final String REST_URL = "/api/requests";

    private final JoinAreaRequestService requestService;

    private final MessageSource messageSource;

    private final JsonFilter jsonFilter;

    @Autowired
    public JoinAreaRequestController(JoinAreaRequestService requestService,
                                     MessageSource messageSource, @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.requestService = requestService;
        this.messageSource = messageSource;
        this.jsonFilter = jsonFilter;
    }

    @PostMapping
    @IsAdminOrVoter
    public ResponseEntity makeRequest(@RequestParam("id") String areaId) {
        JoinAreaRequest request = requestService.make(AuthorizedUser.get(), areaId);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("{base}/{id}").buildAndExpand(REST_URL, request.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", request.getId()));
    }

    @GetMapping("/{id}")
    @IsAdminOrVoter
    public ResponseEntity<JoinRequestTo> getById(@PathVariable("id") String requestId) {
        JoinAreaRequest request = requestService.getByRequester(AuthorizedUser.get(), requestId);
        return ResponseEntity.ok(new JoinRequestTo(request));
    }

    @GetMapping(params = "status")
    @IsAdmin
    public ResponseEntity<List<JoinRequestTo>> getAllInAreaOfStatus(@RequestParam JoinAreaRequest.JoinStatus status) {
        String areaId = ofNullable(AuthorizedUser.get()).map(User::getAreaId).orElse(null);
        List<JoinAreaRequest> requests = requestService.getByStatus(areaId, status);
        return ResponseEntity.ok(toDto(requests));
    }

    @GetMapping
    @IsAdminOrVoter
    public ResponseEntity<List<JoinRequestTo>> getAllInAreaOfRequester() {
        List<JoinAreaRequest> requests = requestService.getByRequester(AuthorizedUser.get());
        return ResponseEntity.ok(toDto(requests));
    }

    @PutMapping("/{id}/approve")
    @IsAdmin
    public ResponseEntity approve(@PathVariable("id") String requestId) {
        requestService.approve(AuthorizedUser.get(), requestId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reject")
    @IsAdmin
    public ResponseEntity reject(@PathVariable("id") String requestId) {
        requestService.reject(AuthorizedUser.get(), requestId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    @IsAdminOrVoter
    public ResponseEntity cancel(@PathVariable("id") String requestId) {
        requestService.cancel(AuthorizedUser.get(), requestId);
        return ResponseEntity.noContent().build();
    }

    public static List<JoinRequestTo> toDto(Collection<JoinAreaRequest> requests) {
        return requests.stream()
                .map(JoinRequestTo::new).collect(Collectors.toList());
    }
}
