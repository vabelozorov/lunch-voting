package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by vabelozorov on 14.11.16.
 */
@RestController
@RequestMapping(MenuController.REST_URL)
public class MenuController {
    static final String REST_URL = LunchPlaceController.REST_URL + "/{lunchPlaceId}/menus";

    @Autowired
    private LunchPlaceService service;

    @PostMapping
    public ResponseEntity create(@RequestBody MenuTo menuTo, @PathVariable String lunchPlaceId) {
        String  id= service.addMenu(lunchPlaceId, menuTo, AuthorizedUser.get());
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL + "/{lunchPlaceId}/menus/{id}")
                .buildAndExpand(lunchPlaceId, id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity delete(@PathVariable String lunchPlaceId, @PathVariable String menuId) {
        service.deleteMenu(lunchPlaceId, menuId, AuthorizedUser.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<LunchPlace> getWithMenus(@RequestParam Optional<String> fields) {
        LunchPlace place = null;
        //service.getWithMenus();
        return ResponseEntity.ok(place);
    }
}
