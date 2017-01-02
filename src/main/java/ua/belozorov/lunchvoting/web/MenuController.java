package ua.belozorov.lunchvoting.web;

import com.monitorjbl.json.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by vabelozorov on 14.11.16.
 */
@RestController
@RequestMapping(MenuController.REST_URL)
public class MenuController {
    static final String REST_URL = LunchPlaceController.REST_URL;

    static final List<String> EXCLUDED_FIELDS = new ArrayList<>(
            Arrays.asList("version", "adminId")
    );

    static final List<String> INCLUDED_FIELDS = new ArrayList<>(
            Arrays.asList("id")
    );

    private final LunchPlaceService placeService;

    @Autowired
    MenuController(LunchPlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping(value = "/{lunchPlaceId}/menus")
    public ResponseEntity create(@RequestBody MenuTo menuTo, @PathVariable String lunchPlaceId) {
        String  id= placeService.addMenu(lunchPlaceId, menuTo, AuthorizedUser.get());
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL + "/{lunchPlaceId}/menus/{id}")
                .buildAndExpand(lunchPlaceId, id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{lunchPlaceId}/menus/{menuId}")
    public ResponseEntity delete(@PathVariable String lunchPlaceId, @PathVariable String menuId) {
        placeService.deleteMenu(lunchPlaceId, menuId, AuthorizedUser.get());
        return ResponseEntity.noContent().build();
    }
}
