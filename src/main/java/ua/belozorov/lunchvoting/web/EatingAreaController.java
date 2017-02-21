package ua.belozorov.lunchvoting.web;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.service.area.EatingAreaService;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.02.17.
 */

@RestController
@RequestMapping(EatingAreaController.REST_URL)
public class EatingAreaController {
    static final String REST_URL = "/api/areas";

    private final EatingAreaService areaService;
    private final MessageSource messageSource;
    private final JsonFilter jsonFilter;

    @Autowired
    public EatingAreaController(EatingAreaService areaService,
                                MessageSource messageSource,
                                @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.areaService = areaService;
        this.messageSource = messageSource;
        this.jsonFilter = jsonFilter;
    }

    @PostMapping//any
    public ResponseEntity create(@RequestParam String name) {
        EatingArea area = ExceptionUtils.executeAndUnwrapException(
                () -> areaService.create(name, AuthorizedUser.get()),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_AREA_NAME, new Object[]{name})
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("{base}/{id}").buildAndExpand(REST_URL, area.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", area.getId()));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity createUserInArea(@PathVariable("id") String areaId,
                                           @RequestBody @Validated(UserTo.Create.class) UserTo userTo) {
        User newUser = new User(null, userTo.getName(), userTo.getEmail(), userTo.getPassword());
        User created = ExceptionUtils.executeAndUnwrapException(
                () -> areaService.createUserInArea(areaId, newUser),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_EMAIL, new Object[]{userTo.getEmail()})
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(UserManagementController.REST_URL + "/{id}").buildAndExpand(created.getId(), created.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", created.getId()));
    }


    /**
     * Creates a new LunchPlace object via HTTP POST request.
     * @param placeTo a LunchPlace object description in JSON format. <br/>
     *              Mandatory fields:
     *              <ul>
     *                  <li><b>name</b> (up to 50 characters, not empty)</li>
     *              </ul>
     *              Optional fields:
     *              <ul>
     *                  <li><b>address</b> string, up to 100 characters</li>
     *                  <li><b>description</b> string, up to 1000 characters</li>
     *                  <li><b>phones</b> an array of strings, each string is 10 characters long</li>
     *              </ul>
     * This method requires an authorization header to be present and ADMIN userRole
     * @return LunchPlace object with id assigned and Http 201 code on success
     */
    @PostMapping("/{areaId}/places")
    public ResponseEntity createPlaceInArea(@RequestBody @Validated(LunchPlaceTo.Create.class) LunchPlaceTo placeTo) {
        String areaId = AuthorizedUser.get().getAreaId();
        LunchPlace created = ExceptionUtils.executeAndUnwrapException(
                () -> areaService.createPlaceInArea(areaId, DtoIntoEntity.toLunchPlace(placeTo, null)),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_PLACE_NAME, new Object[]{placeTo.getName()})
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("{base}/{id}").buildAndExpand(LunchPlaceController.REST_URL, created.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", created.getId()));
    }


    /**
     * Creates a poll based on menus that currently exists with an effective date == today
     * @return
     */
    @PostMapping("/{areaId}/polls")
    public ResponseEntity createPollForTodayMenus() {
        String areaId = AuthorizedUser.get().getAreaId();
        LunchPlacePoll poll = areaService.createPollInArea(areaId);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PollController.REST_URL + "/{id}").buildAndExpand(areaId, poll.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }


    @PostMapping(value = "/{areaId}/polls", params = "menuDate")
    public ResponseEntity createPollForMenuDate(@RequestParam LocalDate menuDate) {
        String areaId = AuthorizedUser.get().getAreaId();
        LunchPlacePoll poll = areaService.createPollInArea(areaId, menuDate);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PollController.REST_URL + "/{id}").buildAndExpand(areaId, poll.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping
    public ResponseEntity update(@RequestParam String name) {
        ExceptionUtils.executeAndUnwrapException(
                () -> {areaService.updateAreaName(name, AuthorizedUser.get()); return null; },
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_AREA_NAME, new Object[]{name})
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaTo> get(@PathVariable String id, @RequestParam(defaultValue = "true") boolean summary) {
        AreaTo to = areaService.getAsTo(id, summary);
        return ResponseEntity.ok(to);
    }

    @GetMapping(value = "/filter", params = "name")
    public ResponseEntity<List<EatingArea>> filterByName(@RequestParam String name) {
        List<EatingArea> areas = areaService.filterByNameStarts(name);
        this.filterArea(areas);
        return ResponseEntity.ok(areas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        areaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void filterArea(Object obj) {
        Map<Class<?>, Set<String>> filterMap = new HashMap<>();
        filterMap.put(
                EatingArea.class, Stream.of("users", "places", "polls", "version").collect(Collectors.toSet())
        );
        jsonFilter.excludingFilter(obj, filterMap);
    }
}
