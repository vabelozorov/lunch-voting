package ua.belozorov.lunchvoting.web;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.queries.LunchPlaceQueryParams;

import java.util.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(LunchPlaceController.REST_URL)
public class LunchPlaceController  {
    static final String REST_URL = "/api/areas/{areaId}/places";

    private final LunchPlaceService placeService;

    private final JsonFilter jsonFilter;

    @Autowired
    public LunchPlaceController(LunchPlaceService placeService,
                                @Qualifier("lunchPlaceJsonFilter") JsonFilter jsonFilter) {
        this.jsonFilter = jsonFilter;
        this.placeService = placeService;
    }

    /**
     * Updates an existing LunchPlace object. The following parameters, any of them is optional, are accepted: <br/>
     *              <ul>
     *                  <li><b>name</b> (up to 50 characters, not empty)</li>
     *                  <li><b>address</b> string, up to 100 characters</li>
     *                  <li><b>description</b> string, up to 1000 characters</li>
     *                  <li><b>phones</b> an array of strings, each string consists of 10 digits</li>
     *              </ul>
     * This method requires an authorization header to be present and ADMIN userRole of a user
     * @param to
     * @return Http 204 code on success
     */
    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable String id, @RequestBody LunchPlaceTo to) {
        String areaId = AuthorizedUser.get().getAreaId();
        LunchPlace place = DtoIntoEntity.toLunchPlace(to, id);
        ExceptionUtils.executeAndUnwrapException(
                () -> {
                    placeService.bulkUpdate(areaId, id, to.getName(), to.getAddress(), to.getDescription(), to.getPhones());
                    return null;
                },
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_PLACE_NAME, new Object[]{to.getName()})
        );
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
    @GetMapping("/{id}")
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

    private List<LunchPlace> getLunchPlacesOptimally(LunchPlaceQueryParams params, RefinedFields refinedFields) {
        List<LunchPlace> places;

        // Conditions on which LP objects have to have 'menus' field loaded from DB
        if (params.hasDates() || refinedFields.containsOriginal("menus")) {
            places = placeService.getMultipleWithMenu(AuthorizedUser.get().getAreaId(), params.getIds(), params.getStartDate(), params.getEndDate());
        // LP w/o its associations is enough
        } else  {
            places = placeService.getMultiple(AuthorizedUser.get().getAreaId(), params.getIds());
        }
        return places;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        placeService.delete(AuthorizedUser.get().getAreaId(), id);
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
