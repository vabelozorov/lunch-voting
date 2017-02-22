package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.net.URI;

/**
 * Created by vabelozorov on 14.11.16.
 */
@RestController
@RequestMapping(MenuController.REST_URL)
public class MenuController {
    static final String REST_URL = LunchPlaceController.REST_URL + "/{placeId}/menus";

    private final LunchPlaceService placeService;

    private final JsonFilter jsonFilter;

    @Autowired
    public MenuController(LunchPlaceService placeService,
                          @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.placeService = placeService;
        this.jsonFilter = jsonFilter;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new MenuToValidator());
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity create(@PathVariable String placeId, @RequestBody @Validated MenuTo menuTo) {
        String areaId = AuthorizedUser.get().getAreaId();
        Menu created= placeService.addMenu(areaId, placeId, menuTo.getEffectiveDate(), menuTo.getDishes());
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL + "/{id}")
                .buildAndExpand(areaId, placeId, created.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    @IsAdminOrVoter
    public ResponseEntity<MenuTo> get(@PathVariable String placeId, @PathVariable("id") String menuId) {
        String areaId = AuthorizedUser.get().getAreaId();
        Menu menu = placeService.getMenu(areaId, placeId, menuId, MenuRepositoryImpl.Fields.DISHES);
        MenuTo to = new MenuTo(menu.getId(), menu.getEffectiveDate(), menu.getDishes(), placeId);
        return ResponseEntity.ok(to);
    }

    @DeleteMapping("/{menuId}")
    @IsAdmin
    public ResponseEntity delete(@PathVariable String placeId, @PathVariable String menuId) {
        placeService.deleteMenu(AuthorizedUser.get().getAreaId(), placeId, menuId);
        return ResponseEntity.noContent().build();
    }

    private class MenuToValidator implements Validator {

        private Validator dishValidator = new DishValidator();

        @Override
        public boolean supports(Class<?> clazz) {
            return MenuTo.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            MenuTo menuTo = (MenuTo) target;
            if (menuTo.getEffectiveDate() == null && menuTo.getDishes() == null) {
                errors.reject("error.model.menu.fields");
            }
            int i = 0;
            for (Dish dish : menuTo.getDishes()) {
                try {
                    errors.pushNestedPath("dishes[" + i + "]");
                    ValidationUtils.invokeValidator(dishValidator, dish, errors);
                } finally {
                    errors.popNestedPath();
                }
                i++;
            }
        }
    }

    private class DishValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return Dish.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            Dish dish = (Dish) target;
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.model.dish.name_invalid");
            if (dish.getPrice() < 0) {
                errors.rejectValue("price", "error.model.dish.price_below_zero");
            }
            if (dish.getPosition() < 0) {
                errors.rejectValue("position", "error.model.dish.position_below_zero");
            }
        }
    }
}
