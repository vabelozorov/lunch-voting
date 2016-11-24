package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.ILunchPlaceService;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@RestController
@RequestMapping("/place")
public class LunchPlaceController {

    @Autowired
    private ILunchPlaceService placeService;

    /**
     *
     * @param place a LunchPlace object description in JSON format. <br/>
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
    public ResponseEntity<LunchPlace> create(@RequestBody LunchPlace place) {
        LunchPlace created = placeService.create(place, AuthorizedUser.adminId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody LunchPlace place) {
        placeService.update(place, AuthorizedUser.adminId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LunchPlace> get(@PathVariable String id) {
        LunchPlace place = placeService.get(id, AuthorizedUser.adminId());
        return new ResponseEntity<>(place, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<LunchPlace>> getAll() {
        Collection<LunchPlace> places = placeService.getAll(AuthorizedUser.adminId());
        return new ResponseEntity<>(places, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        placeService.delete(id, AuthorizedUser.adminId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
