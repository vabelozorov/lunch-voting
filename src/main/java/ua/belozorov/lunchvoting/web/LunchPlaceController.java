package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;
import ua.belozorov.lunchvoting.web.queries.LunchPlaceQueryParams;

import java.net.URI;
import java.util.*;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(LunchPlaceController.REST_URL)
public class LunchPlaceController  {
    static final String REST_URL = "/api/places";

    private final LunchPlaceService placeService;

    private final JsonFilter jsonFilter;

    @Autowired
    public LunchPlaceController(LunchPlaceService placeService,
                                @Qualifier("lunchPlaceJsonFilter") JsonFilter jsonFilter) {
        this.jsonFilter = jsonFilter;
        this.placeService = placeService;
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
    @PostMapping
    public ResponseEntity create(@RequestBody LunchPlaceTo placeTo) {
        LunchPlace created = placeService.create(
                DtoIntoEntity.toLunchPlace(placeTo, AuthorizedUser.get().getId()),
                AuthorizedUser.get()
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("{base}/{id}").buildAndExpand(REST_URL, created.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", created.getId()));
    }

    /**
     * Updates an existing LunchPlace object. The following parameters are accepted: <br/>
     *              Mandatory fields:
     *              <ul>
     *                  <li><b>id</b> 36 characters long string</li>
     *              </ul>
     *              Optional fields:
     *              <ul>
     *                  <li><b>name</b> (up to 50 characters, not empty)</li>
     *                  <li><b>address</b> string, up to 100 characters</li>
     *                  <li><b>description</b> string, up to 1000 characters</li>
     *                  <li><b>phones</b> an array of strings, each string is 10 characters long</li>
     *              </ul>
     * This method requires an authorization header to be present and ADMIN userRole of a user
     * @param placeTo
     * @return Http 204 code on success
     */
    @PutMapping
    public ResponseEntity update(@RequestBody LunchPlaceTo placeTo) {
        LunchPlace place = DtoIntoEntity.toLunchPlace(placeTo, AuthorizedUser.get().getId());
        placeService.update(place, AuthorizedUser.get());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Returns a JSON representation of a LunchPlace object containing the following fields:
     *              <ul>
     *                  <li><b>id</b> (36 characters)</li>
     *                  <li><b>name</b> (up to 50 characters, not empty)</li>
     *                  <li><b>address</b> string, up to 100 characters</li>
     *                  <li><b>description</b> string, up to 1000 characters</li>
     *                  <li><b>phones</b> an array of strings, each string is 10 characters long</li>
     *              </ul>
     * The method does not require authorization.
     * @param id id of the place
     * @return
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<LunchPlace> get(@PathVariable String id, LunchPlaceQueryParams params) {
        params.setIds(new String[]{id});
        RefinedFields refinedFields = new LunchPlaceRefinedFields(params.getFields());
        Collection<LunchPlace> places = this.getLunchPlacesOptimally(params, refinedFields);
        LunchPlace place = places.iterator().next();
        jsonFilter.includingFilter(place, refinedFields);
        return ResponseEntity.ok(place);
    }

    @GetMapping
    public ResponseEntity<Collection<LunchPlace>> getLunchPlaces(LunchPlaceQueryParams params) {
        RefinedFields refinedFields = new LunchPlaceRefinedFields(params.getFields());
        Collection<LunchPlace> places = this.getLunchPlacesOptimally(params, refinedFields);
        jsonFilter.includingFilter(places, refinedFields);
        return ResponseEntity.ok(places);
    }

    private Collection<LunchPlace> getLunchPlacesOptimally(LunchPlaceQueryParams params, RefinedFields refinedFields) {
        Collection<LunchPlace> places;

        // Conditions on which LP objects have to have 'menus' field loaded from DB
        if (params.hasDates() || refinedFields.containsOriginal("menus")) {
            places = placeService.getMultipleWithMenu(params.getIds(), params.getStartDate(), params.getEndDate(), AuthorizedUser.get());
        // LP w/o its associations is enough
        } else  {
            places = placeService.getMultiple(params.getIds(), AuthorizedUser.get());
        }
        return places;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        placeService.delete(id, AuthorizedUser.get());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    private final static class LunchPlaceRefinedFields implements RefinedFields {
        static final Set<String> MANDATORY_EXCLUDE = new HashSet<>(Arrays.asList("version", "adminId"));
        static final Set<String> MANDATORY_INCLUDE = new HashSet<>(Arrays.asList("id"));
        static final Set<String> DEFAULT_INCLUDE = new HashSet<>();
        static {
            DEFAULT_INCLUDE.addAll(MANDATORY_INCLUDE);
            DEFAULT_INCLUDE.add("name");
        }

        private final Set<String> originalFields = new HashSet<>();
        private final Map<String, String> fieldReplacements = new HashMap<>();

        private LunchPlaceRefinedFields(Set<String> fields) {
            this.originalFields.addAll(fields);
            this.fieldReplacements.put("menus", "menus.*");
        }

        private Set<String> refine(Set<String> fields) {
            Set<String> result = new HashSet<>(fields);
            if (result.isEmpty()) {
                result.addAll(DEFAULT_INCLUDE);
            }
            MANDATORY_EXCLUDE.forEach(result::remove);
            MANDATORY_INCLUDE.forEach(result::add);

            this.fieldReplacements.forEach((k,v) -> {
                    if (result.remove(k)) {
                        result.add(v);
                    }
            });
            return result;
        }

        @Override
        public Set<String> get() {
            return Collections.unmodifiableSet(this.refine(this.originalFields));
        }

        @Override
        public boolean containsOriginal(String field) {
            return originalFields.contains(field);
        }
    }
}
