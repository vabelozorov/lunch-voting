package ua.belozorov.lunchvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.to.MenuTo;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;

/**
 * A controller to manage {@link Menu} objects
 *
 * Created on 14.11.16.
 */
@RestController
@RequestMapping(MenuController.REST_URL)
public class MenuController {
    static final String REST_URL = LunchPlaceController.REST_URL + "/{placeId}/menus";

    private final LunchPlaceService placeService;

    private final JsonFilter jsonFilter;

    /**
     * A constructor
     * @param placeService an instance of a class that implements {@link LunchPlaceService} interface
     * @param jsonFilter an instance of a class that implements {@link JsonFilter} interface
     */
    @Autowired
    public MenuController(LunchPlaceService placeService,
                          @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.placeService = placeService;
        this.jsonFilter = jsonFilter;
    }

    @InitBinder("menuTo")
    void initBinder(WebDataBinder binder) {
        binder.addValidators(new MenuToValidator());
    }

    /**
     * <p>Adds a new {@link Menu} to the {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} specified by ID</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/places/{placeId}/menus 201 </code></font>
     *             <b>{areaId}</b> existing {@link ua.belozorov.lunchvoting.model.lunchplace.EatingArea} ID<br>
     *             <b>{placeId}</b> existing {@link LunchPlace} ID
     *          </td>
     *     </tr>
     *     <tr>
     *         <td>Content-Type</td>
     *         <td>{@code application/json}</td>
     *     </tr>
     *     <tr>
     *         <td>Required Request Parameters</td>
     *         <td>{@code effectiveDate<br>dishes : {name, price, position}}</td>
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
     * @param placeId an ID of an existing {@link LunchPlace} in the area of an authenticated user
     * @param menuTo represents request parameters and must contain the following fields
     *               <ul>
     *               <li><code>effectiveDate</code> date for which the created Menu will be valid</li>
     *               <li><code>dishes</code> array of Dish objects with mandatory fields
     *                  <ul>
     *                      <li><code>name</code> dish name, must be 2-50 characters long</li>
     *                      <li><code>price</code> floating value, must be >= 0 </li>
     *                      <li><code>position</code> integer value, must be >= 0 </li>
     *                  </ul>
     *                  </li>
     *               </ul>
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 201 Created</li>
     *      <li>A URL to access the created object in HTTP Location Header</li>
     *      <li>A JSON object with a field {@code id} containing the ID of the newly created {@code Menu}</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
     *      <li>HTTP Status 404 Not_Found, if {@code placeId} refers to a non-existent object
     *      in the area of an authenticated user</li>
     *  </ul>
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @IsAdmin
    public ResponseEntity create(@PathVariable String placeId, @RequestBody @Valid MenuTo menuTo) {
        String areaId = AuthorizedUser.get().getAreaId();
        Menu created = placeService.addMenu(areaId, placeId, menuTo.getEffectiveDate(), menuTo.getDishes());
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL + "/{id}")
                .buildAndExpand(areaId, placeId, created.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * <p>Returns a Menu with a given ID.</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/places/{placeId}/menus/{menuId} 200</code></font>
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
     *         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     *     </tr>
     * </table>
     *
     * @param placeId an ID of an existing {@link LunchPlace} in the area of an authenticated user
     * @param menuId an ID of an existing {@link Menu} in the area of an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 200 Ok</li>
     *      <li>a JSON object with fields {@code id, effectiveDate, lunchPlaceId, dishes}</li>
     *      <li>{@code dishes} is a JSON array of Dish object with fields {@code name, price, position}
     *      </li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found, if {@code placeId} or {@code menuId} refers to a non-existent object
     *      in the area of an authenticated user</li>
     *  </ul>
     */
    @GetMapping("/{id}")
    @IsAdminOrVoter
    public ResponseEntity<MenuTo> get(@PathVariable String placeId, @PathVariable("id") String menuId) {
        String areaId = AuthorizedUser.get().getAreaId();
        Menu menu = placeService.getMenu(areaId, placeId, menuId, MenuRepositoryImpl.Fields.DISHES);
        MenuTo to = new MenuTo(menu.getId(), menu.getEffectiveDate(), menu.getDishes(), placeId);
        return ResponseEntity.ok(to);
    }

    /**
     * <p>Deletes a Menu by its ID</p>
     *
     * <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     *     <tr>
     *         <td>HTTP Request</td>
     *         <td><font style="color:green"><code>HTTP DELETE  /api/areas/{areaId}/places/{placeId}/menus/{menuId} 204</code></font>
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
     * @param placeId an ID of an existing {@link LunchPlace} in the area of an authenticated user
     * @param menuId an ID of an existing {@link Menu} in the area of an authenticated user
     * @return ResponseEntity instance with the following values upon success:
     * <ul>
     *      <li>HTTP Status 204 No_Content</li>
     * </ul>
     *  If the request fails:
     *  <ul>
     *      <li>HTTP Status 404 Not_Found, if {@code placeId} or {@code menuId} refers to a non-existent object
     *      in the area of an authenticated user</li>
     *  </ul>
     */
    @DeleteMapping("/{menuId}")
    @IsAdmin
    public ResponseEntity delete(@PathVariable String placeId, @PathVariable String menuId) {
        placeService.deleteMenu(AuthorizedUser.get().getAreaId(), placeId, menuId);
        return ResponseEntity.noContent().build();
    }

    /*
        Validates MenuTo and enclosed Dish objects
     */
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
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.model.dish.name_length_invalid");
            if (dish.getName().length() < 2 || dish.getName().length() > 50) {
                errors.rejectValue("name", "error.model.dish.name_length_invalid");
            }
            if (dish.getPrice() < 0) {
                errors.rejectValue("price", "error.model.dish.price_below_zero");
            }
            if (dish.getPosition() < 0) {
                errors.rejectValue("position", "error.model.dish.position_below_zero");
            }
        }
    }
}
