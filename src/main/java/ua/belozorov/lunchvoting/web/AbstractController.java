package ua.belozorov.lunchvoting.web;

import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.12.16.
 */
public abstract class AbstractController {

    final JsonResult json;

    @Autowired
    AbstractController(final JsonResult json) {
        this.json = json;
    }

    void jsonFilter(Object object, List<String> excludeFields) {
        Match match = Match.match();
        excludeFields.forEach(match::exclude);
        json.use(JsonView.with(object).onClass(LunchPlace.class, match));
    }

    void jsonFilter(Object object, Optional<String> includeFieldsString,
                              Collection<String> mandatoryInclude, Collection<String> mandatoryExclude) {
        //TODO make validation for "includeFieldsString" variable - only letters and commas are allowed
        Set<String> fields = includeFieldsString.map(s -> s.split(","))
                                .map(Arrays::asList)
                                .map(HashSet::new)
                                .orElse(new HashSet<>());
        Match match = Match.match();
        if (fields.isEmpty()) {
            mandatoryExclude.forEach(match::exclude);
        } else {
            mandatoryExclude.forEach(fields::remove);
            mandatoryInclude.forEach(fields::add);
            match.exclude("*");
            fields.forEach(match::include);
        }
        json.use(JsonView.with(object).onClass(LunchPlace.class, match));
    }
}
