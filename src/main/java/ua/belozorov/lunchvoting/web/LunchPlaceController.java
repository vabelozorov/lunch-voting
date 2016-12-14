package ua.belozorov.lunchvoting.web;

import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;

import java.net.URI;
import java.util.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(LunchPlaceController.REST_URL)
public class LunchPlaceController extends AbstractController {
    static final String REST_URL = "/places";

    static final List<String> EXCLUDED_FIELDS = new LinkedList<>(
            Arrays.asList("version", "adminId", "menus")
    );

    static final List<String> INCLUDED_FIELDS = new LinkedList<>(
            Arrays.asList("id")
    );

    private final LunchPlaceService placeService;


    @Autowired
    public LunchPlaceController(final LunchPlaceService placeService, final JsonResult json) {
        super(json);
        this.placeService = placeService;
    }

    /**
     *
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
        LunchPlace place = DtoIntoEntity.toLunchPlace(placeTo, AuthorizedUser.get().getId());
        String id = placeService.create(place, AuthorizedUser.get());
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).build();
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
    public ResponseEntity<LunchPlace> get(@PathVariable String id,
                                          @RequestParam Optional<String> fields) {
        LunchPlace place = placeService.get(id, AuthorizedUser.get());
        jsonFilter(place, fields);
        return ResponseEntity.ok(place);
    }

    @GetMapping
    public ResponseEntity<Collection<LunchPlace>> getAll(@RequestParam Optional<String> fields) {
        Collection<LunchPlace> places = placeService.getAll(AuthorizedUser.get());
        jsonFilter(places, fields);
        return ResponseEntity.ok(places);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        placeService.delete(id, AuthorizedUser.get());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private void jsonFilter(Object object, Optional<String> includeFieldsString) {
        super.jsonFilter(object, includeFieldsString, INCLUDED_FIELDS, EXCLUDED_FIELDS);
    }
}
