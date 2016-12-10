package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;

/**
 * Created by vabelozorov on 14.11.16.
 */
@RestController
@RequestMapping(MenuController.REST_URL)
public class MenuController {
    static final String REST_URL = LunchPlaceController.REST_URL;

    @Autowired
    private LunchPlaceService service;

    @PostMapping("/{lunchPlaceId}/menus")
    public ResponseEntity<MenuTo> create(@RequestBody MenuTo menuTo, @PathVariable String lunchPlaceId) {
        MenuTo created = service.addMenu(lunchPlaceId, menuTo, AuthorizedUser.get());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{lunchPlaceId}/menus/{menuId}")
    public ResponseEntity delete(@PathVariable String lunchPlaceId, @PathVariable String menuId) {
        service.deleteMenu(lunchPlaceId, menuId, AuthorizedUser.get());
        return ResponseEntity.noContent().build();
    }
}
