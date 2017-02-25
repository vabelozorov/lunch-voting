package ua.belozorov.lunchvoting.web;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.queries.LunchPlaceQueryParams;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import javax.validation.Valid;
import java.util.*;

/**
 *A controller to manage {@link LunchPlace} objects
 *
 * Created on 15.11.16.
 */
@RestController
@RequestMapping(LunchPlaceController.REST_URL)
public class LunchPlaceController  {

    static final String REST_URL = "/api/areas/{areaId}/places";

    private final LunchPlaceService placeService;

    private final JsonFilter jsonFilter;

    /**
     * A constructor
     * @param placeService any instance that implements LunchPlaceService interface
     * @param jsonFilter any instance that implements JsonFilter interface
     */
    @Autowired
    public LunchPlaceController(LunchPlaceService placeService,
                                @Qualifier("lunchPlaceJsonFilter") JsonFilter jsonFilter) {
        this.jsonFilter = jsonFilter;
        this.placeService = placeService;
    }

    /**
     * Updates an existing {@link LunchPlace} object.
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP PUT /api/areas/{areaId}/places/{placeId} 204}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{placeId}</b> existing {@link LunchPlace} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td><code>application/json</code></td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td><code>name<br>address<br>description<br>phones</code></td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param dto represents request parameters, any of them is optional:
     * <ul>
     *  <li><b>name</b> (2-50 characters)</li>
     *  <li><b>address</b> string, up to 200 characters</li>
     *  <li><b>description</b> string, up to 1000 characters</li>
     *  <li><b>phones</b> an array of strings with length <=5, each string consists of 10 digits</li>
     * </ul>
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 204 No_Content</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found is returned if a {@link LunchPlace}  with the given ID does not exist
     *      in the currently authenticated user's {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}</li>
     *  </ul>
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdmin
    public ResponseEntity update(@PathVariable String id, @RequestBody @Validated LunchPlaceTo dto) {
        String areaId = AuthorizedUser.get().getAreaId();
        LunchPlace place = DtoIntoEntity.toLunchPlace(dto, id);
        ExceptionUtils.executeAndUnwrapException(
                () -> {
                    placeService.bulkUpdate(areaId, id, dto.getName(), dto.getAddress(), dto.getDescription(), dto.getPhones());
                    return null;
                },
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_PLACE_NAME, new Object[]{dto.getName()})
        );
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>Returns a {@link LunchPlace} with a given IDs in the area of an authenticated user.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/areas/{areaId}/places/{placeId} 200}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{placeId}</b> existing {@link LunchPlace} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td><code>application/x-www-form-urlencoded</code></td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td><code>fields<br>startDate<br>endDate</code></td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param placeId ID of a {@link LunchPlace}
     * @param params represents query parameters. Can contain the following fields:
     *               <ul>
     *                  <li><strong>fields</strong>  fields to display in the returned JSON response. Available values:
     *               {@code name, address, description, phones, menus}. Additionally, a field {@code id} is always contained
     *               in the response.</li>
     *                  <li><strong>startDate</strong>  instructs to include in the response {@link LunchPlace} objects which
     *               have menus belonging to the time range starting with <strong>startDate</strong>. Only matching menus are
     *               included to the list of {@link LunchPlace} menus.</li>
     *                  <li><strong>endDate</strong>  instructs to include in the response {@link LunchPlace} objects which
     *               have menus belonging to the time range ending with <strong>endDate</strong>. Only matching menus are
     *               included to the list of {@link LunchPlace} menus.</li>
     *               </ul>
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array of objects each containing fields {@code id, name} if {@code fields} parameter
     *      was not specified in the request. Otherwise each JSON object will contain a mandatory {@code id} field and
     *      fields that were specified by a user.</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found is returned if a {@link LunchPlace}  with the given ID does not exist
     *      in the currently authenticated user's {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}</li>
     *  </ul>
     */
    @GetMapping(value = "/{placeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<LunchPlace> get(@PathVariable String placeId, LunchPlaceQueryParams params) {
        params.setIds(new String[]{placeId});
        RefinedFields refinedFields = new LunchPlaceRefinedFields(params.getFields());
        List<LunchPlace> places = this.getLunchPlacesOptimally(params, refinedFields);
        LunchPlace place = places.iterator().next();
        jsonFilter.includingFilter(place, refinedFields);
        return ResponseEntity.ok(place);
    }

    /**
     * <p>Returns multiple {@link LunchPlace} objects from the area of an authenticated user.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">{@code HTTP GET /api/areas/{areaId}/places 200}</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td><code>application/x-www-form-urlencoded</code></td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td><code>ids<br>fields<br>startDate<br>endDate</code></td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param params represents query parameters. Can contain the following fields:
     *               <ul>
     *                  <li><strong>ids</strong>  specifies which objects a user wants to request. If not provided, all objects
     *               will be returned</li>
     *                  <li><strong>fields</strong>  fields to display in the returned JSON response. Available values:
     *               {@code name, address, description, phones, menus}. Additionally, a field {@code id} is always contained
     *               in the response</li>
     *                  <li><strong>startDate</strong>  instructs to include in the response {@link LunchPlace} objects which
     *               have menus belonging to the time range starting with <strong>startDate</strong>. Only matching menus are
     *               included to the list of {@link LunchPlace} menus</li>
     *                  <li><strong>endDate</strong>  instructs to include in the response {@link LunchPlace} objects which
     *               have menus belonging to the time range ending with <strong>endDate</strong>. Only matching menus are
     *               included to the list of {@link LunchPlace} menus</li>
     *               </ul>
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>JSON array of objects each containing fields {@code id, name} if {@code fields} parameter
     *      was not specified in the request. Otherwise each JSON object will contain a mandatory {@code id} field and
     *      fields that were specified by a user.</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found is returned if a provided <code>ids</code> parameter refers to
     *      non-existing {@link LunchPlace} in the currently authenticated user's
     *      {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}</li>
     *  </ul>
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdminOrVoter
    public ResponseEntity<Collection<LunchPlace>> getMany(LunchPlaceQueryParams params) {
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

    /**
     * <p>Deletes a {@link LunchPlace} with a given ID in the area of the authenticated user. </p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green">HTTP DELETE /api/areas/{areaId}/places/{placeId} 204</font><br>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{placeId}</b> existing {@link LunchPlace} ID
     *         </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Optional Parameters</td>
     *         <td>none</td>
     *     </tr>
     *     <tr>
     *         <td>Requires role</td>
     *         <td><strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param id ID of existing {@link LunchPlace}
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 204 No_Content</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found is returned if <code>ID</code> refers to non-existent {@link LunchPlace}
     *      in the currently authenticated user's {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea}</li>
     *  </ul>
     */
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity delete(@PathVariable String id) {
        placeService.delete(AuthorizedUser.get().getAreaId(), id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * @see RefinedFields
     */
    private final static class LunchPlaceRefinedFields implements RefinedFields {
        static final Set<String> MANDATORY_EXCLUDE = new HashSet<>(Arrays.asList("version"));
        static final Set<String> MANDATORY_INCLUDE = new HashSet<>(Arrays.asList("id"));
        static final Set<String> DEFAULT_INCLUDE = new HashSet<>();
        static {
            DEFAULT_INCLUDE.addAll(MANDATORY_INCLUDE);
            DEFAULT_INCLUDE.add("name");
        }

        private final Set<String> originalFields = new HashSet<>();
        private final Map<String, String> fieldReplacements = new HashMap<>();

        private LunchPlaceRefinedFields() {
            this.fieldReplacements.put("menus", "menus.*");
        }

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
