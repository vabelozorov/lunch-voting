package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping(LunchPlaceController.REST_URL)
public class LunchPlaceController {
    static final String REST_URL = "/places";

    @Autowired
    private LunchPlaceService placeService;

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
    public ResponseEntity<LunchPlaceTo> create(@RequestBody LunchPlaceTo placeTo) {
        LunchPlaceTo created = placeService.create(placeTo, AuthorizedUser.get());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
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
        placeService.update(placeTo, AuthorizedUser.get());
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
    public ResponseEntity<LunchPlaceTo> get(@PathVariable String id) {
        LunchPlaceTo placeTo = placeService.get(id, AuthorizedUser.get());
        return new ResponseEntity<>(placeTo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<LunchPlaceTo>> getAll() {
        Collection<LunchPlaceTo> places = placeService.getAll(AuthorizedUser.get());
        return new ResponseEntity<>(places, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        placeService.delete(id, AuthorizedUser.get());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
