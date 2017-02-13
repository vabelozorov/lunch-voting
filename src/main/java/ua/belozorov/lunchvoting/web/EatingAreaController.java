package ua.belozorov.lunchvoting.web;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.service.lunchplace.EatingAreaService;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.util.ControllerUtils.toMap;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.02.17.
 */

@RestController
@RequestMapping(EatingAreaController.REST_URL)
public class EatingAreaController {
    static final String REST_URL = "/api/areas";

    private final EatingAreaService areaService;
    private final MessageSource messageSource;
    private final JsonFilter jsonFilter;

    @Autowired
    public EatingAreaController(EatingAreaService areaService,
                                MessageSource messageSource,
                                @Qualifier("simpleJsonFilter") JsonFilter jsonFilter) {
        this.areaService = areaService;
        this.messageSource = messageSource;
        this.jsonFilter = jsonFilter;
    }

    @PostMapping
    public ResponseEntity create(@RequestParam String name) {
        EatingArea area = ExceptionUtils.unwrapException(
                () -> areaService.create(name, AuthorizedUser.get()),
                ConstraintViolationException.class,
                new DuplicateDataException(messageSource.getMessage(
                        "error.duplicate_area_name",
                        new Object[]{name},
                        LocaleContextHolder.getLocale()
                ))
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("{base}/{id}").buildAndExpand(REST_URL, area.getId()).toUri();
        return ResponseEntity.created(uri).body(toMap("id", area.getId()));
    }

    @PutMapping
    public ResponseEntity update(@RequestParam String name) {
        ExceptionUtils.unwrapException(
                () -> {areaService.update(name, AuthorizedUser.get()); return null; },
                ConstraintViolationException.class,
                new DuplicateDataException(messageSource.getMessage(
                        "error.duplicate_area_name",
                        new Object[]{name},
                        LocaleContextHolder.getLocale()
                ))
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaTo> get(@PathVariable String id, @RequestParam(defaultValue = "true") boolean summary) {
        AreaTo to = areaService.getAsTo(id, summary);
        return ResponseEntity.ok(to);
    }

    @GetMapping(value = "/filter", params = "name")
    public ResponseEntity<List<EatingArea>> filterByName(@RequestParam String name) {
        List<EatingArea> areas = areaService.filterByNameStarts(name);
        this.filterArea(areas);
        return ResponseEntity.ok(areas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        areaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void filterArea(Object obj) {
        Map<Class<?>, Set<String>> filterMap = new HashMap<>();
        filterMap.put(
                EatingArea.class, Stream.of("users", "places", "polls", "version").collect(Collectors.toSet())
        );
        jsonFilter.excludingFilter(obj, filterMap);
    }
}
