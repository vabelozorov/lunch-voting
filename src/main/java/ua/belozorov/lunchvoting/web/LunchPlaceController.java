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
    static final String REST_URL = "/place";

    @Autowired
    private LunchPlaceService placeService;

    /**
     *
     * @param placeTo a LunchPlace object description in JSON format. <br/>
     *              Mandatory fields:
     *              <ul><li>name (up to 50 characters, not empty)</li></ul>
     *              Optional fields:
     *              <ul>
     *                  <li><b>address</b> string, up to 100 characters</li>
     *                  <li><b>description</b> string, up to 1000 characters</li>
     *                  <li><b>phones</b> array of strings, each string is 10 characters long</li>
     *              </ul>
     * @return LunchPlace object with id
     */
    @PostMapping
    public ResponseEntity<LunchPlaceTo> create(@RequestBody LunchPlaceTo placeTo) {
        LunchPlaceTo created = placeService.create(placeTo, AuthorizedUser.get());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody LunchPlaceTo placeTo) {
        placeService.update(placeTo, AuthorizedUser.get());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

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
